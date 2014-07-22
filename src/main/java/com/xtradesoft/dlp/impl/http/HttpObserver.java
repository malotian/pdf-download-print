/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl.http;

import java.util.Map;

/**
 * An asynchronous update interface for receiving notifications about Http
 * information as the Http is constructed.
 */
public interface HttpObserver {

    /**
     * This method is called when information about an Http which was previously
     * requested using an asynchronous interface becomes available.
     * 
     * @param httpInput
     *            the http input
     * @return true, if notify
     */
    boolean notify(Map<String, String> httpInput);
}
