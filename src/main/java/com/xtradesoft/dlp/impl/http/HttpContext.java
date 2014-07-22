/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl.http;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xtradesoft.dlp.base.Context;
import com.xtradesoft.dlp.impl.DLPException;
import com.xtradesoft.dlp.loggable.annotation.Loggable;

/**
 * The Class HttpContext.
 */
public class HttpContext extends Context {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpContext.class);

    /** The executor. */
    private Executor executor;

    /** The host. */
    private String host = "";

    /** The port. */
    private int port = -1;

    /** The service context. */
    private String serviceContext = "";

    /**
     * Instantiates a new HttpContext.
     */
    public HttpContext() {

    }

    /**
     * Gets the executor.
     * 
     * @return the executor
     */
    public Executor getExecutor() {

        return executor;
    }

    /**
     * Gets the host.
     * 
     * @return the host
     */
    public String getHost() {

        return host;
    }

    /**
     * Gets the port.
     * 
     * @return the port
     */
    public int getPort() {

        return port;
    }

    /**
     * Gets the service context.
     * 
     * @return the service context
     */
    public String getServiceContext() {

        return serviceContext;
    }

    /**
     * Checks for host.
     * 
     * @return true, if successful
     */
    boolean hasHost() {

        return !host.isEmpty();
    }

    /**
     * Initialize.
     * 
     * @return the http context
     * @throws DLPException
     *             the DLP exception
     */
    @Loggable(level = Loggable.Level.Debug)
    public HttpContext initialize() throws DLPException {

        if (null != serviceContext && !serviceContext.isEmpty() && !serviceContext.startsWith("/")) {
            serviceContext = "/" + serviceContext;
        }

        LOGGER.debug("initilaized, serviceContext: " + serviceContext);

        return this;
    }

    /**
     * Sets the executor.
     * 
     * @param executor
     *            the new executor
     */
    public void setExecutor(Executor executor) {

        this.executor = executor;
    }

    /**
     * Sets the host.
     * 
     * @param host
     *            the new host
     */
    public void setHost(String host) {

        this.host = host;
    }

    /**
     * Sets the port.
     * 
     * @param port
     *            the new port
     */
    public void setPort(int port) {

        this.port = port;
    }

    /**
     * Sets the service context.
     * 
     * @param serviceContext
     *            the new service context
     */
    public void setServiceContext(String serviceContext) {

        this.serviceContext = serviceContext;
    }

}
