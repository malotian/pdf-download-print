/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xtradesoft.dlp.base.Configuration;
import com.xtradesoft.dlp.base.Context;
import com.xtradesoft.dlp.base.ContextProvider;
import com.xtradesoft.dlp.base.OperationExecutor;
import com.xtradesoft.dlp.base.tablemvc.Model;
import com.xtradesoft.dlp.base.tablemvc.Rows;
import com.xtradesoft.dlp.impl.download.DownloadContext;
import com.xtradesoft.dlp.impl.download.DownloadOperation;
import com.xtradesoft.dlp.impl.http.HttpContext;
import com.xtradesoft.dlp.impl.http.HttpStartOperation;
import com.xtradesoft.dlp.impl.print.PrintContext;
import com.xtradesoft.dlp.impl.print.PrintOperation;
import com.xtradesoft.dlp.loggable.annotation.Loggable;

/**
 * The Class DLPContextProvider.
 */
@Loggable(level = Loggable.Level.Debug)
public class DLPContextProvider extends ContextProvider {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DLPContextProvider.class);

    /** The configuration. */
    private DLPConfiguration configuration;

    /** The downloadbles. */
    private Map<URI, DLPConfiguration.Record> downloadbles;

    /*
     * (non-Javadoc)
     * @see com.xtradesoft.dlp.base.ContextProvider#getContext()
     */
    @Override
    public Context getContext() throws Exception {

        return null;
    }

    /**
     * Gets the context.
     * 
     * @param hint
     *            the hint
     * @return the context
     * @throws Exception
     *             the exception
     */
    public Context getContext(DownloadOperation hint) throws Exception {

        final URL url = hint.getURL();
        final DownloadContext context = new DownloadContext(url);
        final DLPConfiguration.Record record = lookup(hint.lookupURL());

        LOGGER.debug("URL: {}", url);
        LOGGER.debug("HttpUsername: {}", record.HttpUsername());
        LOGGER.debug("HttpPassword: {}", record.HttpPassword());
        LOGGER.debug("AppLoginPostURL: {}", record.hasAppLoginPostURL() ? record.AppLoginPostURL() : "");
        LOGGER.debug("AppUsername: {}", record.AppUsername());
        LOGGER.debug("AppPassword: {}", record.AppPassword());
        LOGGER.debug("ProxyHost: {}", record.ProxyHost());
        LOGGER.debug("ProxyPort: {}", record.hasProxyPort() ? record.ProxyPort() : "");
        LOGGER.debug("MaxBackup: {}", record.hasMaxBackup() ? record.MaxBackup() : "");

        context.setHttpCredentails(new UsernamePasswordCredentials(record.HttpUsername(), record.HttpPassword()));

        if (record.hasAppLoginPostURL()) {
            context.setApplicationLoginURL(record.AppLoginPostURL());

            context.setApplicationCredentials(new UsernamePasswordCredentials(record.AppUsername(), record
                    .AppPassword()));
        }
        context.setMaxBackUps(record.MaxBackup());

        if (!record.ProxyHost().isEmpty()) {
            if (record.ProxyPort() > 0) {
                context.setProxy(new HttpHost(record.ProxyHost(), record.ProxyPort()));
            } else {
                context.setProxy(new HttpHost(record.ProxyHost()));
            }
        }

        try {
            return context.initialize();
        } catch (final URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Gets the context.
     * 
     * @param hint
     *            the hint
     * @param operationExecutor
     *            the operation executor
     * @return the context
     * @throws Exception
     *             the exception
     */
    public Context getContext(HttpStartOperation hint, OperationExecutor operationExecutor) throws Exception {

        final HttpContext context = new HttpContext();
        context.setHost(configuration.getHttpServerHost());
        context.setPort(configuration.getHttpServerPort());
        context.setServiceContext(configuration.getHttpServiceContext());
        context.setExecutor(operationExecutor.executor());
        return context.initialize();
    }

    /*
     * (non-Javadoc)
     * @see com.xtradesoft.dlp.base.ContextProvider#getContext(java.lang.Object)
     */
    @Override
    public Context getContext(Object[] hint) throws Exception {

        LOGGER.debug("hint: {}", hint[0].getClass());
        if (hint[0] instanceof DownloadOperation) {
            return getContext((DownloadOperation) hint[0]);
        } else if (hint[0] instanceof PrintOperation) {
            return getContext((PrintOperation) hint[0]);
        } else if (hint[0] instanceof HttpStartOperation) {
            return getContext((HttpStartOperation) hint[0], (OperationExecutor) hint[1]);
        }
        return null;
    }

    /**
     * Gets the context.
     * 
     * @param hint
     *            the hint
     * @return the context
     * @throws Exception
     *             the exception
     */
    public Context getContext(PrintOperation hint) throws Exception {

        final URL url = hint.getURL();
        final PrintContext context = new PrintContext(url);
        final DLPConfiguration.Record record = lookup(hint.lookupURL());

        LOGGER.debug("URL: {}, file: {}", url, hint.getFile());
        LOGGER.debug("Printer: {}", record.Printer());
        context.setPrinter(record.Printer());
        return context.initialize();

    }

    /*
     * (non-Javadoc)
     * @see
     * com.xtradesoft.dlp.base.ContextProvider#initialize(com.xtradesoft.dlp
     * .base.Configuration)
     */
    @Override
    public void initialize(Configuration configuration) {

        this.configuration = (DLPConfiguration) configuration;
        final Rows<Model> rows = new Rows<Model>(this.configuration.getModel());

        final Map<URI, DLPConfiguration.Record> downloadables = new HashMap<URI, DLPConfiguration.Record>();
        while (rows.hasNext()) {
            final DLPConfiguration.Record record = new DLPConfiguration.Record(rows.next());
            LOGGER.debug("preliminary validation, configuration entry: {}", record);
            if (!record.validate()) {
                LOGGER.warn("failed, preliminary validation - ignoring configuration entry {}", record);
                continue;
            }
            LOGGER.debug("success, preliminary validatiion - configuration entry: {}", record);

            final URI downloadable = removeQueryString(record.DownloadURL());

            if (null != record.DownloadURL().getQuery()) {
                LOGGER.warn("warning, removed query string: {} for lookup, from URL {}", record.DownloadURL()
                        .getQuery(), record.DownloadURL());
            }

            downloadables.put(downloadable, record);
            LOGGER.debug("configured, downloadable: {}", downloadable);

        }

        LOGGER.info("configured, downloadables count: {}", downloadables.size());

        if (downloadables.isEmpty()) {
            LOGGER.warn("warning, no downloadable configured...");
        }

        downloadbles = Collections.synchronizedMap(downloadables);
    }

    /**
     * Lookup.
     * 
     * @param url
     *            the url
     * @return the DLP configuration. record
     * @throws Exception
     *             the exception
     */
    DLPConfiguration.Record lookup(URL url) throws Exception {

        if (null != url.getQuery()) {
            LOGGER.warn("warning, removed query string: {} while lookup, from URL {}", url.getQuery(), url);
        }

        final URI downloadable = removeQueryString(url);
        final DLPConfiguration.Record record = downloadbles.get(downloadable);

        if (null == record) {
            LOGGER.error("{}, is not configured", downloadable);
            throw new IllegalArgumentException(String.format("%s is not configured", downloadable));
        }
        return record;
    }

    /**
     * Removes the query string.
     * 
     * @param input
     *            the input
     * @return the uri
     */
    private URI removeQueryString(URL input) {

        try {
            return new URIBuilder(input.toURI()).removeQuery().build();
        } catch (final URISyntaxException e) {
            LOGGER.warn("error, invalid url/uri: {}, exception: {}", input, e);
        }
        return null;
    }
}
