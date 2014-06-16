/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base;

/**
 * The Class OperationError.
 *
 * @param <E> the element type
 */
public class OperationError<E extends Error> {

  /**
   * The error.
   */
  private E _error;

  /**
   * Instantiates a new OperationError.
   */
  public OperationError() {

  }

  /**
   * Instantiates a new OperationError.
   *
   * @param error the error
   */
  public OperationError(E error) {

    _error = error;
  }

  /**
   * Error.
   *
   * @return the error
   */
  public Error error() {

    return _error;
  }

  /**
   * Error.
   *
   * @param error the error
   */
  public void error(E error) {

    _error = error;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    return String.format("OperationError: {error: %s}", error());
  }
}
