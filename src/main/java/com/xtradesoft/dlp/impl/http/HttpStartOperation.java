/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl.http;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpServer;
import com.xtradesoft.dlp.base.Context;
import com.xtradesoft.dlp.base.Operation;
import com.xtradesoft.dlp.base.OperationResult;

/**
 * The Class HttpStartOperation.
 */
public class HttpStartOperation extends Operation {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpStartOperation.class);

    /** The handler. */
    private HttpRequestHandler handler;

    /**
     * Instantiates a new HttpStartOperation.
     * 
     * @param observer
     *            the observer
     */
    public HttpStartOperation(HttpObserver observer) {

        register(new HttpRequestHandler());
        handler.register(observer);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.xtradesoft.dlp.base.Operation#execute(com.xtradesoft.dlp.base.Context
     * )
     */
    @Override
    public OperationResult execute(Context context) throws Exception {

        final HttpContext httpContext = (HttpContext) context;

        LOGGER.debug("start http server...");
        LOGGER.debug("http configuration, host: {}, port: {}, context: {}", httpContext.getHost(),
                httpContext.getPort(), httpContext.getServiceContext());

        InetSocketAddress listenAt = null;

        if (httpContext.hasHost()) {
            listenAt = new InetSocketAddress(httpContext.getHost(), httpContext.getPort());
        } else {
            listenAt = new InetSocketAddress(httpContext.getPort());
        }

        final HttpServer server = HttpServer.create(listenAt, 0);
        server.createContext(httpContext.getServiceContext(), handler);
        server.setExecutor(httpContext.getExecutor());
        server.start();

        LOGGER.info("success, http server(service) started successfully, at http://{}{} ", listenAt,
                httpContext.getServiceContext());

        return new HttpStartOperationResult(true, server);

    }

    /**
     * Register.
     * 
     * @param handler
     *            the handler
     */
    public void register(HttpRequestHandler handler) {

        this.handler = handler;
    }

    /*
     * (non-Javadoc)
     * @see com.xtradesoft.dlp.base.Operation#toString()
     */
    @Override
    public String toString() {

        return String.format("HttpStartOperation: {%s}", super.toString());

    }
}
