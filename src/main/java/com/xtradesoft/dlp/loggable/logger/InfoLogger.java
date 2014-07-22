/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.loggable.logger;

import org.slf4j.Logger;

/**
 * The Class InfoLogger.
 */
class InfoLogger extends AbstractLevelLogger {

    /**
     * Instantiates a new InfoLogger.
     * 
     * @param classLogger
     *            the class logger
     */
    public InfoLogger(Logger classLogger) {

        super(classLogger);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.xtradesoft.dlp.loggable.logger.LoggableLogger#log(java.lang.String)
     */
    @Override
    public void log(String msg) {

        if (logger().isInfoEnabled()) {
            logger().info(msg);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.xtradesoft.dlp.loggable.logger.LoggableLogger#log(java.lang.String,
     * java.lang.Object[])
     */
    @Override
    public void log(String msg, Object... params) {

        if (logger().isInfoEnabled()) {
            logger().info(msg, params);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.xtradesoft.dlp.loggable.logger.LoggableLogger#log(java.lang.String,
     * java.lang.Throwable)
     */
    @Override
    public void log(String msg, Throwable throwable) {

        if (logger().isInfoEnabled()) {
            logger().info(msg, throwable);
        }
    }

}
