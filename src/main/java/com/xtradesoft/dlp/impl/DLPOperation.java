/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl;

import java.net.URL;

import com.xtradesoft.dlp.base.Context;
import com.xtradesoft.dlp.base.Operation;
import com.xtradesoft.dlp.base.OperationResult;
import com.xtradesoft.dlp.loggable.annotation.Loggable;

/**
 * The Class DLPOperation.
 */
public class DLPOperation extends Operation {

    /** The url. */
    private URL url;

    /*
     * (non-Javadoc)
     * @see
     * com.xtradesoft.dlp.base.Operation#execute(com.xtradesoft.dlp.base.Context
     * )
     */
    @Override
    @Loggable(level = Loggable.Level.Debug)
    public OperationResult execute(Context context) throws Exception {

        return null;
    }

    /**
     * Gets the url.
     * 
     * @return the url
     */
    public URL getURL() {

        return url;
    }

    /**
     * Sets the url.
     * 
     * @param url
     *            the new url
     */
    public void setURL(URL url) {

        this.url = url;
    }

    /*
     * (non-Javadoc)
     * @see com.xtradesoft.dlp.base.Operation#toString()
     */
    @Override
    public String toString() {

        return String.format("DLPOperation: {URL: %s, %s}", getURL(), super.toString());

    }
}
