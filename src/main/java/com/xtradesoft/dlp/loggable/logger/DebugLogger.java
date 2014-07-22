/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.loggable.logger;

import org.slf4j.Logger;

/**
 * The Class DebugLogger.
 */
class DebugLogger extends AbstractLevelLogger {

    /**
     * Instantiates a new DebugLogger.
     * 
     * @param classLogger
     *            the class logger
     */
    public DebugLogger(Logger classLogger) {

        super(classLogger);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.xtradesoft.dlp.loggable.logger.LoggableLogger#log(java.lang.String)
     */
    @Override
    public void log(String msg) {

        if (logger().isDebugEnabled()) {
            logger().debug(msg);
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

        if (logger().isDebugEnabled()) {
            logger().debug(msg, params);
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

        if (logger().isDebugEnabled()) {
            logger().debug(msg, throwable);
        }
    }
}
