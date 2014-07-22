/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base.tablemvc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Model.
 */
@SuppressWarnings("serial")
public class Model extends AbstractTableModel {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Model.class);

    /** The data. */
    private final List<List<Object>> data;

    /** The file name. */
    private String fileName;

    /** The names. */
    private final List<String> names;

    /** The possible values. */
    private final List<Set<Object>> possibleValues;

    /**
     * Instantiates a new Model.
     */
    public Model() {

        super();
        names = new ArrayList<String>();
        data = new ArrayList<List<Object>>();
        possibleValues = new ArrayList<Set<Object>>();
    }

    /**
     * Instantiates a new Model.
     * 
     * @param fileName
     *            the file name
     */
    public Model(String fileName) {

        super();
        names = new ArrayList<String>();
        data = new ArrayList<List<Object>>();
        possibleValues = new ArrayList<Set<Object>>();
        this.fileName = fileName;
    }

    /**
     * Adds the column.
     * 
     * @param name
     *            the name
     */
    public void addColumn(String name) {

        names.add(name);
        possibleValues.add(null);
        fireTableStructureChanged();
    }

    /**
     * Adds the row.
     * 
     * @return the int
     */
    public int addRow() {

        final List<Object> row = new ArrayList<Object>();
        for (int i = 0; i < names.size(); ++i) {
            row.add("");
        }

        return addRow(row);
    }

    /**
     * Adds the row.
     * 
     * @param row
     *            the row
     * @return the int
     */
    public int addRow(List<Object> row) {

        data.add(row);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
        return data.size() - 1;
    }

    /**
     * Delete row.
     * 
     * @param row
     *            the row
     */
    public void deleteRow(int row) {

        if (row == -1) {
            return;
        }

        data.remove(row);
        fireTableRowsDeleted(row, row);
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {

        return names.size();
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int col) {

        return names.get(col);
    }

    /**
     * Gets the possible values.
     * 
     * @param col
     *            the col
     * @return the possible values
     */
    public Set<Object> getPossibleValues(int col) {

        return possibleValues.get(col);
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {

        return data.size();
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int row, int col) {

        final List<Object> rowList = data.get(row);
        Object result = null;
        if (col < rowList.size()) {
            result = rowList.get(col);
        }

        return result;
    }

    /**
     * Checks for possible values.
     * 
     * @param col
     *            the col
     * @return true, if successful
     */
    public boolean hasPossibleValues(int col) {

        return null != possibleValues.get(col) && !possibleValues.get(col).isEmpty();
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int row, int col) {

        return true;
    }

    /**
     * Load.
     */
    public void load() {

        if (null == fileName || fileName.isEmpty()) {
            return;
        }

        final File configFile = new File(fileName);
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (final IOException e) {
                LOGGER.error("failure, creating file: {}, exception: {}", configFile.getAbsoluteFile(), e);
            }
        }

        loadFile(new File(fileName));
    }

    /**
     * Load.
     * 
     * @param properties
     *            the properties
     */
    public void load(Properties properties) {

        for (int r = 0; !properties.isEmpty(); ++r) {
            final List<Object> record = new ArrayList<Object>();
            for (int i = 0; i < names.size(); i++) {
                record.add(properties.get(r + "." + names.get(i)));
                if (properties.containsKey(r + "." + names.get(i))) {
                    properties.remove(r + "." + names.get(i));
                }
            }
            data.add(record);
        }

        fireTableStructureChanged();
    }

    /**
     * Load file.
     * 
     * @param file
     *            the file
     */
    public void loadFile(File file) {

        try {
            final Properties properties = new Properties();
            properties.load(new FileReader(file));
            load(properties);

        } catch (final IOException e) {
            LOGGER.error("failure, loading properties file: {}, exception: {}", file.getAbsolutePath(), e);
        }
    }

    /**
     * Properties.
     * 
     * @return the properties
     */
    public Properties properties() {

        fireTableDataChanged();

        final Properties properties = new Properties() {

            @Override
            public synchronized Enumeration<Object> keys() {

                return Collections.enumeration(new TreeSet<Object>(super.keySet()));
            }

        };

        for (int r = 0; r < data.size(); r++) {
            final List<Object> record = data.get(r);
            for (int i = 0; i < record.size(); ++i) {
                properties.put(new String(r + "." + names.get(i)),
                        null != record.get(i) ? String.valueOf(record.get(i)) : "");
            }
        }

        return properties;

    }

    /**
     * Save.
     */
    public void save() {

        saveToFile(new File(fileName), properties());
    }

    /**
     * Save to file.
     * 
     * @param file
     *            the file
     * @param properties
     *            the properties
     */
    public void saveToFile(File file, Properties properties) {

        try {
            fireTableDataChanged();
            properties.store(new FileWriter(file), "test");
        } catch (final IOException e) {
            LOGGER.error("failure, saving properties file: {}, exception: {}", file.getAbsolutePath(), e);
        }
    }

    /**
     * Sets the possible values.
     * 
     * @param col
     *            the col
     * @param values
     *            the values
     */
    public void setPossibleValues(int col, Set<Object> values) {

        possibleValues.set(col, values);
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
     * int, int)
     */
    @Override
    public void setValueAt(Object value, int row, int col) {

        final List<Object> rowList = data.get(row);

        if (col >= rowList.size()) {
            while (col >= rowList.size()) {
                rowList.add(null);
            }
        }

        rowList.set(col, value);

        fireTableCellUpdated(row, col);
    }
}
