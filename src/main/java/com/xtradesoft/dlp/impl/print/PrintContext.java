/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl.print;

import java.net.URL;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xtradesoft.dlp.base.Context;
import com.xtradesoft.dlp.impl.DLPException;
import com.xtradesoft.dlp.loggable.annotation.Loggable;

/**
 * The Class PrintContext.
 */
public class PrintContext extends Context {

  /**
   * The Constant logger.
   */
  final static Logger logger = LoggerFactory.getLogger(PrintContext.class);

  /**
   * Gets the available services.
   *
   * @return the available services
   */
  public static PrintService[] getAvailableServices() {

    return PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
  }

  /**
   * The file.
   */
  private String _file = "";

  /**
   * The printer.
   */
  private String _printer = "";

  /**
   * The service.
   */
  PrintService _service;

  /**
   * The url.
   */
  private URL _url;

  /**
   * Instantiates a new PrintContext.
   *
   * @param url the url
   */
  public PrintContext(URL url) {

    _url = url;
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
   * Gets the printer.
   *
   * @return the printer
   */
  public String getPrinter() {

    return _printer;
  }

  /**
   * Gets the service.
   *
   * @return the service
   */
  public PrintService getService() {

    return _service;
  }

  /**
   * Gets the url.
   *
   * @return the url
   */
  public URL getURL() {

    return _url;
  }

  /**
   * Checks for print service.
   *
   * @return true, if successful
   */
  public boolean hasPrintService() {

    return (null != getService());

  }

  /**
   * Initialize.
   *
   * @return the prints the context
   * @throws DLPException the DLP exception
   */
  @Loggable(level = Loggable.Level.Debug)
  public PrintContext initialize() throws DLPException {

    setService(lookup(_printer));
    return this;
  }

  /**
   * Lookup.
   *
   * @param printerName the printer name
   * @return the prints the service
   * @throws DLPException the DLP exception
   */
  @Loggable(level = Loggable.Level.Debug)
  PrintService lookup(String printerName) throws DLPException {

    logger.debug("printer lookup: {}", printerName);

    for (final PrintService service : getAvailableServices()) {
      if (service.getName().equals(printerName)) {
        return service;
      }
    }

    logger.debug("failure, printer lookup: {}", printerName);
    for (final PrintService service : getAvailableServices()) {
      logger.info("available printer: {}", service.getName());
    }

    throw new DLPException("failure, printer lookup: " + printerName);
  }

  /**
   * Sets the file.
   *
   * @param file the new file
   */
  public void setFile(String file) {

    _file = file;
  }

  /**
   * Sets the printer.
   *
   * @param printer the new printer
   */
  public void setPrinter(String printer) {

    _printer = printer;
  }

  /**
   * Sets the service.
   *
   * @param service the new service
   */
  public void setService(PrintService service) {

    _service = service;
  }

  /**
   * Sets the url.
   *
   * @param url the new url
   */
  public void setURL(URL url) {

    _url = url;
  }
}
