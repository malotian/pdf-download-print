/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl.download;

import java.io.File;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xtradesoft.dlp.base.Context;
import com.xtradesoft.dlp.base.FileRoller;
import com.xtradesoft.dlp.base.OperationResult;
import com.xtradesoft.dlp.impl.DLPOperation;

/**
 * The Class DownloadOperation.
 */
public class DownloadOperation extends DLPOperation {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadOperation.class);

    /** The observer. */
    private DownloadObserver observer;

    /** The roller. */
    private final FileRoller roller;

    /**
     * Instantiates a new DownloadOperation.
     * 
     * @param url
     *            the url
     */
    public DownloadOperation(URL url) {

        setURL(url);
        roller = new FileRoller(getBaseFileName());
    }

    /*
     * (non-Javadoc)
     * @see
     * com.xtradesoft.dlp.impl.DLPOperation#execute(com.xtradesoft.dlp.base.
     * Context)
     */
    @Override
    public OperationResult execute(Context context) throws Exception {

        final DownloadContext dlContext = (DownloadContext) context;

        LOGGER.debug("application login required: {}", dlContext.applicationLoginRequired());

        if (dlContext.applicationLoginRequired()) {
            login(dlContext);
        }

        final File file = roller.roll(dlContext.getMaxBackUps());
        final DowmloadOperationResult result = new DowmloadOperationResult(dlContext.getURL(), file.getName());

        LOGGER.debug("HttpGet: {}", dlContext.httpGet());
        dlContext.getExecutor().execute(dlContext.httpGet()).saveContent(file);
        LOGGER.info("success, HttpGet: {}, Response saved to file: {}", dlContext.httpGet(), file.getAbsolutePath());

        LOGGER.debug("notify, downloaded completion - file: {}", file);
        getObserver().notify(result);
        LOGGER.debug("notified, downloaded completion - file: {}", file);

        return result;
    }

    /**
     * Gets the base file name.
     * 
     * @return the base file name
     */
    protected String getBaseFileName() {

        String baseFile = getURL().getPath().substring(getURL().getPath().lastIndexOf('/') + 1);

        if (!baseFile.endsWith(".pdf")) {
            baseFile += ".pdf";
        }

        return baseFile;
    }

    /**
     * Gets the observer.
     * 
     * @return the observer
     */
    public DownloadObserver getObserver() {

        return observer;
    }

    /**
     * Login.
     * 
     * @param dlContext
     *            the dl context
     * @return true, if successful
     * @throws Exception
     *             the exception
     */
    boolean login(DownloadContext dlContext) throws Exception {

        LOGGER.info("HttpApplicationLogin.Request: {}", dlContext.httpApplicationLogin());
        final Response response = dlContext.getExecutor().execute(dlContext.httpApplicationLogin());
        final HttpResponse httpResponse = response.returnResponse();
        LOGGER.debug("HttpApplicationLogin.Response: {}", httpResponse);
        LOGGER.debug("HttpApplicationLogin.Response, location-header: {}", httpResponse.getFirstHeader("location")
                .getValue());

        return true;
    }

    /**
     * Register.
     * 
     * @param observer
     *            the observer
     */
    public void register(DownloadObserver observer) {

        this.observer = observer;
    }

    /*
     * (non-Javadoc)
     * @see com.xtradesoft.dlp.impl.DLPOperation#toString()
     */
    @Override
    public String toString() {

        return String.format("DownloadOperation: {%s}", super.toString());

    }
}
