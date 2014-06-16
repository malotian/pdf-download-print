/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl;

import com.xtradesoft.dlp.base.Error;
import com.xtradesoft.dlp.base.OperationResult;

/**
 * The Class DLPOperationResult.
 */
public class DLPOperationResult extends OperationResult {

  /**
   * Instantiates a new DLPOperationResult.
   */
  public DLPOperationResult() {

  }

  /**
   * Instantiates a new DLPOperationResult.
   *
   * @param error the error
   */
  public DLPOperationResult(Error error) {

    super(error);
  }

  /*
   * (non-Javadoc)
   * @see com.xtradesoft.dlp.base.OperationResult#toString()
   */
  @Override
  public String toString() {

    return String.format("DLPOperationResult: {%s}", super.toString());
  }

}
