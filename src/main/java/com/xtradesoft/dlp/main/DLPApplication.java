/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xtradesoft.dlp.base.Configuration;
import com.xtradesoft.dlp.base.ConfigurationObserver;
import com.xtradesoft.dlp.base.FutureOperationResult;
import com.xtradesoft.dlp.base.FutureOperationResults;
import com.xtradesoft.dlp.base.Operation;
import com.xtradesoft.dlp.base.ScheduleResult;
import com.xtradesoft.dlp.base.ScheduleResults;
import com.xtradesoft.dlp.base.tablemvc.Controller;
import com.xtradesoft.dlp.base.tablemvc.Model;
import com.xtradesoft.dlp.base.tablemvc.Rows;
import com.xtradesoft.dlp.base.tablemvc.View;
import com.xtradesoft.dlp.impl.DLPConfiguration;
import com.xtradesoft.dlp.impl.DLPContextProvider;
import com.xtradesoft.dlp.impl.DLPOperationExecutor;
import com.xtradesoft.dlp.impl.download.DowmloadOperationResult;
import com.xtradesoft.dlp.impl.download.DownloadObserver;
import com.xtradesoft.dlp.impl.download.DownloadOperation;
import com.xtradesoft.dlp.impl.http.HttpObserver;
import com.xtradesoft.dlp.impl.http.HttpStartOperation;
import com.xtradesoft.dlp.impl.print.PrintOperation;
import com.xtradesoft.dlp.log.LogModel;
import com.xtradesoft.dlp.log.LogRenderer;

/**
 * The Class DLPApplication.
 */
public class DLPApplication implements ConfigurationObserver, DownloadObserver, HttpObserver {

    /**
     * The Class RunnableEx.
     */
    private static final class RunnableEx implements Runnable {

        /*
         * (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {

            final DLPApplication application = new DLPApplication();
            try {

                final DLPConfiguration configuration = new DLPConfiguration();
                configuration.initialize();

                if (configuration.validate()) {
                    application.initialize(configuration);
                    application.schedule(configuration);
                }

            } catch (final Exception e) {
                LOGGER.error("exception", e);
            }

        }
    }

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DLPApplication.class);

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {

        // overcome race condition on Mac OS X Java
        SwingUtilities.invokeLater(new RunnableEx());

    }

    /** The context provider. */
    DLPContextProvider contextProvider;

    /** The executor. */
    DLPOperationExecutor executor;

    /** The futures. */
    FutureOperationResults futures;

    /** The tasks. */
    ScheduleResults tasks;

    /**
     * Instantiates a new DLPApplication.
     */
    public DLPApplication() {

        executor = new DLPOperationExecutor();
        contextProvider = new DLPContextProvider();
        executor.register(contextProvider);
        futures = new FutureOperationResults();
    }

    /**
     * Initialize.
     * 
     * @param configuration
     *            the configuration
     * @throws Exception
     *             the exception
     */
    public void initialize(DLPConfiguration configuration) throws Exception {

        contextProvider.initialize(configuration);

        configuration.register(this);

        final View<Model> configView = new View<Model>();

        configView.initialize(configuration.getModel());
        configView.showAddDeleteSave();

        new Controller<Model>(configuration.getModel(), configView);

        final DLPMainFrame frame = new DLPMainFrame(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent w) {

                LOGGER.debug("windowClosed");
            }

            @Override
            public void windowClosing(WindowEvent w) {

                stop();
                System.exit(0);
            }
        });

        frame.addChild(configView, "config-view");

        final View<Model> logView = new View<Model>();
        logView.setRenderer(new LogRenderer());

        logView.initialize(LogModel.model());

        new Controller<Model>(LogModel.model(), logView);

        frame.addChild(logView, "log-view");

        frame.sendToTray();

        startHttp();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.xtradesoft.dlp.base.ConfigurationObserver#notify(com.xtradesoft.dlp
     * .base.Configuration)
     */
    @Override
    public void notify(Configuration configuration) {

        for (final ScheduleResult task : tasks) {
            task.future().cancel(true);
        }

        contextProvider.initialize(configuration);
        schedule((DLPConfiguration) configuration);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.xtradesoft.dlp.impl.download.DownloadObserver#notify(com.xtradesoft
     * .dlp.impl.download.DowmloadOperationResult)
     */
    @Override
    public void notify(DowmloadOperationResult result) {

        final PrintOperation operation = new PrintOperation();
        operation.setURL(result.url());
        operation.setFile(result.file());
        executor.submit(operation);
    }

    /*
     * (non-Javadoc)
     * @see com.xtradesoft.dlp.impl.http.HttpObserver#notify(java.util.Map)
     */
    @Override
    public boolean notify(Map<String, String> httpInput) {

        final String url = httpInput.get("print");
        LOGGER.info("received, download and print request for url: {}", url);
        try {
            submit(new URL(url));
        } catch (final MalformedURLException e) {
            LOGGER.error("no or invalid url: {}, error: {}", url, e);
            return false;
        }
        return true;
    }

    /**
     * Schedule.
     * 
     * @param configuration
     *            the configuration
     */
    public void schedule(DLPConfiguration configuration) {

        final ScheduleResults results = new ScheduleResults();
        final Rows<Model> rows = new Rows<Model>(configuration.getModel());

        while (rows.hasNext()) {
            final DLPConfiguration.Record record = new DLPConfiguration.Record(rows.next());

            if (!record.validate()) {
                continue;
            }

            if (record.HasPollInterval() && record.PollInterval() <= 0) {
                LOGGER.info("ignoring polling PollInterval: {} for URL: {}", record.PollInterval(),
                        record.DownloadURL());
            } else {

                final ScheduleResult result = schedule(record.DownloadURL(), record.PollInterval());
                results.add(result);
                LOGGER.info("scheduled download of URL: {} every {} seconds", record.DownloadURL(),
                        record.PollInterval());
            }

        }
        LOGGER.info("downloadables(scheduled), count: {}", results.size());

        tasks = results.createSynchronized();
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

        return executor.schedule(operation, everySeconds);
    }

    /**
     * Schedule.
     * 
     * @param url
     *            the url
     * @param everySeconds
     *            the every seconds
     * @return the schedule result
     */
    public ScheduleResult schedule(URL url, int everySeconds) {

        final DownloadOperation operation = new DownloadOperation(url);
        operation.register(this);
        return schedule(operation, everySeconds);
    }

    /**
     * Start http.
     * 
     * @return the future operation result
     */
    public FutureOperationResult startHttp() {

        return submit(new HttpStartOperation(this));
    }

    /**
     * Stop.
     */
    public void stop() {

        executor.cancelAll();
        executor.shutdown();
        executor = null;
    }

    /**
     * Submit.
     * 
     * @param operation
     *            the operation
     * @return the future operation result
     */
    public FutureOperationResult submit(Operation operation) {

        final FutureOperationResult future = executor.submit(operation);
        futures.add(future);
        return future;
    }

    /**
     * Submit.
     * 
     * @param url
     *            the url
     * @return the future operation result
     */
    public FutureOperationResult submit(URL url) {

        final DownloadOperation operation = new DownloadOperation(url);
        operation.register(this);
        return submit(operation);
    }

}
