/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl.download;

/**
 * An asynchronous update interface for receiving notifications about Download
 * information as the Download is constructed.
 */
public interface DownloadObserver {

  /**
   * This method is called when information about an Download which was
   * previously requested using an asynchronous interface becomes available.
   *
   * @param result the result
   */
  public void notify(DowmloadOperationResult result);

}
