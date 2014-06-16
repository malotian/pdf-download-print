/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xtradesoft.dlp.base.Context;
import com.xtradesoft.dlp.impl.DLPException;
import com.xtradesoft.dlp.loggable.annotation.Loggable;

/**
 * The Class HttpContext.
 */
public class HttpContext extends Context {

  /**
   * The Constant logger.
   */
  final static Logger logger = LoggerFactory.getLogger(HttpContext.class);

  /**
   * The host.
   */
  private String _host = "";

  /**
   * The port.
   */
  private int _port = -1;

  /**
   * The service context.
   */
  private String _serviceContext = "";

  /**
   * Instantiates a new HttpContext.
   */
  public HttpContext() {

  }

  /**
   * Gets the host.
   *
   * @return the host
   */
  public String getHost() {

    return _host;
  }

  /**
   * Gets the port.
   *
   * @return the port
   */
  public int getPort() {

    return _port;
  }

  /**
   * Gets the service context.
   *
   * @return the service context
   */
  public String getServiceContext() {

    return _serviceContext;
  }

  /**
   * Checks for host.
   *
   * @return true, if successful
   */
  boolean hasHost() {

    return !_host.isEmpty();
  }

  /**
   * Initialize.
   *
   * @return the http context
   * @throws DLPException the DLP exception
   */
  @Loggable(level = Loggable.Level.Debug)
  public HttpContext initialize() throws DLPException {

    if ((null != _serviceContext) && !_serviceContext.isEmpty() && !_serviceContext.startsWith("/")) {
      _serviceContext = "/" + _serviceContext;
    }

    return this;
  }

  /**
   * Sets the host.
   *
   * @param _host the new host
   */
  public void setHost(String _host) {

    this._host = _host;
  }

  /**
   * Sets the port.
   *
   * @param port the new port
   */
  public void setPort(int port) {

    _port = port;
  }

  /**
   * Sets the service context.
   *
   * @param serviceContext the new service context
   */
  public void setServiceContext(String serviceContext) {

    _serviceContext = serviceContext;
  }

}
