/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.loggable.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xtradesoft.dlp.loggable.annotation.Loggable;

/**
 * A factory for creating LevelLogger objects.
 */
public class LevelLoggerFactory {

    /**
     * Creates the.
     * 
     * @param lvl
     *            the lvl
     * @param type
     *            the type
     * @return the loggable logger
     */
    public static LoggableLogger create(Loggable.Level lvl, Class<?> type) {

        final Logger classLogger = LoggerFactory.getLogger(type);
        LoggableLogger loggable = null;
        switch (lvl) {
            case Trace:
                loggable = new TraceLogger(classLogger);
                break;
            case Debug:
                loggable = new DebugLogger(classLogger);
                break;
            case Info:
                loggable = new InfoLogger(classLogger);
                break;
            case Warn:
                loggable = new WarnLogger(classLogger);
                break;
            case Error:
                loggable = new ErrorLogger(classLogger);
                break;
            default:
                throw new InvalidLoggerException("Level=" + lvl + " is not supported");
        }
        return loggable;
    }

    /**
     * Instantiates a new LevelLoggerFactory.
     */
    private LevelLoggerFactory() {

    }
}
