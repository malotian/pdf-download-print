/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base;

import java.util.ArrayList;

/**
 * The Class OperationResults.
 */
@SuppressWarnings("serial")
public class OperationResults extends ArrayList<OperationResult> {

    /** The error. */
    private Error error;

    /**
     * Instantiates a new OperationResults.
     */
    public OperationResults() {

    }

    /**
     * Instantiates a new OperationResults.
     * 
     * @param error
     *            the error
     */
    public OperationResults(Error error) {

        this.error = error;
    }

    /**
     * Error.
     * 
     * @return the error
     */
    public Error error() {

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
}
