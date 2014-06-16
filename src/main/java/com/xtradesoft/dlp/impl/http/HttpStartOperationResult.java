/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl.http;

import com.xtradesoft.dlp.base.OperationResult;

/**
 * The Class HttpStartOperationResult.
 */
public class HttpStartOperationResult extends OperationResult {

  /**
   * The started.
   */
  boolean _started;

  /**
   * Instantiates a new HttpStartOperationResult.
   *
   * @param started the started
   */
  HttpStartOperationResult(boolean started) {

    _started = started;
  }

}
