/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base;

/**
 * The Class OperationResult.
 */
public class OperationResult {

    /** The error. */
    OperationError<Error> error;

    /** The operation. */
    Operation operation;

    /**
     * Instantiates a new OperationResult.
     */
    public OperationResult() {

    }

    /**
     * Instantiates a new OperationResult.
     * 
     * @param error
     *            the error
     */
    public OperationResult(Error error) {

        this.error = new OperationError<Error>(error);
    }

    /**
     * Instantiates a new OperationResult.
     * 
     * @param error
     *            the error
     */
    public OperationResult(OperationError<Error> error) {

        this.error = error;
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
     * Result of.
     * 
     * @return the operation
     */
    public Operation resultOf() {

        return operation;
    }

    /**
     * Sets the.
     * 
     * @param operation
     *            the operation
     * @return the operation result
     */
    public OperationResult set(Operation operation) {

        this.operation = operation;
        return this;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return String.format("OperationResult: {%s, error: %s}", operation, null == error ? null : error.error());
    }

}
