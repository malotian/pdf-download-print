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
         * The Class PrintJobAdapterEx.
         */
        private final class PrintJobAdapterEx extends PrintJobAdapter {

            /** The job. */
            private final DocPrintJob job;

            /**
             * Instantiates a new PrintJobAdapterEx.
             * 
             * @param job
             *            the job
             */
            private PrintJobAdapterEx(DocPrintJob job) {

                this.job = job;
            }

            /**
             * Done.
             */
            void done() {

                synchronized (PrintJobWatcher.this) {
                    setDone(true);
                    PrintJobWatcher.this.notify();
                }
            }

            /* 
             * (non-Javadoc)
             * @see javax.print.event.PrintJobAdapter#printJobCanceled(javax.print.event.PrintJobEvent)
             */
            @Override
            public void printJobCanceled(PrintJobEvent pje) {

                LOGGER.warn("failure, canceled print: {}, at printer: {}", getFile(), job.getPrintService());
                done();
            }

            /* 
             * (non-Javadoc)
             * @see javax.print.event.PrintJobAdapter#printJobCompleted(javax.print.event.PrintJobEvent)
             */
            @Override
            public void printJobCompleted(PrintJobEvent pje) {

                LOGGER.info("success, completed print: {}, at printer: {}", getFile(), job.getPrintService());
                setPrintJobCompleted(true);
                done();
            }

            /* 
             * (non-Javadoc)
             * @see javax.print.event.PrintJobAdapter#printJobFailed(javax.print.event.PrintJobEvent)
             */
            @Override
            public void printJobFailed(PrintJobEvent pje) {

                LOGGER.error("failure, failed print: {}, at printer: {}", getFile(), job.getPrintService());
                done();
            }

            /* 
             * (non-Javadoc)
             * @see javax.print.event.PrintJobAdapter#printJobNoMoreEvents(javax.print.event.PrintJobEvent)
             */
            @Override
            public void printJobNoMoreEvents(PrintJobEvent pje) {

                LOGGER.info("assume, completed print: {}, at printer: {}", getFile(), job.getPrintService());
                done();
            }
        }

        /** The done. */
        private boolean done = false;

        /** The print job completed. */
        private boolean printJobCompleted;

        /**
         * Instantiates a new PrintJobWatcher.
         * 
         * @param job
         *            the job
         */
        public PrintJobWatcher(final DocPrintJob job) {

            job.addPrintJobListener(new PrintJobAdapterEx(job));
        }

        /**
         * Checks if is the done.
         * 
         * @return the done
         */
        public boolean isDone() {

            return done;
        }

        /**
         * Checks if is the print job completed.
         * 
         * @return the print job completed
         */
        public boolean isPrintJobCompleted() {

            return printJobCompleted;
        }

        /**
         * Sets the done.
         * 
         * @param done
         *            the new done
         */
        public void setDone(boolean done) {

            this.done = done;
        }

        /**
         * Sets the print job completed.
         * 
         * @param printJobCompleted
         *            the new print job completed
         */
        public void setPrintJobCompleted(boolean printJobCompleted) {

            this.printJobCompleted = printJobCompleted;
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
                LOGGER.error(e.getMessage(), e);
            }
            return isPrintJobCompleted();
        }
    }

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PrintOperation.class);

    /** The file. */
    private String file;

    /*
     * (non-Javadoc)
     * @see
     * com.xtradesoft.dlp.impl.DLPOperation#execute(com.xtradesoft.dlp.base.
     * Context)
     */
    @Override
    public OperationResult execute(Context context) throws Exception {

        if (null != System.getProperty("ShowPrintDialog")) {
            return executeEx(context);
        }

        final PrintContext printContext = (PrintContext) context;

        PDDocument document = null;
        boolean printCompleted = false;
        try {
            LOGGER.debug("load: {}", getFile());
            document = PDDocument.load(getFile());
            final SimpleDoc printable = new SimpleDoc(document, DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
            final DocPrintJob printJob = printContext.getService().createPrintJob();
            final PrintJobWatcher watcher = new PrintJobWatcher(printJob);
            final PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
            attrs.add(new JobName(getFile(), Locale.getDefault()));
            LOGGER.info("success, requesting silent print: {}, at printer: {}", getFile(), printJob.getPrintService());
            printJob.print(printable, attrs);
            printCompleted = watcher.waitForDone();

        } catch (final IOException e) {
            LOGGER.error("error, invalid {} contents - verify http/application login details", getFile());
            throw e;
        } finally {
            if (document != null) {
                document.close();
            }
        }
        return new PrintOperationResult(getFile(), printCompleted);
    }

    /**
     * Execute ex.
     * 
     * @param context
     *            the context
     * @return the operation result
     * @throws Exception
     *             the exception
     */
    public OperationResult executeEx(Context context) throws Exception {

        final PrintContext printContext = (PrintContext) context;

        PDDocument document = null;
        final boolean printCompleted = false;
        try {
            LOGGER.debug("load file: {} ", getFile());
            document = PDDocument.load(getFile());
            final PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setJobName(printContext.getFile());
            document.print(printJob);

        } catch (final IOException e) {
            LOGGER.error("error, invalid {} contents - verify http/application login details.", getFile());
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

        return file;
    }

    /**
     * Sets the file.
     * 
     * @param file
     *            the new file
     */
    public void setFile(String file) {

        this.file = file;
    }

    /*
     * (non-Javadoc)
     * @see com.xtradesoft.dlp.impl.DLPOperation#toString()
     */
    @Override
    public String toString() {

        return String.format("PrintOperation: {file: %s, %s}", file, super.toString());

    }
}
