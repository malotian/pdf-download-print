/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.loggable.logger;

/**
 * The Class InvalidLoggerException.
 */
@SuppressWarnings("serial")
class InvalidLoggerException extends RuntimeException {

  /**
   * Instantiates a new InvalidLoggerException.
   *
   * @param message the message
   */
  public InvalidLoggerException(String message) {

    super(message);

  }

  /**
   * Instantiates a new InvalidLoggerException.
   *
   * @param message the message
   * @param cause   the cause
   */
  public InvalidLoggerException(String message, Throwable cause) {

    super(message, cause);

  }

  /**
   * Instantiates a new InvalidLoggerException.
   *
   * @param cause the cause
   */
  public InvalidLoggerException(Throwable cause) {

    super(cause);

  }

}
