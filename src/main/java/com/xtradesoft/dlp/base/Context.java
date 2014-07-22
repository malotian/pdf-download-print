/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base;

/**
 * The Class Context.
 */
public abstract class Context {

    /** The next. */
    Context next;

    /**
     * Checks for next.
     * 
     * @return true, if successful
     */
    public boolean hasNext() {

        return next != null;
    }

    /**
     * Next.
     * 
     * @return the context
     */
    public Context next() {

        return next;
    }

    /**
     * Next.
     * 
     * @param next
     *            the next
     */
    public void next(Context next) {

        this.next = next;
    }
}
