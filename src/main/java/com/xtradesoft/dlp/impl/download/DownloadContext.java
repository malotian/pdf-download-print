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

    /** The application credentials. */
    private UsernamePasswordCredentials applicationCredentials;

    /** The application login url. */
    private URL applicationLoginURL;

    /** The executor. */
    Executor executor;

    /** The http credentials. */
    private UsernamePasswordCredentials httpCredentials;

    /** The http get. */
    Request httpGet;

    /** The http head. */
    Request httpHead;

    /** The http post. */
    Request httpPost;

    /** The max backups. */
    private int maxBackups;

    /** The proxy. */
    private HttpHost proxy = null;

    /** The url. */
    private URL url;

    /**
     * Instantiates a new DownloadContext.
     * 
     * @param url
     *            the url
     */
    public DownloadContext(URL url) {

        this.url = url;
    }

    /**
     * Application login required.
     * 
     * @return true, if successful
     */
    public boolean applicationLoginRequired() {

        return null != httpPost;
    }

    /**
     * Gets the application credentials.
     * 
     * @return the application credentials
     */
    public UsernamePasswordCredentials getApplicationCredentials() {

        return applicationCredentials;
    }

    /**
     * Gets the application login url.
     * 
     * @return the application login url
     */
    public URL getApplicationLoginURL() {

        return applicationLoginURL;
    }

    /**
     * Gets the executor.
     * 
     * @return the executor
     */
    public Executor getExecutor() {

        return executor;
    }

    /**
     * Gets the http credentials.
     * 
     * @return the http credentials
     */
    public UsernamePasswordCredentials getHttpCredentials() {

        return httpCredentials;
    }

    /**
     * Gets the max back ups.
     * 
     * @return the max back ups
     */
    public int getMaxBackUps() {

        return maxBackups;
    }

    /**
     * Gets the proxy.
     * 
     * @return the proxy
     */
    public HttpHost getProxy() {

        return proxy;
    }

    /**
     * Gets the url.
     * 
     * @return the url
     */
    public URL getURL() {

        return url;
    }

    /**
     * Checks for proxy.
     * 
     * @return true, if successful
     */
    public boolean hasProxy() {

        return null != proxy;
    }

    /**
     * Http application login.
     * 
     * @return the request
     */
    public Request httpApplicationLogin() {

        return httpPost;
    }

    /**
     * Http get.
     * 
     * @return the request
     */
    public Request httpGet() {

        return httpGet;
    }

    /**
     * Http head.
     * 
     * @return the request
     */
    public Request httpHead() {

        return httpHead;
    }

    /**
     * Initialize.
     * 
     * @return the download context
     * @throws URISyntaxException
     *             the URI syntax exception
     */
    @Loggable(level = Loggable.Level.Debug)
    public DownloadContext initialize() throws URISyntaxException {

        executor = Executor.newInstance();
        executor.auth(httpCredentials);
        httpGet = Request.Get(url.toURI());
        httpHead = Request.Head(url.toURI());

        if (hasProxy()) {
            httpGet.viaProxy(getProxy());
            httpHead.viaProxy(getProxy());

        }

        if (null != applicationLoginURL) {

            httpPost = Request.Post(getApplicationLoginURL().toURI()).bodyForm(
                    Form.form().add("email", getApplicationCredentials().getUserName())
                            .add("password", getApplicationCredentials().getPassword()).build());

            if (hasProxy()) {
                httpPost.viaProxy(getProxy());
            }
        }

        return this;
    }

    /**
     * Sets the application credentials.
     * 
     * @param applicationCredentials
     *            the new application credentials
     */
    public void setApplicationCredentials(UsernamePasswordCredentials applicationCredentials) {

        this.applicationCredentials = applicationCredentials;
    }

    /**
     * Sets the application login url.
     * 
     * @param applicationLoginURL
     *            the new application login url
     */
    public void setApplicationLoginURL(URL applicationLoginURL) {

        this.applicationLoginURL = applicationLoginURL;
    }

    /**
     * Sets the http credentails.
     * 
     * @param httpapplicationCredentials
     *            the new http credentails
     */
    public void setHttpCredentails(UsernamePasswordCredentials httpapplicationCredentials) {

        httpCredentials = httpapplicationCredentials;
    }

    /**
     * Sets the max back ups.
     * 
     * @param maxBackUps
     *            the new max back ups
     */
    public void setMaxBackUps(int maxBackUps) {

        maxBackups = maxBackUps;
    }

    /**
     * Sets the proxy.
     * 
     * @param proxy
     *            the new proxy
     */
    public void setProxy(HttpHost proxy) {

        this.proxy = proxy;
    }

    /**
     * Sets the url.
     * 
     * @param url
     *            the new url
     */
    public void setURL(URL url) {

        this.url = url;
    }
}
