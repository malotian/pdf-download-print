/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xtradesoft.dlp.loggable.annotation.Loggable;

/**
 * The Class OperationExecutor.
 */
@Loggable(level = Loggable.Level.Debug)
public abstract class OperationExecutor {

    /**
     * The Class CallableHelper.
     */
    private class CallableHelper implements Callable<OperationResult> {

        /** The operation. */
        private final Operation operation;

        /** The operation executor. */
        private final OperationExecutor operationExecutor;

        /**
         * Instantiates a new CallableHelper.
         * 
         * @param operationExecutor
         *            the operation executor
         * @param operation
         *            the operation
         */
        public CallableHelper(OperationExecutor operationExecutor, Operation operation) {

            this.operationExecutor = operationExecutor;
            this.operation = operation;
        }

        /*
         * (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public OperationResult call() throws Exception {

            OperationResult result = null;
            try {
                result = operationExecutor.execute(operation);
            } catch (final Exception e) {
                LOGGER.error("failure, executing: {}, exception: {}", operation, e);
                result = new OperationResult(new Error(e));
            } finally {
                result.set(operation);
            }
            return result;
        }
    }

    /**
     * The Class Schedulable.
     */
    private class Schedulable implements Runnable {

        /** The operation. */
        private final Operation operation;

        /**
         * Instantiates a new Schedulable.
         * 
         * @param operation
         *            the operation
         */
        public Schedulable(Operation operation) {

            this.operation = operation;

        }

        /*
         * (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {

            submit(operation);

        }

    }

    /**
     * The Class Watchable.
     */
    private static class Watchable {

        /** The future operation result. */
        final FutureOperationResult futureOperationResult;

        /** The shall watch. */
        boolean shallWatch;

        /**
         * Instantiates a new Watchable.
         * 
         * @param futureOperationResult
         *            the future operation result
         */
        public Watchable(FutureOperationResult futureOperationResult) {

            this.futureOperationResult = futureOperationResult;
            shallWatch = true;
        }

        /**
         * Do not watch.
         */
        public void doNotWatch() {

            shallWatch = false;
        }

        /**
         * Future.
         * 
         * @return the future
         */
        public Future<OperationResult> future() {

            return futureOperationResult.future();
        }

        /**
         * Shall watch.
         * 
         * @return true, if successful
         */
        public boolean shallWatch() {

            return shallWatch;
        }
    }

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OperationExecutor.class);

    /** The context provider. */
    private ContextProvider contextProvider;

    /** The executor service. */
    ScheduledExecutorService executorService;

    /** The polling interval. */
    private long pollingInterval;

    /** The watchables. */
    private List<Watchable> watchables;

    /** The watcher. */
    private ScheduledFuture<?> watcher;

    /** The watch executor. */
    ScheduledExecutorService watchExecutor;

    public Executor executor() {

        return executorService;
    }

    /**
     * Instantiates a new OperationExecutor.
     */
    public OperationExecutor() {

        executorService = Executors.newScheduledThreadPool(1);
        watchExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {

            @Override
            public Thread newThread(Runnable runnable) {

                final Thread t = new Thread(runnable);
                t.setName("watch-thread");
                t.setDaemon(true);
                return t;
            }
        });

        watchables = new CopyOnWriteArrayList<Watchable>();
        pollingInterval = 200L;
        startPolling();
    }

    /**
     * Cancel all.
     */
    public void cancelAll() {

        cancelAll(false);
    }

    /**
     * Cancel all.
     * 
     * @param force
     *            the force
     */
    public void cancelAll(boolean force) {

        for (final Watchable watchable : watchables) {
            watchable.future().cancel(force);
        }
    }

    /**
     * Execute.
     * 
     * @param operation
     *            the operation
     * @return the operation result
     * @throws Exception
     *             the exception
     */
    public abstract OperationResult execute(Operation operation) throws Exception;

    /**
     * Gets the contextprovider.
     * 
     * @return the contextprovider
     */
    public ContextProvider getContextprovider() {

        return contextProvider;
    }

    /**
     * Gets the polling interval.
     * 
     * @return the polling interval
     */
    public long getPollingInterval() {

        return pollingInterval;
    }

    /**
     * Checks if is shutdown.
     * 
     * @return true, if is shutdown
     */
    public boolean isShutdown() {

        return executorService.isShutdown();
    }

