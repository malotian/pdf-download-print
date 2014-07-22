/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.loggable.logger;

import org.slf4j.Logger;

/**
 * The Class WarnLogger.
 */
class WarnLogger extends AbstractLevelLogger {

    /**
     * Instantiates a new WarnLogger.
     * 
     * @param classLogger
     *            the class logger
     */
    public WarnLogger(Logger classLogger) {

        super(classLogger);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.xtradesoft.dlp.loggable.logger.LoggableLogger#log(java.lang.String)
     */
    @Override
    public void log(String msg) {

        if (logger().isWarnEnabled()) {
            logger().warn(msg);
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

        if (logger().isWarnEnabled()) {
            logger().warn(msg, params);
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

        if (logger().isWarnEnabled()) {
            logger().warn(msg, throwable);
        }
    }

}
