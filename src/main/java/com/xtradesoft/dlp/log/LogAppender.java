/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.log;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Layout;
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

  /**
   * The model.
   */
  private static Model _model;

  /**
   * Sets the model.
   *
   * @param model the new model
   */
  public static void setModel(Model model) {

    _model = model;
  }

  /**
   * Instantiates a new LogAppender.
   */
  public LogAppender() {

    setLayout(new PatternLayout("%5.5p|%d{HH:mm:ss}|%m"));
  }

  /**
   * Instantiates a new LogAppender.
   *
   * @param model the model
   */
  public LogAppender(Model model) {

    LogAppender._model = model;
    setLayout(new PatternLayout("%5.5p|%d{HH:mm:ss}|%m"));
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

  /*
   * (non-Javadoc)
   * @see org.apache.log4j.WriterAppender#closeWriter()
   */
  @Override
  protected final void closeWriter() {

  }

  /**
   * Insert text.
   *
   * @param level the level
   * @param text  the text
   */
  protected void insertText(Level level, String text) {

    final String[] tokens = text.split("\\|");

    final List<Object> message = new ArrayList<Object>();
    for (final String token : tokens) {
      message.add(token);
    }

    if (null != _model) {
      _model.addRow(message);
    }

  }

  /**
   * Insert text.
   *
   * @param level   the level
   * @param message the message
   * @param ti      the ti
   */
  private void insertText(Level level, String message, ThrowableInformation ti) {

    insertText(level, message);

    if (ti == null) {
      return;
    }

    final String s[] = ti.getThrowableStrRep();
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

  /*
   * (non-Javadoc)
   * @see org.apache.log4j.AppenderSkeleton#setLayout(org.apache.log4j.Layout)
   */
  @Override
  public void setLayout(Layout layout) {

    super.setLayout(layout);
  }
}
