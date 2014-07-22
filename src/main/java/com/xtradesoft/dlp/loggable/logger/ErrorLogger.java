/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.loggable.logger;

import org.slf4j.Logger;

/**
 * The Class ErrorLogger.
 */
class ErrorLogger extends AbstractLevelLogger {

    /**
     * Instantiates a new ErrorLogger.
     * 
     * @param classLogger
     *            the class logger
     */
    public ErrorLogger(Logger classLogger) {

        super(classLogger);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.xtradesoft.dlp.loggable.logger.LoggableLogger#log(java.lang.String)
     */
    @Override
    public void log(String msg) {

        if (logger().isErrorEnabled()) {
            logger().error(msg);
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

        if (logger().isErrorEnabled()) {
            logger().error(msg, params);
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

        if (logger().isErrorEnabled()) {
            logger().error(msg, throwable);
        }
    }

}
