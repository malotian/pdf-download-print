/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base;

import java.util.concurrent.Future;

/**
 * The Class FutureOperationResult.
 */
public class FutureOperationResult extends OperationError<Error> {

    /** The future. */
    Future<OperationResult> future;

    /** The operation. */
    Operation operation;

    /**
     * Instantiates a new FutureOperationResult.
     * 
     * @param error
     *            the error
     */
    FutureOperationResult(Error error) {

        super(error);
    }

    /**
     * Instantiates a new FutureOperationResult.
     * 
     * @param future
     *            the future
     */
    FutureOperationResult(Future<OperationResult> future) {

        this.future = future;
    }

    /**
     * Future.
     * 
     * @return the future
     */
    public Future<OperationResult> future() {

        return future;
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
     * @return the future operation result
     */
    public FutureOperationResult set(Operation operation) {

        this.operation = operation;
        return this;
    }

    /*
     * (non-Javadoc)
     * @see com.xtradesoft.dlp.base.OperationError#toString()
     */
    @Override
    public String toString() {

        return String.format("FutureOperationResult {%s, %s}", resultOf(), super.toString());

    }

}
