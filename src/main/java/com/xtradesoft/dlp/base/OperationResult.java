/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base;

/**
 * The Class OperationResult.
 */
public class OperationResult {

  /**
   * The error.
   */
  OperationError<Error> _error;

  /**
   * The operation.
   */
  Operation _operation;

  /**
   * Instantiates a new OperationResult.
   */
  public OperationResult() {

  }

  /**
   * Instantiates a new OperationResult.
   *
   * @param error the error
   */
  public OperationResult(Error error) {

    _error = new OperationError<Error>(error);
  }

  /**
   * Instantiates a new OperationResult.
   *
   * @param error the error
   */
  public OperationResult(OperationError<Error> error) {

    _error = error;
  }

  /**
   * Checks for got error.
   *
   * @return true, if successful
   */
  boolean hasGotError() {

    return null != _error;
  }

  /**
   * Result of.
   *
   * @return the operation
   */
  public Operation resultOf() {

    return _operation;
  }

  /**
   * Sets the.
   *
   * @param operation the operation
   * @return the operation result
   */
  public OperationResult set(Operation operation) {

    _operation = operation;
    return this;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    return String.format("OperationResult: {%s, error: %s}", _operation, (null == _error) ? null : _error.error());
  }

}
