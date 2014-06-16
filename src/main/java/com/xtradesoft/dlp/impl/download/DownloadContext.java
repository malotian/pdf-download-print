/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl.download;

import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

import com.xtradesoft.dlp.base.Context;
import com.xtradesoft.dlp.loggable.annotation.Loggable;

/**
 * The Class DownloadContext.
 */
public class DownloadContext extends Context {

  /**
   * The application credentials.
   */
  private UsernamePasswordCredentials _applicationCredentials;

  /**
   * The application login url.
   */
  private URL _applicationLoginURL;

  /**
   * The executor.
   */
  Executor _executor;

  /**
   * The http credentials.
   */
  private UsernamePasswordCredentials _httpCredentials;

  /**
   * The http get.
   */
  Request _httpGet;

  /**
   * The http head.
   */
  Request _httpHead;

  /**
   * The http post.
   */
  Request _httpPost;

  /**
   * The max backups.
   */
  private int _maxBackups;

  /**
   * The proxy.
   */
  private HttpHost _proxy = null;

  /**
   * The url.
   */
  private URL _url;

  /**
   * Instantiates a new DownloadContext.
   *
   * @param url the url
   */
  public DownloadContext(URL url) {

    _url = url;
  }

  /**
   * Application login required.
   *
   * @return true, if successful
   */
  public boolean applicationLoginRequired() {

    return (null != _httpPost);
  }

  /**
   * Gets the application credentials.
   *
   * @return the application credentials
   */
  public UsernamePasswordCredentials getApplicationCredentials() {

    return _applicationCredentials;
  }

  /**
   * Gets the application login url.
   *
   * @return the application login url
   */
  public URL getApplicationLoginURL() {

    return _applicationLoginURL;
  }

  /**
   * Gets the executor.
   *
   * @return the executor
   */
  public Executor getExecutor() {

    return _executor;
  }

  /**
   * Gets the http credentials.
   *
   * @return the http credentials
   */
  public UsernamePasswordCredentials getHttpCredentials() {

    return _httpCredentials;
  }

  /**
   * Gets the max back ups.
   *
   * @return the max back ups
   */
  public int getMaxBackUps() {

    return _maxBackups;
  }

  /**
   * Gets the proxy.
   *
   * @return the proxy
   */
  public HttpHost getProxy() {

    return _proxy;
  }

  /**
   * Gets the url.
   *
   * @return the url
   */
  public URL getURL() {

    return _url;
  }

  /**
   * Checks for proxy.
   *
   * @return true, if successful
   */
  public boolean hasProxy() {

    return (null != _proxy);
  }

  /**
   * Http application login.
   *
   * @return the request
   */
  public Request HttpApplicationLogin() {

    return _httpPost;
  }

  /**
   * Http get.
   *
   * @return the request
   */
  public Request HttpGet() {

    return _httpGet;
  }

  /**
   * Http head.
   *
   * @return the request
   */
  public Request HttpHead() {

    return _httpHead;
  }

  /**
   * Initialize.
   *
   * @return the download context
   * @throws URISyntaxException the URI syntax exception
   */
  @Loggable(level = Loggable.Level.Debug)
  public DownloadContext initialize() throws URISyntaxException {

    _executor = Executor.newInstance();
    _executor.auth(_httpCredentials);
    _httpGet = Request.Get(_url.toURI());
    _httpHead = Request.Head(_url.toURI());

    if (hasProxy()) {
      _httpGet.viaProxy(getProxy());
      _httpHead.viaProxy(getProxy());

    }

    if (null != _applicationLoginURL) {

      _httpPost = Request.Post(getApplicationLoginURL().toURI()).bodyForm(
              Form.form().add("email", getApplicationCredentials().getUserName())
                      .add("password", getApplicationCredentials().getPassword()).build());

      if (hasProxy()) {
        _httpPost.viaProxy(getProxy());
      }
    }

    return this;
  }

  /**
   * Sets the application credentials.
   *
   * @param applicationCredentials the new application credentials
   */
  public void setApplicationCredentials(UsernamePasswordCredentials applicationCredentials) {

    _applicationCredentials = applicationCredentials;
  }

  /**
   * Sets the application login url.
   *
   * @param applicationLoginURL the new application login url
   */
  public void setApplicationLoginURL(URL applicationLoginURL) {

    _applicationLoginURL = applicationLoginURL;
  }

  /**
   * Sets the http credentails.
   *
   * @param httpapplicationCredentials the new http credentails
   */
  public void setHttpCredentails(UsernamePasswordCredentials httpapplicationCredentials) {

    _httpCredentials = httpapplicationCredentials;
  }

  /**
   * Sets the max back ups.
   *
   * @param maxBackUps the new max back ups
   */
  public void setMaxBackUps(int maxBackUps) {

    _maxBackups = maxBackUps;
  }

  /**
   * Sets the proxy.
   *
   * @param proxy the new proxy
   */
  public void setProxy(HttpHost proxy) {

    _proxy = proxy;
  }

  /**
   * Sets the url.
   *
   * @param url the new url
   */
  public void setURL(URL url) {

    _url = url;
  }
}
