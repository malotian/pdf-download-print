/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl.print;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.print.DocPrintJob;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

import org.apache.http.client.utils.URIBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.Orientation;
import org.apache.pdfbox.printing.PDFPrinter;
import org.apache.pdfbox.printing.Scaling;
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
             * @see
             * javax.print.event.PrintJobAdapter#printJobCanceled(javax.print
             * .event.PrintJobEvent)
             */
            @Override
            public void printJobCanceled(PrintJobEvent pje) {

                LOGGER.warn("failure, canceled print: {}, at printer: {}", getFile(), job.getPrintService());
                done();
            }

            /*
             * (non-Javadoc)
             * @see
             * javax.print.event.PrintJobAdapter#printJobCompleted(javax.print
             * .event.PrintJobEvent)
             */
            @Override
            public void printJobCompleted(PrintJobEvent pje) {

                LOGGER.info("success, completed print: {}, at printer: {}", getFile(), job.getPrintService());
                setPrintJobCompleted(true);
                done();
            }

            /*
             * (non-Javadoc)
             * @see
             * javax.print.event.PrintJobAdapter#printJobFailed(javax.print.
             * event.PrintJobEvent)
             */
            @Override
            public void printJobFailed(PrintJobEvent pje) {

                LOGGER.error("failure, failed print: {}, at printer: {}", getFile(), job.getPrintService());
                done();
            }

            /*
             * (non-Javadoc)
             * @see
             * javax.print.event.PrintJobAdapter#printJobNoMoreEvents(javax.
             * print.event.PrintJobEvent)
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

    /** The lookup default. */
    boolean lookupDefault = false;

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
			document = PDDocument.load(new File(getFile()));
			PrinterJob printerJob = PrinterJob.getPrinterJob();
			printerJob.setPrintService(printContext.getService());
			PDFPrinter pdfPrinter = new PDFPrinter(document, printerJob, Scaling.SCALE_TO_FIT, Orientation.AUTO, null, false, 0.0F);
			pdfPrinter.silentPrint();

		} catch (final IOException e) {
			e.printStackTrace();
			LOGGER.error(
					"error, invalid {} contents - verify http/application login details",
					getFile());
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
            document = PDDocument.load(new File(getFile()));
            final PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setJobName(printContext.getFile());
            PDFPrinter pdfPrinter = new PDFPrinter(document);
            pdfPrinter.print(printJob);

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
     * Lookup url.
     * 
     * @return the url
     */
    public URL lookupURL() {

        try {
            final URIBuilder helper = new URIBuilder(getURL().toURI());
            helper.removeQuery();

            if (shallLookupdefault()) {
                final String[] pathTokens = getURL().getPath().split("/");
                if (lookupDefault && pathTokens.length >= 2) {
                    helper.setPath("/" + pathTokens[1]);
                }
            }

            return helper.build().toURL();

        } catch (final URISyntaxException e) {
            LOGGER.error("no or invalid url: {}, error: {}", getURL(), e);
        } catch (final MalformedURLException e) {
            LOGGER.error("no or invalid url: {}, error: {}", getURL(), e);
        }

        return null;
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

    /**
     * Sets the lookup default.
     * 
     * @param lookupDefault
     *            the new lookup default
     */
    public void setLookupDefault(boolean lookupDefault) {

        this.lookupDefault = lookupDefault;

    }

    /**
     * Shall lookupdefault.
     * 
     * @return true, if successful
     */
    boolean shallLookupdefault() {

        return lookupDefault;

    }

    /*
     * (non-Javadoc)
     * @see com.xtradesoft.dlp.impl.DLPOperation#toString()
     */
    @Override
    public String toString() {

        return String.format("PrintOperation: {file: %s, %s}", file, super.toString());

    }
    
//    /**
//     * Converts a given page range of a PDF document to bitmap images.
//     * @param document the PDF document
//     * @param folder 
//     * @param imageFormat the target format (ex. "png")
//     * @param outputPrefix used to construct the filename for the individual images
//     * @return true if the images were produced, false if there was an error
//     * @throws IOException if an I/O error occurs
//     * @throws PdfToFlashException 
//     */
//    protected List<String> writeImages(PDDocument document, File folder, String imageFormat, String outputPrefix) throws IOException, PdfToFlashException
//    {
//        return writeImages(document, folder, imageFormat, "", 1, Integer.MAX_VALUE, outputPrefix, BufferedImage.TYPE_INT_RGB, 96, 1.0f);
//    }
//    
//    /**
//     * Converts a given page range of a PDF document to bitmap images.
//     * @param document the PDF document
//     * @param folder 
//     * @param imageFormat the target format (ex. "png")
//     * @param password the password (needed if the PDF is encrypted)
//     * @param startPage the start page (1 is the first page)
//     * @param endPage the end page (set to Integer.MAX_VALUE for all pages)
//     * @param outputPrefix used to construct the filename for the individual images
//     * @param imageType the image type (see {@link BufferedImage}.TYPE_*)
//     * @param resolution the resolution in dpi (dots per inch)
//     * @param quality the image compression quality (0 < quality < 1.0f).
//     * @return true if the images were produced, false if there was an error
//     * @throws IOException if an I/O error occurs
//     * @throws PdfToFlashException 
//     */
//    protected List<String> writeImages(PDDocument document, File folder, String imageFormat, String password, int startPage, int endPage, String outputPrefix, int imageType, int resolution, float quality) throws IOException, PdfToFlashException
//    {
//        List<String> fileNames = new ArrayList<String>();
//        
//        List pages = document.getDocumentCatalog().getAllPages();
//        int digitCount = Integer.toString(pages.size()).length();
//        String format = "%0" + digitCount + "d." + imageFormat;
//        for (int i = startPage - 1; i < endPage && i < pages.size(); i++)
//        {
//            PDPage page = (PDPage) pages.get(i);
//            BufferedImage image = page.convertToImage(imageType, resolution);
//            String fileName = outputPrefix + String.format(format, i + 1, imageFormat);
//            fileNames.add(fileName);
//            
//			boolean foundWriter = ImageIOUtil.writeImage(image, imageFormat, new FileOutputStream(fileName), resolution, quality);
//            
//            if (!foundWriter)
//            {
//                throw new RuntimeException("No writer found for format '" + imageFormat + "'");
//            }
//        }
//        
//        return fileNames;
//    }    
}
