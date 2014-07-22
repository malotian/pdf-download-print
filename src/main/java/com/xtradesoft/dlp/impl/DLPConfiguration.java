/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.impl;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.print.PrintService;

import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xtradesoft.dlp.base.Configuration;
import com.xtradesoft.dlp.base.tablemvc.Model;
import com.xtradesoft.dlp.base.tablemvc.Row;
import com.xtradesoft.dlp.base.tablemvc.Rows;
import com.xtradesoft.dlp.loggable.annotation.Loggable;

/**
 * The Class DLPConfiguration.
 */
public class DLPConfiguration extends Configuration {

    /**
     * The Class DLPConfigurationModel.
     */
    @SuppressWarnings("serial")
    class DLPConfigurationModel extends Model {

        /**
         * Instantiates a new DLPConfigurationModel.
         * 
         * @param downloadConfig
         *            the download config
         */
        public DLPConfigurationModel(String downloadConfig) {

            super(downloadConfig);

            for (final String name : Fields.names()) {
                addColumn(name);
            }

            final Set<Object> printers = new HashSet<Object>();
            for (final PrintService service : PrinterJob.lookupPrintServices()) {
                printers.add(service.getName());
            }

            setPossibleValues(Fields.Printer.value(), printers);
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {

            Class<?> colClass = null;
            switch (Fields.from(columnIndex)) {

                case DownloadURL:
                    colClass = URL.class;
                    break;
                case Printer:
                    colClass = String.class;
                    break;
                case AppLoginPostURL:
                    colClass = URL.class;
                    break;
                case ProxyHost:
                    colClass = HttpHost.class;
                    break;
                case ProxyPort:
                    colClass = Integer.class;
                    break;
                case MaxBackups:
                    colClass = Integer.class;
                    break;
                case PollInterval:
                    colClass = Integer.class;
                    break;
                default:
                    colClass = super.getColumnClass(columnIndex);
            }
            return null != colClass ? colClass : super.getColumnClass(columnIndex);

        }

        /*
         * (non-Javadoc)
         * @see com.xtradesoft.dlp.base.tablemvc.Model#save()
         */
        @Override
        public void save() {

            super.save();
            notifyObservers();
        }

    }

    /**
     * The Enum Fields.
     */
    public enum Fields {

        /** The App login post url. */
        AppLoginPostURL(4),

        /** The App password. */
        AppPassword(6),

        /** The App username. */
        AppUsername(5),

        /** The Download url. */
        DownloadURL(0),

        /** The Http password. */
        HttpPassword(3),

        /** The Http username. */
        HttpUsername(2),

        /** The Max backups. */
        MaxBackups(9),

        /** The Poll interval. */
        PollInterval(10),

        /** The Printer. */
        Printer(1),

        /** The Proxy host. */
        ProxyHost(7),

        /** The Proxy port. */
        ProxyPort(8);

        /** The Constant _map. */
        private static final SortedMap<Integer, Fields> _map = new TreeMap<Integer, Fields>();

        static {
            for (final Fields field : Fields.values()) {
                _map.put(field.value(), field);
            }
        }

        /**
         * From.
         * 
         * @param value
         *            the value
         * @return the fields
         */
        public static Fields from(int value) {

            return _map.get(value);
        }

        /**
         * Names.
         * 
         * @return the list
         */
        public static List<String> names() {

            final List<String> names = new ArrayList<String>();
            for (final Fields field : _map.values()) {
                names.add(field.name());
            }
            return names;
        }

        /** The column index. */
        private final int columnIndex;

        /**
         * Instantiates a new Fields.
         * 
         * @param index
         *            the index
         */
        Fields(int index) {

            columnIndex = index;
        }

        /**
         * Value.
         * 
         * @return the int
         */
        public int value() {

            return columnIndex;
        }

    }

    /**
     * The Class Record.
     */
    public static class Record {

        /** The row. */
        com.xtradesoft.dlp.base.tablemvc.Row<Model> row;

        /**
         * Instantiates a new Record.
         * 
         * @param row
         *            the row
         */
        public Record(Row<Model> row) {

            this.row = row;
        }

        /**
         * App login post url.
         * 
         * @return the url
         */
        public URL AppLoginPostURL() {

            try {
                return new URL(row.get(Fields.AppLoginPostURL.value()));
            } catch (final MalformedURLException e) {
                LOGGER.error("configuration error, AppLoginPostURL", e);
                return null;
            }

        }

        /**
         * App password.
         * 
         * @return the string
         */
        public String AppPassword() {

            return row.get(Fields.AppPassword.value());
        }

        /**
         * App username.
         * 
         * @return the string
         */
        public String AppUsername() {

            return row.get(Fields.AppUsername.value());
        }

        /**
         * Download url.
         * 
         * @return the url
         */
        public URL DownloadURL() {

            try {
                return new URL(row.get(Fields.DownloadURL.value()));
            } catch (final MalformedURLException e) {
                LOGGER.error("configuration error, DownloadURL", e);
                return null;
            }
        }

        /**
         * Checks for.
         * 
         * @param field
         *            the field
         * @return true, if successful
         */
        boolean has(Fields field) {

            return row.has(field.value());
        }

        /**
         * Checks for app login post url.
         * 
         * @return true, if successful
         */
        public boolean hasAppLoginPostURL() {

            return has(Fields.AppLoginPostURL);
        }

        /**
         * Checks for max backup.
         * 
         * @return true, if successful
         */
        public boolean hasMaxBackup() {

            return has(Fields.MaxBackups);
        }

        /**
         * Checks for poll interval.
         * 
         * @return true, if successful
         */
        public boolean HasPollInterval() {

            return has(Fields.PollInterval);
        }

