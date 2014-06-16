/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl.print;

import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.Locale;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xtradesoft.dlp.base.Context;
import com.xtradesoft.dlp.base.OperationResult;
import com.xtradesoft.dlp.impl.DLPOperation;

/**
 * The Class PrintOperation.
 */
public class PrintOperation extends DLPOperation {

  /**
   * The Class PrintJobWatcher.
   */
  public class PrintJobWatcher {

    /**
     * The done.
     */
    private boolean _done = false;

    /**
     * The print job completed.
     */
    public boolean _printJobCompleted;

    /**
     * Instantiates a new PrintJobWatcher.
     *
     * @param job the job
     */
    public PrintJobWatcher(final DocPrintJob job) {

      job.addPrintJobListener(new PrintJobAdapter() {

        void done() {

          synchronized (PrintJobWatcher.this) {
            setDone(true);
            PrintJobWatcher.this.notify();
          }
        }

        @Override
        public void printJobCanceled(PrintJobEvent pje) {

          logger.warn("failure, canceled print: {}, at printer: {}", getFile(), job.getPrintService());
          done();
        }

        @Override
        public void printJobCompleted(PrintJobEvent pje) {

          logger.info("success, completed print: {}, at printer: {}", getFile(), job.getPrintService());
          setPrintJobCompleted(true);
          done();
        }

        @Override
        public void printJobFailed(PrintJobEvent pje) {

          logger.error("failure, failed print: {}, at printer: {}", getFile(), job.getPrintService());
          done();
        }

        @Override
        public void printJobNoMoreEvents(PrintJobEvent pje) {

          logger.info("assume, completed print: {}, at printer: {}", getFile(), job.getPrintService());
          done();
        }
      });
    }

    /**
     * Checks if is done.
     *
     * @return true, if is done
     */
    public boolean isDone() {

      return _done;
    }

    /**
     * Checks if is prints the job completed.
     *
     * @return true, if is prints the job completed
     */
    public boolean isPrintJobCompleted() {

      return _printJobCompleted;
    }

    /**
     * Sets the done.
     *
     * @param done the new done
     */
    public void setDone(boolean done) {

      _done = done;
    }

    /**
     * Sets the prints the job completed.
     *
     * @param printJobCompleted the new prints the job completed
     */
    public void setPrintJobCompleted(boolean printJobCompleted) {

      _printJobCompleted = printJobCompleted;
    }

    /**
     * Wait for done.
     *
     * @return true, if successful
     */
    public synchronized boolean waitForDone() {

      try {
        while (!isDone()) {
          wait();
        }

      } catch (final InterruptedException e) {
      }
      return isPrintJobCompleted();
    }
  }

  /**
   * The Constant logger.
   */
  final static Logger logger = LoggerFactory.getLogger(PrintOperation.class);

  /**
   * The file.
   */
  private String _file;

  /**
   * _execute.
   *
   * @param context the context
   * @return the operation result
   * @throws Exception the exception
   */
  public OperationResult _execute(Context context) throws Exception {

    final PrintContext printContext = (PrintContext) context;

    PDDocument document = null;
    final boolean printCompleted = false;
    try {
      logger.debug("load: {}", getFile());
      document = PDDocument.load(getFile());
      final PrinterJob printJob = PrinterJob.getPrinterJob();
      printJob.setJobName(printContext.getFile());
      document.print(printJob);

    } catch (final IOException e) {
      logger.error("error, invalid {} contents - verify http/application login details...", getFile());
      throw e;
    } finally {
      if (document != null) {
        document.close();
      }
    }
    return new PrintOperationResult(getFile(), printCompleted);
  }

  /*
   * (non-Javadoc)
   * @see
   * com.xtradesoft.dlp.impl.DLPOperation#execute(com.xtradesoft.dlp.base.
   * Context)
   */
  @Override
  public OperationResult execute(Context context) throws Exception {

    if (null != System.getProperty("ShowPrintDialog")) {
      return _execute(context);
    }
    final PrintContext printContext = (PrintContext) context;

    PDDocument document = null;
    boolean printCompleted = false;
    try {
      logger.debug("load: {}", getFile());
      document = PDDocument.load(getFile());
      final SimpleDoc printable = new SimpleDoc(document, DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
      final DocPrintJob printJob = printContext.getService().createPrintJob();
      final PrintJobWatcher watcher = new PrintJobWatcher(printJob);
      final PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
      attrs.add(new JobName(getFile(), Locale.getDefault()));
      logger.info("success, requesting silent print: {}, at printer: {}", getFile(), printJob.getPrintService());
      printJob.print(printable, attrs);
      printCompleted = watcher.waitForDone();

    } catch (final IOException e) {
      logger.error("error, invalid {} contents - verify http/application login details...", getFile());
      throw e;
    } finally {
      if (document != null) {
        document.close();
      }
    }
    return new PrintOperationResult(getFile(), printCompleted);
  }

  /**
   * Gets the file.
   *
   * @return the file
   */
  public String getFile() {

    return _file;
  }

  /**
   * Sets the file.
   *
   * @param file the new file
   */
  public void setFile(String file) {

    _file = file;
  }

  /*
   * (non-Javadoc)
   * @see com.xtradesoft.dlp.impl.DLPOperation#toString()
   */
  @Override
  public String toString() {

    return String.format("PrintOperation: {file: %s, %s}", _file, super.toString());

  }
}
