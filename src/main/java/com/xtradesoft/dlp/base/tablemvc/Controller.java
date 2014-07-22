/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.base.tablemvc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The Class Controller.
 * 
 * @param <T>
 *            the generic type
 */
public class Controller<T extends Model> {

    /**
     * The listener interface for receiving add events. The class that is
     * interested in processing a add event implements this interface, and the
     * object created with that class is registered with a component using the
     * component's <code>addAddListener<code> method. When
     * the add event occurs, that object's appropriate
     * method is invoked.
     * 
     * @see AddEvent
     */
    class AddListener implements ActionListener {

        /*
         * (non-Javadoc)
         * @see
         * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
         * )
         */
        @Override
        public void actionPerformed(ActionEvent e) {

            final int i = Controller.this.model.addRow();
            Controller.this.view.getTable().clearSelection();
            Controller.this.view.getTable().addRowSelectionInterval(i, i);
        }
    }

    /**
     * The listener interface for receiving delete events. The class that is
     * interested in processing a delete event implements this interface, and
     * the object created with that class is registered with a component using
     * the component's <code>addDeleteListener<code> method. When
     * the delete event occurs, that object's appropriate
     * method is invoked.
     * 
     * @see DeleteEvent
     */
    class DeleteListener implements ActionListener {

        /*
         * (non-Javadoc)
         * @see
         * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
         * )
         */
        @Override
        public void actionPerformed(ActionEvent e) {

            final int row = Controller.this.view.getTable().getSelectedRow();
            if (row != -1) {
                Controller.this.model.deleteRow(row);
            }
        }
    }

    /**
     * The listener interface for receiving save events. The class that is
     * interested in processing a save event implements this interface, and the
     * object created with that class is registered with a component using the
     * component's <code>addSaveListener<code> method. When
     * the save event occurs, that object's appropriate
     * method is invoked.
     * 
     * @see SaveEvent
     */
    class SaveListener implements ActionListener {

        /*
         * (non-Javadoc)
         * @see
         * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
         * )
         */
        @Override
        public void actionPerformed(ActionEvent e) {

            if (Controller.this.view.getTable().isEditing()) {
                Controller.this.view.getTable().getCellEditor().stopCellEditing();
            }
            Controller.this.model.save();
        }
    }

    /** The model. */
    private final T model;

    /** The view. */
    private final View<T> view;

    /**
     * Instantiates a new Controller.
     * 
     * @param model
     *            the model
     * @param view
     *            the view
     */
    public Controller(T model, View<T> view) {

        this.model = model;
        this.view = view;

        this.view.registerSaveListener(new SaveListener());
        this.view.registerAddListener(new AddListener());
        this.view.registerDeleteListener(new DeleteListener());
    }
}
