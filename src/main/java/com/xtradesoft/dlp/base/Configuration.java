/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class Configuration.
 */
public abstract class Configuration {

    /** The observers. */
    List<ConfigurationObserver> observers = new ArrayList<ConfigurationObserver>();

    /**
     * Notify observers.
     */
    public void notifyObservers() {

        for (final ConfigurationObserver observer : observers) {
            observer.notify(this);
        }
    }

    /**
     * Register.
     * 
     * @param observer
     *            the observer
     */
    public void register(ConfigurationObserver observer) {

        observers.add(observer);
    }

    /**
     * Unregister.
     * 
     * @param observer
     *            the observer
     */
    public void unregister(ConfigurationObserver observer) {

        observers.remove(observer);
    }
}
