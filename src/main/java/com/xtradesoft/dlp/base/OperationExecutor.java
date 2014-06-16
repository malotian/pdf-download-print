/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
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

    /**
     * The operation.
     */
    private final Operation _operation;

    /**
     * The operation executor.
     */
    private final OperationExecutor _operationExecutor;

    /**
     * Instantiates a new CallableHelper.
     *
     * @param operationExecutor the operation executor
     * @param operation         the operation
     */
    public CallableHelper(OperationExecutor operationExecutor, Operation operation) {

      _operationExecutor = operationExecutor;
      _operation = operation;
    }

    /*
     * (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public OperationResult call() throws Exception {

      OperationResult result = null;
      try {
        result = _operationExecutor.execute(_operation);
      } catch (final Exception e) {
        logger.error("failure, executing: {}, exception: {}", _operation, e);
        result = new OperationResult(new Error(e));
      } finally {
        result.set(_operation);
      }
      return result;
    }
  }

  /**
   * The Class Schedulable.
   */
  private class Schedulable implements Runnable {

    /**
     * The operation.
     */
    private final Operation _operation;

    /**
     * Instantiates a new Schedulable.
     *
     * @param operation the operation
     */
    public Schedulable(Operation operation) {

      _operation = operation;

    }

    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

      submit(_operation);

    }

  }

  /**
   * The Class Watchable.
   */
  private static class Watchable {

    /**
     * The future operation result.
     */
    final FutureOperationResult _futureOperationResult;

    /**
     * The shall watch.
     */
    boolean _shallWatch;

    /**
     * Instantiates a new Watchable.
     *
     * @param futureOperationResult the future operation result
     */
    public Watchable(FutureOperationResult futureOperationResult) {

      _futureOperationResult = futureOperationResult;
      _shallWatch = true;
    }

    /**
     * Do not watch.
     */
    public void doNotWatch() {

      _shallWatch = false;
    }

    /**
     * Future.
     *
     * @return the future
     */
    public Future<OperationResult> future() {

      return _futureOperationResult.future();
    }

    /**
     * Shall watch.
     *
     * @return true, if successful
     */
    public boolean shallWatch() {

      return _shallWatch;
    }
  }

  /**
   * The Constant logger.
   */
  final static Logger logger = LoggerFactory.getLogger(OperationExecutor.class);

  /**
   * The context provider.
   */
  private ContextProvider _contextProvider;

  /**
   * The executor service.
   */
  ScheduledExecutorService _executorService;

  /**
   * The polling interval.
   */
  private long _pollingInterval;

  /**
   * The watchables.
   */
  private CopyOnWriteArrayList<Watchable> _watchables;

  /**
   * The watch executor.
   */
  ScheduledExecutorService _watchExecutor;

  /**
   * The watcher.
   */
  private ScheduledFuture<?> watcher;

  /**
   * Instantiates a new OperationExecutor.
   */
  public OperationExecutor() {

    _executorService = Executors.newScheduledThreadPool(1);
    _watchExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {

      @Override
      public Thread newThread(Runnable runnable) {

        final Thread t = new Thread(runnable);
        t.setName("watch-thread");
        t.setDaemon(true);
        return t;
      }
    });

    _watchables = new CopyOnWriteArrayList<Watchable>();
    _pollingInterval = 200L;
    startPolling();
  }

  /**
   * _perform.
   *
   * @param operation the operation
   * @return the operation result
   * @throws InterruptedException the interrupted exception
   * @throws ExecutionException   the execution exception
   */
  private OperationResult _perform(Operation operation) throws InterruptedException, ExecutionException {

    final FutureOperationResult futureResult = _submit(operation);
    return futureResult.future().get();
  }

  /**
   * _schedule.
   *
   * @param operation    the operation
   * @param everySeconds the every seconds
   * @return the schedule result
   * @throws InterruptedException the interrupted exception
   * @throws ExecutionException   the execution exception
   */
  public ScheduleResult _schedule(Operation operation, int everySeconds) throws InterruptedException,
          ExecutionException {

    final ScheduledFuture<?> result = _executorService.scheduleAtFixedRate(new Schedulable(operation), 0,
            everySeconds, TimeUnit.SECONDS);

    return new ScheduleResult(result);
  }

  /**
   * _submit.
   *
   * @param operation the operation
   * @return the future operation result
   * @throws InterruptedException the interrupted exception
   * @throws ExecutionException   the execution exception
   */
  private FutureOperationResult _submit(Operation operation) throws InterruptedException, ExecutionException {

    final Future<OperationResult> result = _executorService.submit(new CallableHelper(this, operation));
    final FutureOperationResult futureResult = new FutureOperationResult(result);
    futureResult.set(operation);
    _watchables.add(new Watchable(futureResult));
    return futureResult;
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
   * @param force the force
   */
  public void cancelAll(boolean force) {

    for (final Watchable watchable : _watchables) {
      watchable.future().cancel(force);
    }
  }

  /**
   * Execute.
   *
   * @param operation the operation
   * @return the operation result
   * @throws Exception the exception
   */
  public abstract OperationResult execute(Operation operation) throws Exception;

  /**
   * Gets the contextprovider.
   *
   * @return the contextprovider
   */
  public ContextProvider getContextprovider() {

    return _contextProvider;
  }

  /**
   * Gets the polling interval.
   *
   * @return the polling interval
   */
  public long getPollingInterval() {

    return _pollingInterval;
  }

  /**
   * Checks if is shutdown.
   *
   * @return true, if is shutdown
   */
  public boolean isShutdown() {

    return _executorService.isShutdown();
  }

  /**
   * Perform.
   *
   * @param operation the operation
   * @return the operation result
   */
  public OperationResult perform(Operation operation) {

    try {
      return _perform(operation);
    } catch (final Exception e) {
      logger.error("failure, perform: {}, exception {}", operation, e);
      return new OperationResult(new Error(e));
    }
  }

  /**
   * Poll task result.
   */
  private void pollTaskResult() {

    for (final Watchable watchable : _watchables) {

      if (!watchable.shallWatch() || !watchable.future().isDone()) {
        continue;
      }

      watchable.doNotWatch();

      OperationResult result = null;
      try {
        result = watchable.future().get();
      } catch (final CancellationException ex) {
        logger.error("failure, cancelled {}", ex);
      } catch (final Exception ex) {
        logger.error("failure, pollTaskResult {} ", ex);
      }

      if (result.hasGotError()) {
        logger.error("failure, {}", result);
      } else {
        logger.debug("success, completed, {}", result);
      }
    }

  }

  /**
   * Register.
   *
   * @param contextProvider the context provider
   */
  public void register(ContextProvider contextProvider) {

    _contextProvider = contextProvider;
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
   * @param operation    the operation
   * @param everySeconds the every seconds
   * @return the schedule result
   */
  public ScheduleResult schedule(Operation operation, int everySeconds) {

    try {
      return _schedule(operation, everySeconds);
    } catch (final Exception e) {
      logger.error("failure, schedule: {}, exception {}", operation, e);
      return new ScheduleResult(new Error(e));
    }
  }

  /**
   * Sets the polling interval.
   *
   * @param interval the new polling interval
   */
  public void setPollingInterval(long interval) {

    if ((interval > 0) && (interval != _pollingInterval)) {
      _pollingInterval = interval;
      restartPolling();
    }
  }

  /**
   * Shutdown.
   */
  public void shutdown() {

    if ((_executorService != null) && !_executorService.isShutdown()) {
      _executorService.shutdown();
      _watchExecutor.shutdown();
    }
  }

  /**
   * Start polling.
   */
  private void startPolling() {

    watcher = _watchExecutor.scheduleAtFixedRate(new Runnable() {

      @Override
      public void run() {

        pollTaskResult();
      }
    }, _pollingInterval, _pollingInterval, TimeUnit.MILLISECONDS);
  }

  /**
   * Stop polling.
   */
  private void stopPolling() {

    if ((watcher != null) && !watcher.isDone()) {
      watcher.cancel(false);
    }
  }

  /**
   * Submit.
   *
   * @param operation the operation
   * @return the future operation result
   */
  public FutureOperationResult submit(Operation operation) {

    try {
      return _submit(operation);
    } catch (final Exception e) {
      logger.error("failure, submit: {}, exception {}", operation, e);
      return new FutureOperationResult(new Error(e));
    }
  }
}