        /**
         * Checks for proxy port.
         * 
         * @return true, if successful
         */
        public boolean hasProxyPort() {

            return has(Fields.ProxyPort);
        }

        /**
         * Http password.
         * 
         * @return the string
         */
        public String HttpPassword() {

            return row.get(Fields.HttpPassword.value());
        }

        /**
         * Http username.
         * 
         * @return the string
         */
        public String HttpUsername() {

            return row.get(Fields.HttpUsername.value());
        }

        /**
         * Max backup.
         * 
         * @return the int
         */
        public int MaxBackup() {

            return Integer.parseInt(row.get(Fields.MaxBackups.value()));

        }

        /**
         * Poll interval.
         * 
         * @return the int
         */
        public int PollInterval() {

            return Integer.parseInt(row.get(Fields.PollInterval.value()));
        }

        /**
         * Printer.
         * 
         * @return the string
         */
        public String Printer() {

            return row.get(Fields.Printer.value());
        }

        /**
         * Proxy host.
         * 
         * @return the string
         */
        public String ProxyHost() {

            return row.get(Fields.ProxyHost.value());
        }

        /**
         * Proxy port.
         * 
         * @return the int
         */
        public int ProxyPort() {

            return Integer.parseInt(row.get(Fields.ProxyPort.value()));
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {

            final StringBuilder helper = new StringBuilder();

            for (final Fields field : Fields.values()) {
                if (0 != helper.length()) {
                    helper.append(", ");
                }
                helper.append("{" + field.name() + "=" + row.get(field.value()) + "}");
            }

            return getClass().getName() + "[" + helper.toString() + "]";
        }

        /**
         * Validate.
         * 
         * @return true, if successful
         */
        public boolean validate() {

            boolean valid = true;
            try {
                if (null == DownloadURL()) {
                    valid = false;
                }
                if (hasAppLoginPostURL()) {
                    AppLoginPostURL();
                }
                if (has(Fields.ProxyPort) && ProxyPort() <= 0) {
                    valid = false;
                }
                if (has(Fields.MaxBackups) && MaxBackup() < 5) {
                    LOGGER.error("configuration error, MaxBackup should be >= 5");
                    valid = false;
                }
            } catch (final Exception e) {
                LOGGER.error("configuration error, invalid record {}, exception: {}", this, e);
            }
            return valid;
        }
    }

    /** The Constant APPLICATION_PROPERTIES_FILENAME. */
    private static final String APPLICATION_PROPERTIES_FILENAME = "application.properties";

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DLPConfiguration.class);

    /** The model. */
    private DLPConfigurationModel model;

    /** The properties. */
    Properties properties;

    /**
     * Instantiates a new DLPConfiguration.
     */
    public DLPConfiguration() {

        properties = new Properties();
    }

    /**
     * Gets the download input file.
     * 
     * @return the download input file
     */
    public String getDownloadInputFile() {

        return (String) properties.get("download.input.file");
    }

    /**
     * Gets the http server host.
     * 
     * @return the http server host
     */
    public String getHttpServerHost() {

        return (String) properties.get("http.server.host");
    }

    /**
     * Gets the http server port.
     * 
     * @return the http server port
     */
    public int getHttpServerPort() {

        return Integer.parseInt((String) properties.get("http.server.port"));
    }

    /**
     * Gets the http service context.
     * 
     * @return the http service context
     */
    public String getHttpServiceContext() {

        return (String) properties.get("http.service.context");
    }

    /**
     * Gets the model.
     * 
     * @return the model
     */
    public DLPConfigurationModel getModel() {

        return model;
    }

    /**
     * Initialize.
     * 
     * @return the DLP configuration
     * @throws Exception
     *             the exception
     */
    public DLPConfiguration initialize() throws Exception {

        properties.load(new FileReader(APPLICATION_PROPERTIES_FILENAME));
        model = new DLPConfigurationModel(getDownloadInputFile());
        model.load();
        return this;
    }

    /**
     * Validate.
     * 
     * @return true, if successful
     */
    @Loggable(level = Loggable.Level.Debug)
    public boolean validate() {

        boolean valid = true;

        try {
            if (getHttpServerPort() <= 0) {
                valid = false;
                LOGGER.error("configuration error, invalid http.server.port: {}", getHttpServerPort());
            }

            if (getHttpServerHost().isEmpty()) {
                valid = false;
                LOGGER.error("configuration error, invalid http.server.host: {}", getHttpServerHost());
            }

            if (getHttpServiceContext().isEmpty()) {
                valid = false;
                LOGGER.error("configuration error, invalid http.service.context: {}", getHttpServiceContext());
            }

            if (!new File(getDownloadInputFile()).exists()) {
                LOGGER.warn("configuration warning, invalid download input(doesn't exist)...");
            }
        } catch (final NumberFormatException e) {
            valid = false;
            LOGGER.error("configuration error, no or invalid http port...");
        } catch (final Exception e) {
            valid = false;
            LOGGER.error("configuration error, verify {}, exception: {}", APPLICATION_PROPERTIES_FILENAME, e);
        }

        return valid;
    }

    /**
     * Validate ex.
     * 
     * @return true, if successful
     */
    @Loggable(level = Loggable.Level.Debug)
    boolean validateEx() {

        boolean valid = true;

        final Rows<Model> rows = new Rows<Model>(getModel());
        while (rows.hasNext()) {
            final DLPConfiguration.Record record = new DLPConfiguration.Record(rows.next());
            LOGGER.debug("configuration entry: {}", record);

            if (!record.validate()) {
                LOGGER.warn("configuration error, invalid record {}", this);
                valid = false;
            }
        }
        return valid;
    }
}
