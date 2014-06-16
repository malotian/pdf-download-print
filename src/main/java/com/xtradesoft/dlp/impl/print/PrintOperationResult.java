/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl.print;

import com.xtradesoft.dlp.impl.DLPOperationResult;

/**
 * The Class PrintOperationResult.
 */
public class PrintOperationResult extends DLPOperationResult {

  /**
   * The file.
   */
  public String file;

  /**
   * The print completed.
   */
  public boolean printCompleted;

  /**
   * Instantiates a new PrintOperationResult.
   *
   * @param file           the file
   * @param printCompleted the print completed
   */
  PrintOperationResult(String file, boolean printCompleted) {

    super();
    this.file = file;
    this.printCompleted = printCompleted;
  }

  /*
   * (non-Javadoc)
   * @see com.xtradesoft.dlp.impl.DLPOperationResult#toString()
   */
  @Override
  public String toString() {

    return String.format("PrintOperationResult: {file:%s, printCompleted:%s,  %s}", file, printCompleted,
            super.toString());
  }

}
