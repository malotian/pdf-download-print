/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl;

import com.xtradesoft.dlp.base.Context;
import com.xtradesoft.dlp.base.Operation;
import com.xtradesoft.dlp.base.OperationExecutor;
import com.xtradesoft.dlp.base.OperationResult;
import com.xtradesoft.dlp.loggable.annotation.Loggable;

/**
 * The Class DLPOperationExecutor.
 */
@Loggable(level = Loggable.Level.Debug)
public class DLPOperationExecutor extends OperationExecutor {

  /*
   * (non-Javadoc)
   * @see
   * com.xtradesoft.dlp.base.OperationExecutor#execute(com.xtradesoft.dlp.
   * base.Operation)
   */
  @Override
  public OperationResult execute(Operation operation) throws Exception {

    final Context context = getContextprovider().getContext(operation);
    return operation.execute(context);
  }

}
