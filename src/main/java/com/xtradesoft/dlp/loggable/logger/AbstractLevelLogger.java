/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.loggable.logger;

import org.slf4j.Logger;

/**
 * The Class AbstractLevelLogger.
 */
abstract class AbstractLevelLogger implements LoggableLogger {

    /** The class logger. */
    private final Logger classLogger;

    /**
     * Instantiates a new AbstractLevelLogger.
     * 
     * @param classLogger
     *            the class logger
     */
    public AbstractLevelLogger(Logger classLogger) {

        this.classLogger = classLogger;
    }

    /**
     * Logger.
     * 
     * @return the logger
     */
    public Logger logger() {

        return classLogger;
    }

}
