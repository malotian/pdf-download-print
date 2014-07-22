/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.loggable.logger;

/**
 * The Interface LoggableLogger.
 */
public interface LoggableLogger {

    /**
     * Log.
     * 
     * @param msg
     *            the msg
     */
    public void log(String msg);

    /**
     * Log.
     * 
     * @param msg
     *            the msg
     * @param params
     *            the params
     */
    public void log(String msg, Object... params);

    /**
     * Log.
     * 
     * @param msg
     *            the msg
     * @param throwable
     *            the throwable
     */
    public void log(String msg, Throwable throwable);
}
