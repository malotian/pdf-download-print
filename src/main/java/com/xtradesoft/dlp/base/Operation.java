/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Class Operation.
 */
public abstract class Operation {

  /**
   * The Class UniqueIdGenerator.
   */
  static class UniqueIdGenerator {

    /**
     * The counter.
     */
    private final AtomicInteger counter = new AtomicInteger();

    /**
     * Next id.
     *
     * @return the string
     */
    public String nextId() {

      return String.format("0x%09X", counter.incrementAndGet());
    }
  }

  /**
   * The id generator.
   */
  private static UniqueIdGenerator idGenerator = new UniqueIdGenerator();

  /**
   * The id.
   */
  private String _id;

  /**
   * Instantiates a new Operation.
   */
  public Operation() {

    setID(idGenerator.nextId());

  }

  /**
   * Execute.
   *
   * @param context the context
   * @return the operation result
   * @throws Exception the exception
   */
  public abstract OperationResult execute(Context context) throws Exception;

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getID() {

    return _id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  void setID(String id) {

    _id = id;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    return String.format("Operation: {ID: %s}", getID());

  }
}
