/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base.tablemvc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * The Class View.
 * 
 * @param <T>
 *            the generic type
 */
@SuppressWarnings("serial")
public class View<T extends Model> extends JComponent {

    /**
     * The Class JTableEx.
     */
    private final class JTableEx extends JTable {

        /**
         * Instantiates a new JTableEx.
         * 
         * @param dm
         *            the dm
         */
        private JTableEx(TableModel dm) {

            super(dm);
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.JTable#getScrollableTracksViewportWidth()
         */
        @Override
        public boolean getScrollableTracksViewportWidth() {

            return getRowCount() == 0 ? super.getScrollableTracksViewportWidth()
                    : getPreferredSize().width < getParent().getWidth();
        }

        /*
         * (non-Javadoc)
         * @see
         * javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer
         * , int, int)
         */
        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {

            final Component component = super.prepareRenderer(renderer, row, column);

            final TableColumn tableColumn = getColumnModel().getColumn(column);

            tableColumn
                    .setPreferredWidth(Math.max(component.getPreferredSize().width, tableColumn.getPreferredWidth()));
            tableColumn.setMinWidth(Math.max(component.getPreferredSize().width, tableColumn.getPreferredWidth()));

            if (null != View.this.renderer) {
                View.this.renderer.prepareRenderer(this, component, renderer, row, column);
            }
            return component;
        }
    }

    /**
     * The Interface Renderer.
     */
    public interface Renderer {

        /**
         * Prepare renderer.
         * 
         * @param table
         *            the table
         * @param component
         *            the component
         * @param renderer
         *            the renderer
         * @param row
         *            the row
         * @param column
         *            the column
         * @return the component
         */
        public Component prepareRenderer(JTable table, Component component, TableCellRenderer renderer, int row,
                int column);
    }

    /**
     * The Class TableModelListenerEx.
     */
    private final class TableModelListenerEx implements TableModelListener {

        /**
         * Invoke scroll.
         */
        protected void invokeScroll() {

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {

                    final int last = View.this.table.getModel().getRowCount() - 1;
                    final Rectangle r = View.this.table.getCellRect(last, 0, true);
                    View.this.table.scrollRectToVisible(r);
                }
            });
        }

        /*
         * (non-Javadoc)
         * @see
         * javax.swing.event.TableModelListener#tableChanged(javax.swing.event
         * .TableModelEvent)
         */
        @Override
        public void tableChanged(TableModelEvent e) {

            if (e.getType() == TableModelEvent.INSERT) {
                invokeScroll();
            }
        }
    }

    /** The add button. */
    JButton addButton;

    /** The button panel. */
    JPanel buttonPanel;

    /** The delete button. */
    JButton deleteButton;

    /** The renderer. */
    Renderer renderer;

    /** The save button. */
    JButton saveButton;

    /** The table. */
    JTable table;

    /**
     * Enhance.
     * 
     * @param model
     *            the model
     */
    public void enhance(final T model) {

        for (int col = 0; col < model.getColumnCount(); ++col) {
            restrictToPossibleValuesIfNeeded(model, col);
        }
    }

    /**
     * Gets the table.
     * 
     * @return the table
     */
    public JTable getTable() {

        return this.table;
    }

    /**
     * Hide add delete save.
     */
    public void hideAddDeleteSave() {

        this.remove(this.buttonPanel);
    }

    /**
     * Initialize.
     * 
     * @param model
     *            the model
     */
    public void initialize(final T model) {

        setLayout(new BorderLayout(6, 6));

        this.table = new JTableEx(model);

        this.table.setAutoCreateColumnsFromModel(true);
        this.table.setFillsViewportHeight(true);

        final TableModelListener listener = new TableModelListenerEx();

        this.table.getModel().addTableModelListener(listener);

        enhance(model);

        final JScrollPane scrollpane = new JScrollPane(this.table);
        scrollpane.setPreferredSize(new Dimension(900, 600));
        this.add(scrollpane, BorderLayout.CENTER);

        this.buttonPanel = new JPanel();

        this.addButton = new JButton("Add");
        this.buttonPanel.add(this.addButton);

        this.deleteButton = new JButton("Delete");
        this.buttonPanel.add(this.deleteButton);

        this.saveButton = new JButton("Save");
        this.buttonPanel.add(this.saveButton);

        setVisible(true);

    }

    /**
     * Register add listener.
     * 
     * @param listener
     *            the listener
     */
    public void registerAddListener(ActionListener listener) {

        this.addButton.addActionListener(listener);
    }

    /**
     * Register delete listener.
     * 
     * @param listener
     *            the listener
     */
    public void registerDeleteListener(ActionListener listener) {

        this.deleteButton.addActionListener(listener);
    }

    /**
     * Register save listener.
     * 
     * @param listener
     *            the listener
     */
    public void registerSaveListener(ActionListener listener) {

        this.saveButton.addActionListener(listener);
    }

    /**
     * Restrict to possible values if needed.
     * 
     * @param model
     *            the model
     * @param col
     *            the col
     */
    public void restrictToPossibleValuesIfNeeded(final T model, int col) {

        if (String.class != model.getColumnClass(col)) {
            return;
        }

        if (!model.hasPossibleValues(col)) {
            return;
        }

        final JComboBox<String> comboBox = new JComboBox<String>();
        final Set<Object> values = model.getPossibleValues(col);

        for (final Object value : values) {
            if (value instanceof String) {
                comboBox.addItem((String) value);
            }
        }

        if (comboBox.getItemCount() <= 0) {
            return;
        }

        final TableColumn column = this.table.getColumnModel().getColumn(col);
        column.setCellEditor(new DefaultCellEditor(comboBox));
    }

    /**
     * Sets the renderer.
     * 
     * @param renderer
     *            the new renderer
     */
    public void setRenderer(Renderer renderer) {

        this.renderer = renderer;
    }

    /**
     * Show add delete save.
     */
    public void showAddDeleteSave() {

        this.add(this.buttonPanel, BorderLayout.SOUTH);
    }
}
