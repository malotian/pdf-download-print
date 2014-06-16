/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl.download;

import java.io.File;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xtradesoft.dlp.base.Context;
import com.xtradesoft.dlp.base.FileRoller;
import com.xtradesoft.dlp.base.OperationResult;
import com.xtradesoft.dlp.impl.DLPOperation;

/**
 * The Class DownloadOperation.
 */
public class DownloadOperation extends DLPOperation {

  /**
   * The Constant logger.
   */
  final static Logger logger = LoggerFactory.getLogger(DownloadOperation.class);

  /**
   * The observer.
   */
  private DownloadObserver _observer;

  /**
   * The roller.
   */
  public FileRoller _roller;

  /**
   * Instantiates a new DownloadOperation.
   *
   * @param url the url
   */
  public DownloadOperation(URL url) {

    setURL(url);
    _roller = new FileRoller(getBaseFileName());
  }

  /*
   * (non-Javadoc)
   * @see
   * com.xtradesoft.dlp.impl.DLPOperation#execute(com.xtradesoft.dlp.base.
   * Context)
   */
  @Override
  public OperationResult execute(Context context) throws Exception {

    final DownloadContext dlContext = (DownloadContext) context;

    logger.debug("application login required: {}", dlContext.applicationLoginRequired());

    if (dlContext.applicationLoginRequired()) {
      login(dlContext);
    }

    final File file = _roller.roll(dlContext.getMaxBackUps());
    final DowmloadOperationResult result = new DowmloadOperationResult(dlContext.getURL(), file.getName());

    logger.debug("HttpGet: {}", dlContext.HttpGet());
    dlContext.getExecutor().execute(dlContext.HttpGet()).saveContent(file);
    logger.info("success, HttpGet: {}, Response saved to file: {}", dlContext.HttpGet(), file.getAbsolutePath());

    logger.debug("notify, downloaded completion - file: {}", file);
    getObserver().notify(result);
    logger.debug("notified, downloaded completion - file: {}", file);

    return result;
  }

  /**
   * Gets the base file name.
   *
   * @return the base file name
   */
  protected String getBaseFileName() {

    String baseFile = getURL().getPath().substring(getURL().getPath().lastIndexOf('/') + 1);

    if (!baseFile.endsWith(".pdf")) {
      baseFile += ".pdf";
    }

    return baseFile;
  }

  /**
   * Gets the observer.
   *
   * @return the observer
   */
  public DownloadObserver getObserver() {

    return _observer;
  }

  /**
   * Login.
   *
   * @param dlContext the dl context
   * @return true, if successful
   * @throws Exception the exception
   */
  boolean login(DownloadContext dlContext) throws Exception {

    logger.info("HttpApplicationLogin.Request: {}", dlContext.HttpApplicationLogin());
    final Response response = dlContext.getExecutor().execute(dlContext.HttpApplicationLogin());
    final HttpResponse httpResponse = response.returnResponse();
    logger.debug("HttpApplicationLogin.Response: {}", httpResponse);
    logger.debug("HttpApplicationLogin.Response, location-header: {}", httpResponse.getFirstHeader("location")
            .getValue());

    return true;
  }

  /**
   * Register.
   *
   * @param observer the observer
   */
  public void register(DownloadObserver observer) {

    _observer = observer;
  }

  /*
   * (non-Javadoc)
   * @see com.xtradesoft.dlp.impl.DLPOperation#toString()
   */
  @Override
  public String toString() {

    return String.format("DownloadOperation: {%s}", super.toString());

  }
}
