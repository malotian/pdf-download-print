/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base.tablemvc;

/**
 * The Class Row.
 * 
 * @param <T>
 *            the generic type
 */
public class Row<T extends Model> {

    /** The model. */
    T model;

    /** The row index. */
    int rowIndex = -1;

    /**
     * Instantiates a new Row.
     * 
     * @param model
     *            the model
     * @param rowIndex
     *            the row index
     */
    public Row(T model, int rowIndex) {

        this.rowIndex = rowIndex;
        this.model = model;
    }

    /**
     * Gets the.
     * 
     * @param col
     *            the col
     * @return the string
     */
    public String get(int col) {

        return String.valueOf(this.model.getValueAt(this.rowIndex, col));
    }

    /**
     * Checks for.
     * 
     * @param col
     *            the col
     * @return true, if successful
     */
    public boolean has(int col) {

        return null != this.model.getValueAt(this.rowIndex, col) && !get(col).isEmpty();
    }

}
