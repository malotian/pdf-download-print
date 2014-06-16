/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.loggable.logger;

import org.slf4j.Logger;

/**
 * The Class TraceLogger.
 */
class TraceLogger extends AbstractLevelLogger {

  /**
   * Instantiates a new TraceLogger.
   *
   * @param classLogger the class logger
   */
  public TraceLogger(Logger classLogger) {

    super(classLogger);
  }

  /*
   * (non-Javadoc)
   * @see
   * com.xtradesoft.dlp.loggable.logger.LoggableLogger#log(java.lang.String)
   */
  @Override
  public void log(String msg) {

    if (classLogger.isTraceEnabled()) {
      classLogger.trace(msg);
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

    if (classLogger.isTraceEnabled()) {
      classLogger.trace(msg, params);
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

    if (classLogger.isTraceEnabled()) {
      classLogger.trace(msg, throwable);
    }
  }

}
