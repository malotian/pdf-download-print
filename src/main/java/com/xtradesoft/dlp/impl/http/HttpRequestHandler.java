/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl.http;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * The Class HttpRequestHandler.
 */
public class HttpRequestHandler implements HttpHandler {

  /**
   * The Constant logger.
   */
  final static Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);

  /**
   * The observer.
   */
  private HttpObserver _observer;

  /*
   * (non-Javadoc)
   * @see
   * com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange
   * )
   */
  @Override
  public void handle(HttpExchange exchange) throws IOException {

    logger.info("recieved, http request type: {}, uri: {}", exchange.getRequestMethod(), exchange.getRequestURI());
    final Map<String, String> parameters = parseQueryParameters(exchange.getRequestURI());
    final boolean success = _observer.notify(parameters);
    exchange.sendResponseHeaders(success ? 200 : 400, 0);
    exchange.getResponseBody().close();
    exchange.close();
    logger.info("sent, http response: {}, request uri: {}", success ? 200 : 400, exchange.getRequestURI());
  }

  /**
   * Parses the query parameters.
   *
   * @param uri the uri
   * @return the map
   */
  protected Map<String, String> parseQueryParameters(final URI uri) {

    final Map<String, String> map = new HashMap<String, String>();
    if ((null != uri.getQuery()) && (uri.getQuery().length() > 0)) {
      final String[] params = uri.getQuery().split("&");
      for (final String param : params) {
        final String[] pair = param.split("=", 2);
        logger.debug("http query parameter, {}: {}", pair[0], pair[1]);
        map.put(pair[0], pair.length > 1 ? pair[1] : null);
      }
    }
    return map;
  }

  /**
   * Register.
   *
   * @param observer the observer
   */
  void register(HttpObserver observer) {

    _observer = observer;
  }

}
