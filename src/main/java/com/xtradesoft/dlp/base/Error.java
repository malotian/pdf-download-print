/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base;

/**
 * The Class Error.
 */
@SuppressWarnings("serial")
public class Error extends Exception {

    /**
     * Instantiates a new Error.
     * 
     * @param inner
     *            the inner
     */
    public Error(Throwable inner) {

        super(inner);
    }

}
