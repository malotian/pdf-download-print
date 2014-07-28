/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl.http;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * The Class HttpRequestHandler.
 */
public class HttpRequestHandler implements HttpHandler {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestHandler.class);

    /** The observer. */
    private HttpObserver observer;

    /*
     * (non-Javadoc)
     * @see
     * com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange
     * )
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        LOGGER.info("recieved, http request type: {}, uri: {}", exchange.getRequestMethod(), exchange.getRequestURI());
        final Map<String, String> parameters = parseQueryParameters(exchange.getRequestURI());
        final boolean success = observer.notify(parameters);
        exchange.sendResponseHeaders(success ? 200 : 400, 0);
        exchange.getResponseBody().close();
        exchange.close();
        LOGGER.info("sent, http response: {}, request uri: {}", success ? 200 : 400, exchange.getRequestURI());
    }

    /**
     * Parses the query parameters.
     * 
     * @param uri
     *            the uri
     * @return the map
     */
    protected Map<String, String> parseQueryParameters(final URI uri) {

        final List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
        final Map<String, String> map = new HashMap<String, String>();
        for (final NameValuePair param : params) {
            LOGGER.debug("http query parameter, {}: {}", param.getName(), param.getValue());
            map.put(param.getName(), param.getValue());
        }
        return map;
    }

    /**
     * Register.
     * 
     * @param observer
     *            the observer
     */
    void register(HttpObserver observer) {

        this.observer = observer;
    }

}
