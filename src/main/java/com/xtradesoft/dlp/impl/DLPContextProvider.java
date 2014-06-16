/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xtradesoft.dlp.base.Configuration;
import com.xtradesoft.dlp.base.Context;
import com.xtradesoft.dlp.base.ContextProvider;
import com.xtradesoft.dlp.base.tablemvc.Model;
import com.xtradesoft.dlp.base.tablemvc.Rows;
import com.xtradesoft.dlp.impl.download.DownloadContext;
import com.xtradesoft.dlp.impl.download.DownloadOperation;
import com.xtradesoft.dlp.impl.http.HttpContext;
import com.xtradesoft.dlp.impl.http.HttpStartOperation;
import com.xtradesoft.dlp.impl.print.PrintContext;
import com.xtradesoft.dlp.impl.print.PrintOperation;
import com.xtradesoft.dlp.loggable.annotation.Loggable;

/**
 * The Class DLPContextProvider.
 */
@Loggable(level = Loggable.Level.Debug)
public class DLPContextProvider extends ContextProvider {

  /**
   * The Constant logger.
   */
  final static Logger logger = LoggerFactory.getLogger(DLPContextProvider.class);

  /**
   * The configuration.
   */
  private DLPConfiguration _configuration;

  /**
   * The downloadbles.
   */
  private Map<URI, DLPConfiguration.Record> _downloadbles;

  /*
   * (non-Javadoc)
   * @see com.xtradesoft.dlp.base.ContextProvider#getContext()
   */
  @Override
  public Context getContext() throws Exception {

    return null;
  }

  /*
   * (non-Javadoc)
   * @see com.xtradesoft.dlp.base.ContextProvider#getContext(java.lang.Object)
   */
  @Override
  public Context getContext(Object hint) throws Exception {

    logger.debug("hint: {}", hint.getClass());
    if (hint instanceof DownloadOperation) {
      final URL url = ((DownloadOperation) hint).getURL();
      final DownloadContext context = new DownloadContext(url);
      final DLPConfiguration.Record record = lookup(url);

      logger.debug("URL: {}", url);
      logger.debug("HttpUsername: {}", record.HttpUsername());
      logger.debug("HttpPassword: {}", record.HttpPassword());
      logger.debug("AppLoginPostURL: {}", record.hasAppLoginPostURL() ? record.AppLoginPostURL() : "");
      logger.debug("AppUsername: {}", record.AppUsername());
      logger.debug("AppPassword: {}", record.AppPassword());
      logger.debug("ProxyHost: {}", record.ProxyHost());
      logger.debug("ProxyPort: {}", record.hasProxyPort() ? record.ProxyPort() : "");
      logger.debug("MaxBackup: {}", record.hasMaxBackup() ? record.MaxBackup() : "");

      context.setHttpCredentails(new UsernamePasswordCredentials(record.HttpUsername(), record.HttpPassword()));

      if (record.hasAppLoginPostURL()) {
        context.setApplicationLoginURL(record.AppLoginPostURL());

        context.setApplicationCredentials(new UsernamePasswordCredentials(record.AppUsername(), record
                .AppPassword()));
      }
      context.setMaxBackUps(record.MaxBackup());

      if (!record.ProxyHost().isEmpty()) {
        if (record.ProxyPort() > 0) {
          context.setProxy(new HttpHost(record.ProxyHost(), record.ProxyPort()));
        } else {
          context.setProxy(new HttpHost(record.ProxyHost()));
        }
      }

      try {
        return context.initialize();
      } catch (final URISyntaxException e) {
        logger.error(e.getMessage(), e);
      }

    } else if (hint instanceof PrintOperation) {
      final URL url = ((PrintOperation) hint).getURL();
      final PrintContext context = new PrintContext(url);
      final DLPConfiguration.Record record = lookup(url);

      logger.debug("URL: {}", url);
      logger.debug("Printer: {}", record.Printer());
      context.setPrinter(record.Printer());
      return context.initialize();
    } else if (hint instanceof HttpStartOperation) {
      final HttpContext context = new HttpContext();
      context.setHost(_configuration.getHttpServerHost());
      context.setPort(_configuration.getHttpServerPort());
      context.setServiceContext(_configuration.getHttpServiceContext());
      return context.initialize();
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * com.xtradesoft.dlp.base.ContextProvider#initialize(com.xtradesoft.dlp
   * .base.Configuration)
   */
  @Override
  public void initialize(Configuration configuration) {

    _configuration = (DLPConfiguration) configuration;
    final Rows<Model> rows = new Rows<Model>(_configuration.getModel());

    final Map<URI, DLPConfiguration.Record> downloadables = new HashMap<URI, DLPConfiguration.Record>();
    while (rows.hasNext()) {
      final DLPConfiguration.Record record = new DLPConfiguration.Record(rows.next());
      logger.debug("preliminary validation, configuration entry: {}", record);
      if (!record.validate()) {
        logger.warn("failed, preliminary validation - ignoring configuration entry {}", record);
        continue;
      }
      logger.debug("success, preliminary validatiion - configuration entry: {}", record);

      final URI downloadable = removeQueryString(record.DownloadURL());

      if (null != record.DownloadURL().getQuery()) {
        logger.warn("warning, removed query string: {} while lookup, from URL {}", record.DownloadURL()
                .getQuery(), record.DownloadURL());
      }

      downloadables.put(downloadable, record);
      logger.debug("configured, downloadable: {}", downloadable);

    }

    logger.info("configured, downloadables count: {}", downloadables.size());

    if (downloadables.isEmpty()) {
      logger.warn("warning, no downloadable configured...");
    }

    _downloadbles = Collections.synchronizedMap(downloadables);
  }

  /**
   * Lookup.
   *
   * @param url the url
   * @return the DLP configuration. record
   * @throws Exception the exception
   */
  DLPConfiguration.Record lookup(URL url) throws Exception {

    if (null != url.getQuery()) {
      logger.warn("warning, removed query string: {} while lookup, from URL {}", url.getQuery(), url);
    }

    final URI downloadable = removeQueryString(url);
    final DLPConfiguration.Record record = _downloadbles.get(downloadable);

    if (null == record) {
      logger.error("{}, is not configured", downloadable);
      throw new IllegalArgumentException(String.format("%s is not configured", downloadable));
    }
    return record;
  }

  /**
   * Removes the query string.
   *
   * @param input the input
   * @return the uri
   */
  private URI removeQueryString(URL input) {

    try {
      return new URIBuilder(input.toURI()).removeQuery().build();
    } catch (final URISyntaxException e) {
      logger.warn("error, invalid url/uri: ", input);
    }
    return null;
  }
}
