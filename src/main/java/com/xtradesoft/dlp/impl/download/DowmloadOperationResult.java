/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl.download;

import java.net.URL;

import com.xtradesoft.dlp.impl.DLPOperationResult;

/**
 * The Class DowmloadOperationResult.
 */
public class DowmloadOperationResult extends DLPOperationResult {

  /**
   * The file.
   */
  public String file;

  /**
   * The url.
   */
  public URL url;

  /**
   * Instantiates a new DowmloadOperationResult.
   *
   * @param url  the url
   * @param file the file
   */
  DowmloadOperationResult(URL url, String file) {

    this.url = url;
    this.file = file;
  }

  /*
   * (non-Javadoc)
   * @see com.xtradesoft.dlp.impl.DLPOperationResult#toString()
   */
  @Override
  public String toString() {

    return String.format("DowmloadOperationResult: {url:%s, file:%s,  %s}", url, file, super.toString());
  }

}
