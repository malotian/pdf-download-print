/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base.tablemvc;

/**
 * The Class Row.
 *
 * @param <T> the generic type
 */
public class Row<T extends Model> {

  /**
   * The model.
   */
  T _model;

  /**
   * The row index.
   */
  int _rowIndex = -1;

  /**
   * Instantiates a new Row.
   *
   * @param model    the model
   * @param rowIndex the row index
   */
  public Row(T model, int rowIndex) {

    _rowIndex = rowIndex;
    _model = model;
  }

  /**
   * Gets the.
   *
   * @param col the col
   * @return the string
   */
  public String get(int col) {

    return String.valueOf(_model.getValueAt(_rowIndex, col));
  }

  /**
   * Checks for.
   *
   * @param col the col
   * @return true, if successful
   */
  public boolean has(int col) {

    return ((null != _model.getValueAt(_rowIndex, col)) && !get(col).isEmpty());
  }

}
