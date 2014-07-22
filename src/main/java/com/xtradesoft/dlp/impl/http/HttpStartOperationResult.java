/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl.http;

import com.sun.net.httpserver.HttpServer;
import com.xtradesoft.dlp.base.OperationResult;

/**
 * The Class HttpStartOperationResult.
 */
public class HttpStartOperationResult extends OperationResult {

    /** The started. */
    boolean started;

    final HttpServer server;

    /**
     * Instantiates a new HttpStartOperationResult.
     * 
     * @param started
     *            the started
     */
    HttpStartOperationResult(boolean started, HttpServer server) {

        this.server = server;
        this.started = started;
    }

}
