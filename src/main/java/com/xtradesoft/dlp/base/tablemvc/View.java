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

/**
 * The Class View.
 *
 * @param <T> the generic type
 */
@SuppressWarnings("serial")
public class View<T extends Model> extends JComponent {

  /**
   * The Interface Renderer.
   */
  public interface Renderer {

    /**
     * Prepare renderer.
     *
     * @param table     the table
     * @param component the component
     * @param renderer  the renderer
     * @param row       the row
     * @param column    the column
     * @return the component
     */
    public Component prepareRenderer(JTable table, Component component, TableCellRenderer renderer, int row,
                                     int column);
  }

  /**
   * The add button.
   */
  JButton _addButton;

  /**
   * The button panel.
   */
  JPanel _buttonPanel;

  /**
   * The delete button.
   */
  JButton _deleteButton;

  /**
   * The renderer.
   */
  Renderer _renderer;

  /**
   * The save button.
   */
  JButton _saveButton;

  /**
   * The table.
   */
  JTable _table;

  /**
   * Enhance.
   *
   * @param model the model
   */
  public void enhance(final T model) {

    for (int col = 0; col < model.getColumnCount(); ++col) {
      if (String.class != model.getColumnClass(col)) {
        continue;
      }

      if (!model.hasPossibleValues(col)) {
        continue;
      }

      final JComboBox<String> comboBox = new JComboBox<String>();
      final Set<Object> values = model.getPossibleValues(col);

      for (final Object value : values) {
        if (value instanceof String) {
          comboBox.addItem((String) value);
        }
      }

      if (comboBox.getItemCount() <= 0) {
        continue;
      }

      final TableColumn column = _table.getColumnModel().getColumn(col);
      column.setCellEditor(new DefaultCellEditor(comboBox));

    }
  }

  /**
   * Gets the table.
   *
   * @return the table
   */
  public JTable getTable() {

    return _table;
  }

  /**
   * Hide add delete save.
   */
  public void hideAddDeleteSave() {

    this.remove(_buttonPanel);
  }

  /**
   * Initialize.
   *
   * @param model the model
   */
  public void initialize(final T model) {

    setLayout(new BorderLayout(6, 6));

    _table = new JTable(model) {

      @Override
      public boolean getScrollableTracksViewportWidth() {

        return getRowCount() == 0 ? super.getScrollableTracksViewportWidth()
                : getPreferredSize().width < getParent().getWidth();
      }

      @Override
      public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {

        final Component component = super.prepareRenderer(renderer, row, column);

        final TableColumn tableColumn = getColumnModel().getColumn(column);

        tableColumn.setPreferredWidth(Math.max(component.getPreferredSize().width,
                tableColumn.getPreferredWidth()));
        tableColumn.setMinWidth(Math.max(component.getPreferredSize().width, tableColumn.getPreferredWidth()));

        if (null != _renderer) {
          _renderer.prepareRenderer(this, component, renderer, row, column);
        }
        return component;
      }
    };

    _table.setAutoCreateColumnsFromModel(true);
    _table.setFillsViewportHeight(true);

    final TableModelListener listener = new TableModelListener() {

      protected void invokeScroll() {

        SwingUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {

            final int last = _table.getModel().getRowCount() - 1;
            final Rectangle r = _table.getCellRect(last, 0, true);
            _table.scrollRectToVisible(r);
          }
        });
      }

      @Override
      public void tableChanged(TableModelEvent e) {

        if (e.getType() == TableModelEvent.INSERT) {
          invokeScroll();
        }
      }
    };

    _table.getModel().addTableModelListener(listener);

    enhance(model);

    final JScrollPane scrollpane = new JScrollPane(_table);
    scrollpane.setPreferredSize(new Dimension(900, 600));
    this.add(scrollpane, BorderLayout.CENTER);

    _buttonPanel = new JPanel();

    _addButton = new JButton("Add");
    _buttonPanel.add(_addButton);

    _deleteButton = new JButton("Delete");
    _buttonPanel.add(_deleteButton);

    _saveButton = new JButton("Save");
    _buttonPanel.add(_saveButton);

    setVisible(true);

  }

  /**
   * Register add listener.
   *
   * @param listener the listener
   */
  public void registerAddListener(ActionListener listener) {

    _addButton.addActionListener(listener);
  }

  /**
   * Register delete listener.
   *
   * @param listener the listener
   */
  public void registerDeleteListener(ActionListener listener) {

    _deleteButton.addActionListener(listener);
  }

  /**
   * Register save listener.
   *
   * @param listener the listener
   */
  public void registerSaveListener(ActionListener listener) {

    _saveButton.addActionListener(listener);
  }

  /**
   * Sets the renderer.
   *
   * @param _renderer the new renderer
   */
  public void setRenderer(Renderer _renderer) {

    this._renderer = _renderer;
  }

  /**
   * Show add delete save.
   */
  public void showAddDeleteSave() {

    this.add(_buttonPanel, BorderLayout.SOUTH);
  }
}
