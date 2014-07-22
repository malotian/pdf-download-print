/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.log;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.xtradesoft.dlp.base.tablemvc.View.Renderer;

/**
 * The Class LogRenderer.
 */
public class LogRenderer implements Renderer {

    /*
     * (non-Javadoc)
     * @see
     * com.xtradesoft.dlp.base.tablemvc.View.Renderer#prepareRenderer(javax.
     * swing.JTable, java.awt.Component, javax.swing.table.TableCellRenderer,
     * int, int)
     */
    @Override
    public Component prepareRenderer(JTable table, Component component, TableCellRenderer renderer, int row, int column) {

        if (!table.isRowSelected(row)) {
            final int modelRow = table.convertRowIndexToModel(row);
            final String type = ((String) table.getModel().getValueAt(modelRow, 0)).trim();
            component.setForeground(Color.BLACK);
            if ("ERROR".equals(type)) {
                component.setBackground(new Color(255, 69, 0));
            } else if ("WARN".equals(type)) {
                component.setBackground(new Color(255, 255, 0));
            } else if ("INFO".equals(type)) {
                component.setBackground(new Color(0, 255, 255));
            } else if ("DEBUG".equals(type)) {
                component.setBackground(new Color(0, 255, 0));
            } else if ("ALL".equals(type)) {
                component.setBackground(Color.green);
            }
        }

        return null;
    }

}
