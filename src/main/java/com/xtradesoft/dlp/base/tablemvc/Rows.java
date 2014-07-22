/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base.tablemvc;

import java.util.Iterator;

/**
 * The Class Rows.
 * 
 * @param <T>
 *            the generic type
 */
public class Rows<T extends Model> implements Iterator<Row<T>> {

    /** The context row. */
    private int contextRow = -1;

    /** The model. */
    private final T model;

    /**
     * Instantiates a new Rows.
     * 
     * @param model
     *            the model
     */
    public Rows(T model) {

        this.model = model;
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {

        return this.contextRow + 1 < this.model.getRowCount();
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public Row<T> next() {

        if (hasNext()) {
            return new Row<T>(this.model, ++this.contextRow);
        }
        throw new IndexOutOfBoundsException("no more rows");
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {

        throw new UnsupportedOperationException();

    }

}
