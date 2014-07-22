/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base;

/**
 * The Class ContextProvider.
 */
public abstract class ContextProvider {

    /**
     * Gets the context.
     * 
     * @return the context
     * @throws Exception
     *             the exception
     */
    public abstract Context getContext() throws Exception;

    /**
     * Gets the context.
     * 
     * @param hint
     *            the hint
     * @return the context
     * @throws Exception
     *             the exception
     */
    public abstract Context getContext(Object[] hint) throws Exception;

    /**
     * Initialize.
     * 
     * @param configuration
     *            the configuration
     */
    public abstract void initialize(Configuration configuration);
}
