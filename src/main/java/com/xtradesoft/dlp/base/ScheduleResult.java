/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base;

import java.util.concurrent.ScheduledFuture;

/**
 * The Class ScheduleResult.
 */
public class ScheduleResult {

  /**
   * The error.
   */
  private Error _error;

  /**
   * The future.
   */
  ScheduledFuture<?> _future;

  /**
   * Instantiates a new ScheduleResult.
   *
   * @param error the error
   */
  ScheduleResult(Error error) {

    _error = error;
  }

  /**
   * Instantiates a new ScheduleResult.
   *
   * @param future the future
   */
  ScheduleResult(ScheduledFuture<?> future) {

    _future = future;
  }

  /**
   * Future.
   *
   * @return the scheduled future
   */
  public ScheduledFuture<?> future() {

    return _future;
  }

  /**
   * Gets the error.
   *
   * @return the error
   */
  public Error getError() {

    return _error;
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
   * Sets the error.
   *
   * @param error the new error
   */
  public void setError(Error error) {

    _error = error;
  }

}