    /**
     * Perform.
     * 
     * @param operation
     *            the operation
     * @return the operation result
     */
    public OperationResult perform(Operation operation) {

        try {
            return performEx(operation);
        } catch (final Exception e) {
            LOGGER.error("failure, perform: {}, exception {}", operation, e);
            return new OperationResult(new Error(e));
        }
    }

    /**
     * Perform ex.
     * 
     * @param operation
     *            the operation
     * @return the operation result
     * @throws InterruptedException
     *             the interrupted exception
     * @throws ExecutionException
     *             the execution exception
     */
    private OperationResult performEx(Operation operation) throws InterruptedException, ExecutionException {

        final FutureOperationResult futureResult = submitEx(operation);
        return futureResult.future().get();
    }

    /**
     * Poll task result.
     */
    private void pollTaskResult() {

        for (final Watchable watchable : watchables) {

            if (!watchable.shallWatch() || !watchable.future().isDone()) {
                continue;
            }

            watchable.doNotWatch();

            OperationResult result = null;
            try {
                result = watchable.future().get();
            } catch (final CancellationException ex) {
                LOGGER.error("failure, cancelled {}", ex);
            } catch (final Exception ex) {
                LOGGER.error("failure, pollTaskResult {} ", ex);
            }

            if (result.hasGotError()) {
                LOGGER.error("failure, {}", result);
            } else {
                LOGGER.debug("success, completed, {}", result);
            }
        }

    }

    /**
     * Register.
     * 
     * @param contextProvider
     *            the context provider
     */
    public void register(ContextProvider contextProvider) {

        this.contextProvider = contextProvider;
    }

    /**
     * Restart polling.
     */
    private void restartPolling() {

        stopPolling();
        startPolling();
    }

    /**
     * Schedule.
     * 
     * @param operation
     *            the operation
     * @param everySeconds
     *            the every seconds
     * @return the schedule result
     */
    public ScheduleResult schedule(Operation operation, int everySeconds) {

        try {
            return scheduleEx(operation, everySeconds);
        } catch (final Exception e) {
            LOGGER.error("failure, schedule: {}, exception {}", operation, e);
            return new ScheduleResult(new Error(e));
        }
    }

    /**
     * Schedule ex.
     * 
     * @param operation
     *            the operation
     * @param everySeconds
     *            the every seconds
     * @return the schedule result
     * @throws InterruptedException
     *             the interrupted exception
     * @throws ExecutionException
     *             the execution exception
     */
    public ScheduleResult scheduleEx(Operation operation, int everySeconds) throws InterruptedException,
            ExecutionException {

        final ScheduledFuture<?> result = executorService.scheduleAtFixedRate(new Schedulable(operation), 0,
                everySeconds, TimeUnit.SECONDS);

        return new ScheduleResult(result);
    }

    /**
     * Sets the polling interval.
     * 
     * @param interval
     *            the new polling interval
     */
    public void setPollingInterval(long interval) {

        if (interval > 0 && interval != pollingInterval) {
            pollingInterval = interval;
            restartPolling();
        }
    }

    /**
     * Shutdown.
     */
    public void shutdown() {

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            watchExecutor.shutdown();
        }
    }

    /**
     * Start polling.
     */
    private void startPolling() {

        watcher = watchExecutor.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {

                pollTaskResult();
            }
        }, pollingInterval, pollingInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * Stop polling.
     */
    private void stopPolling() {

        if (watcher != null && !watcher.isDone()) {
            watcher.cancel(false);
        }
    }

    /**
     * Submit.
     * 
     * @param operation
     *            the operation
     * @return the future operation result
     */
    public FutureOperationResult submit(Operation operation) {

        try {
            return submitEx(operation);
        } catch (final Exception e) {
            LOGGER.error("failure, submit: {}, exception {}", operation, e);
            return new FutureOperationResult(new Error(e));
        }
    }

    /**
     * Submit ex.
     * 
     * @param operation
     *            the operation
     * @return the future operation result
     * @throws InterruptedException
     *             the interrupted exception
     * @throws ExecutionException
     *             the execution exception
     */
    private FutureOperationResult submitEx(Operation operation) throws InterruptedException, ExecutionException {

        final Future<OperationResult> result = executorService.submit(new CallableHelper(this, operation));
        final FutureOperationResult futureResult = new FutureOperationResult(result);
        futureResult.set(operation);
        watchables.add(new Watchable(futureResult));
        return futureResult;
    }
}
