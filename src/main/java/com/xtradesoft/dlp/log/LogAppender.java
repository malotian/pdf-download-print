/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.log;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import com.xtradesoft.dlp.base.tablemvc.Model;

/**
 * The Class LogAppender.
 */
public class LogAppender extends WriterAppender {

    /** The model. */
    private static Model model;

    /** The Constant patternLayout. */
    private static final String PATTERN_LAYOUT = "%5.5p|%d{HH:mm:ss}|%m";

    /**
     * Sets the model.
     * 
     * @param model
     *            the new model
     */
    public static void setModel(Model model) {

        LogAppender.model = model;
    }

    /**
     * Instantiates a new LogAppender.
     */
    public LogAppender() {

        setLayout(new PatternLayout(PATTERN_LAYOUT));
    }

    /**
     * Instantiates a new LogAppender.
     * 
     * @param model
     *            the model
     */
    public LogAppender(Model model) {

        LogAppender.model = model;
        setLayout(new PatternLayout(PATTERN_LAYOUT));
    }

    /*
     * (non-Javadoc)
     * @see
     * org.apache.log4j.WriterAppender#append(org.apache.log4j.spi.LoggingEvent)
     */
    @Override
    public void append(LoggingEvent event) {

        insertText(event.getLevel(), layout.format(event), event.getThrowableInformation());
    }

    /**
     * Insert text.
     * 
     * @param level
     *            the level
     * @param text
     *            the text
     */
    protected void insertText(Level level, String text) {

        final String[] tokens = text.split("\\|");

        final List<Object> message = new ArrayList<Object>();
        for (final String token : tokens) {
            message.add(token);
        }

        if (null != model) {
            model.addRow(message);
        }

    }

    /**
     * Insert text.
     * 
     * @param level
     *            the level
     * @param message
     *            the message
     * @param ti
     *            the ti
     */
    private void insertText(Level level, String message, ThrowableInformation ti) {

        insertText(level, message);

        if (ti == null) {
            return;
        }

        final String[] s = ti.getThrowableStrRep();
        for (final String element : s) {
            insertText(level, "||" + element);
        }

    }

    /*
     * (non-Javadoc)
     * @see org.apache.log4j.WriterAppender#requiresLayout()
     */
    @Override
    public boolean requiresLayout() {

        return false;
    }

}
