/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Class ScheduleResults.
 */
@SuppressWarnings("serial")
public class ScheduleResults extends ArrayList<ScheduleResult> {

  /**
   * Instantiates a new ScheduleResults.
   */
  public ScheduleResults() {

  }

  /**
   * Instantiates a new ScheduleResults.
   *
   * @param results the results
   */
  private ScheduleResults(List<ScheduleResult> results) {

    super(results);
  }

  /**
   * Creates the synchronized.
   *
   * @return the schedule results
   */
  public ScheduleResults createSynchronized() {

    return new ScheduleResults(Collections.synchronizedList(this));

  }
}
