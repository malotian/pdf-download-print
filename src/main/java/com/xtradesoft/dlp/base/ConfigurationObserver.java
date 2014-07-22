/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base;

/**
 * An asynchronous update interface for receiving notifications about
 * Configuration information as the Configuration is constructed.
 */
public interface ConfigurationObserver {

    /**
     * This method is called when information about an Configuration which was
     * previously requested using an asynchronous interface becomes available.
     * 
     * @param configuration
     *            the configuration
     */
    void notify(Configuration configuration);
}
