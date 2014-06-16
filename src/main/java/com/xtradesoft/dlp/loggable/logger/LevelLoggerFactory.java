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
   * @param lvl  the lvl
   * @param type the type
   * @return the loggable logger
   */
  public static LoggableLogger create(Loggable.Level lvl, Class<?> type) {

    final Logger logger = LoggerFactory.getLogger(type);
    switch (lvl) {
      case Trace:
        return new TraceLogger(logger);
      case Debug:
        return new DebugLogger(logger);
      case Info:
        return new InfoLogger(logger);
      case Warn:
        return new WarnLogger(logger);
      case Error:
        return new ErrorLogger(logger);
    }
    throw new InvalidLoggerException("Level=" + lvl + " is not supported");
  }
}
