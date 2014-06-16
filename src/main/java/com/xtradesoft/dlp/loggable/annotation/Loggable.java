/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.loggable.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Interface Loggable.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Loggable {

  /**
   * The Enum Level.
   */
  public enum Level {

    /**
     * The Debug.
     */
    Debug,

    /**
     * The Error.
     */
    Error,

    /**
     * The Info.
     */
    Info,

    /**
     * The Trace.
     */
    Trace,

    /**
     * The Warn.
     */
    Warn;
  }

  /**
   * Error level.
   *
   * @return the level
   */
  Level errorLevel() default Level.Error;

  /**
   * Level.
   *
   * @return the level
   */
  Level level() default Level.Trace;

  /**
   * Log entry exit.
   *
   * @return true, if successful
   */
  boolean logEntryExit() default true;

  /**
   * Log error.
   *
   * @return true, if successful
   */
  boolean logError() default true;

  /**
   * Message.
   *
   * @return the string
   */
  String message() default "";

  /**
   * Skip arguments.
   *
   * @return true, if successful
   */
  boolean skipArguments() default false;

  /**
   * Skip results.
   *
   * @return true, if successful
   */
  boolean skipResults() default false;
}
