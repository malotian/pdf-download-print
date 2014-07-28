/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.main;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class DLPMainFrame.
 */
@SuppressWarnings("serial")
public class DLPMainFrame extends JFrame {

    /**
     * The Class DLPChildFrame.
     */
    public class DLPChildFrame extends JInternalFrame {

        /**
         * Instantiates a new DLPChildFrame.
         * 
         * @param title
         *            the title
         */
        public DLPChildFrame(String title) {

            super(title, true, false, true, true);

            setSize(300, 300);
        }

    }

    /**
     * The Class SystemTrayAdapter.
     * 
     * @param <T>
     *            the generic type
     */
    public class SystemTrayAdapter<T extends JFrame> {

        /** The frame. */
        private final JFrame frame;

        /**
         * Instantiates a new SystemTrayAdapter.
         * 
         * @param frame
         *            the frame
         */
        SystemTrayAdapter(T frame) {

            this.frame = frame;
            adapt();
        }

        /**
         * Adapt.
         */
        void adapt() {

            if (!SystemTray.isSupported()) {
                LOGGER.error("system tray is not supported");
                return;
            }

            final PopupMenu menu = new PopupMenu();

            final MenuItem showItem = new MenuItem("Show");
            menu.add(showItem);

            final MenuItem exitItem = new MenuItem("Exit");
            menu.add(exitItem);

            final URL imageURL = SystemTrayAdapter.class.getResource("/images/app-icon.png");

            final Image image = new ImageIcon(imageURL).getImage();
            final TrayIcon icon = new TrayIcon(image, TITLE, menu);

            icon.setImageAutoSize(true);
            this.frame.setIconImage(image);

            icon.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {

                    SystemTrayAdapter.this.frame.setVisible(true);
                    SystemTrayAdapter.this.frame.setExtendedState(Frame.NORMAL);
                    SystemTray.getSystemTray().remove(icon);
                }

            });

            showItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {

                    SystemTrayAdapter.this.frame.setVisible(true);
                    SystemTrayAdapter.this.frame.setExtendedState(Frame.NORMAL);
                    SystemTray.getSystemTray().remove(icon);
                }
            });

            exitItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {

                    if (null == windowListner) {
                        return;
                    }

                    windowListner.windowClosing(new WindowEvent(SystemTrayAdapter.this.frame,
                            WindowEvent.WINDOW_CLOSING));

                    windowListner
                            .windowClosed(new WindowEvent(SystemTrayAdapter.this.frame, WindowEvent.WINDOW_CLOSED));

                    SystemTray.getSystemTray().remove(icon);
                    SystemTrayAdapter.this.frame.dispose();
                }
            });

            this.frame.addWindowListener(new WindowAdapter() {

                @Override
                public void windowIconified(WindowEvent we) {

                    SystemTrayAdapter.this.frame.setVisible(false);
                    try {
                        SystemTray.getSystemTray().add(icon);
                    } catch (final AWTException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }

            });

            this.frame.setState(Frame.ICONIFIED);
        }
    }

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DLPMainFrame.class);

    /** The Constant TITLE. */
    private static final String TITLE = "pdf-download-print";

    static {
        JFrame.setDefaultLookAndFeelDecorated(true);
    }

    /** The desktop. */
    JDesktopPane desktop;

    /** The window listner. */
    private WindowAdapter windowListner;

    /**
     * Instantiates a new DLPMainFrame.
     */
    public DLPMainFrame() {

        super(TITLE);
        initialize();
    }

    /**
     * Instantiates a new DLPMainFrame.
     * 
     * @param windowListner
     *            the window listner
     */
    public DLPMainFrame(WindowAdapter windowListner) {

        super(TITLE);
        this.windowListner = windowListner;
        addWindowListener(windowListner);
        initialize();
    }

    /**
     * Adds the child.
     * 
     * @param child
     *            the child
     * @param title
     *            the title
     */
    public void addChild(JComponent child, String title) {

        final DLPChildFrame childFrame = new DLPChildFrame(title);
        childFrame.pack();

        childFrame.setSize(300, 300);
        childFrame.add(child);

        childFrame.setVisible(true);
        desktop.add(childFrame);

        try {
            childFrame.setSelected(true);

        } catch (final java.beans.PropertyVetoException e) {
            LOGGER.error(e.getMessage(), e);
        }

        tile();
    }

    /**
     * Dipose childs.
     */
    public void diposeChilds() {

        final JInternalFrame[] frames = desktop.getAllFrames();

        for (final JInternalFrame frame : frames) {
            frame.dispose();
        }

    }

    /**
     * Initialize.
     */
    public void initialize() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize());
        setMaximumSize(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize());
        setResizable(false);

        desktop = new JDesktopPane();
        setContentPane(desktop);

        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

        setVisible(true);
    }

    /**
     * Send to tray.
     */
    public void sendToTray() {

        new SystemTrayAdapter<DLPMainFrame>(this);
    }

    /**
     * Tile.
     */
    public void tile() {

        final JInternalFrame[] frames = desktop.getAllFrames();
        final Dimension frameSize = new Dimension(desktop.getSize());
        final int xShift = 0;
        int yShift = 0;
        if (frames.length > 0) {
            frameSize.height /= frames.length;
            yShift = frameSize.height;
        }

        int x = 0, y = 0;
        for (final JInternalFrame frame : frames) {
            if (frame.isMaximum()) {
                try {
                    frame.setMaximum(false);
                } catch (final PropertyVetoException pve) {
                    LOGGER.error(pve.getMessage(), pve);
                }
            }
            frame.setSize(frameSize);
            frame.setLocation(x, y);
            x += xShift;
            y += yShift;
        }
    }

}
