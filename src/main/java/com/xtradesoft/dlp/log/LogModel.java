/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.log;

import com.xtradesoft.dlp.base.tablemvc.Model;

/**
 * The Class LogModel.
 */
@SuppressWarnings("serial")
public class LogModel extends Model {

  /**
   * The singleton.
   */
  private static LogModel singleton;

  static {
    singleton = new LogModel();
    LogAppender.setModel(singleton);

  }

  /**
   * Model.
   *
   * @return the log model
   */
  public static LogModel model() {

    return singleton;
  }

  /**
   * Instantiates a new LogModel.
   */
  private LogModel() {

    super();
    addColumn("Level");
    addColumn("Timestamp");
    addColumn("Message");
  }

}
