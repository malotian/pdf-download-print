/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base;

import java.util.concurrent.ScheduledFuture;

/**
 * The Class ScheduleResult.
 */
public class ScheduleResult {

    /** The error. */
    private Error error;

    /** The future. */
    ScheduledFuture<?> future;

    /**
     * Instantiates a new ScheduleResult.
     * 
     * @param error
     *            the error
     */
    ScheduleResult(Error error) {

        this.error = error;
    }

    /**
     * Instantiates a new ScheduleResult.
     * 
     * @param future
     *            the future
     */
    ScheduleResult(ScheduledFuture<?> future) {

        this.future = future;
    }

    /**
     * Future.
     * 
     * @return the scheduled future
     */
    public ScheduledFuture<?> future() {

        return future;
    }

    /**
     * Gets the error.
     * 
     * @return the error
     */
    public Error getError() {

        return error;
    }

    /**
     * Checks for got error.
     * 
     * @return true, if successful
     */
    boolean hasGotError() {

        return null != error;
    }

    /**
     * Sets the error.
     * 
     * @param error
     *            the new error
     */
    public void setError(Error error) {

        this.error = error;
    }

}
