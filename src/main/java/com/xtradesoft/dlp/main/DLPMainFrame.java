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
     * The Constant openFrameCount.
     */
    static final int openFrameCount = 0;

    /**
     * The Constant yOffset.
     */
    static final int xOffset = 30, yOffset = 30;

    /**
     * Instantiates a new DLPChildFrame.
     *
     * @param title the title
     */
    public DLPChildFrame(String title) {

      super(title, true, false, true, true);

      setSize(300, 300);

      setLocation(xOffset * openFrameCount, yOffset * openFrameCount);
    }

  }

  /**
   * The Class SystemTrayAdapter.
   *
   * @param <T> the generic type
   */
  public class SystemTrayAdapter<T extends JFrame> {

    /**
     * The frame.
     */
    private final JFrame _frame;

    /**
     * Instantiates a new SystemTrayAdapter.
     *
     * @param frame the frame
     */
    SystemTrayAdapter(T frame) {

      _frame = frame;
      _adapt();
    }

    /**
     * _adapt.
     */
    void _adapt() {

      if (!SystemTray.isSupported()) {
        logger.error("system tray is not supported");
      }

      final PopupMenu menu = new PopupMenu();

      final MenuItem showItem = new MenuItem("Show");
      menu.add(showItem);

      final MenuItem exitItem = new MenuItem("Exit");
      menu.add(exitItem);

      final URL imageURL = SystemTrayAdapter.class.getResource("/images/app-icon.png");

      if (imageURL == null) {
        logger.error("/images/app-icon.png found, skip systray installation");
        return;
      }

      final Image image = new ImageIcon(imageURL).getImage();
      final TrayIcon icon = new TrayIcon(image, "pdf-download-print", menu);

      icon.setImageAutoSize(true);
      _frame.setIconImage(image);

      icon.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent ae) {

          _frame.setVisible(true);
          _frame.setExtendedState(Frame.NORMAL);
          SystemTray.getSystemTray().remove(icon);
        }

      });

      showItem.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent ae) {

          _frame.setVisible(true);
          _frame.setExtendedState(Frame.NORMAL);
          SystemTray.getSystemTray().remove(icon);
        }
      });

      exitItem.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent ae) {

          if (null == _windowListner) {
            return;
          }

          _windowListner.windowClosing(new WindowEvent(_frame, WindowEvent.WINDOW_CLOSING));
          dispose();
          _windowListner.windowClosed(new WindowEvent(_frame, WindowEvent.WINDOW_CLOSED));
        }
      });

      _frame.addWindowListener(new WindowAdapter() {

        @Override
        public void windowIconified(WindowEvent we) {

          _frame.setVisible(false);
          try {
            SystemTray.getSystemTray().add(icon);
          } catch (final AWTException e) {
            logger.error(e.getMessage(), e);
          }
        }

      });

      _frame.setState(Frame.ICONIFIED);
    }
  }

  /**
   * The Constant logger.
   */
  final static Logger logger = LoggerFactory.getLogger(DLPMainFrame.class);

  static {
    JFrame.setDefaultLookAndFeelDecorated(true);
  }

  /**
   * The desktop.
   */
  JDesktopPane _desktop;

  /**
   * The window listner.
   */
  private WindowAdapter _windowListner;

  /**
   * Instantiates a new DLPMainFrame.
   */
  public DLPMainFrame() {

    super("pdf-download-print");
    initialize();
  }

  /**
   * Instantiates a new DLPMainFrame.
   *
   * @param windowListner the window listner
   */
  public DLPMainFrame(WindowAdapter windowListner) {

    super("pdf-download-print");
    addWindowListener(_windowListner = windowListner);
    initialize();
  }

  /**
   * Adds the child.
   *
   * @param child the child
   * @param title the title
   */
  public void addChild(JComponent child, String title) {

    final DLPChildFrame frame = new DLPChildFrame(title);
    frame.pack();

    frame.setSize(300, 300);
    frame.add(child);

    frame.setVisible(true);
    _desktop.add(frame);

    try {
      frame.setSelected(true);

    } catch (final java.beans.PropertyVetoException e) {
    }

    tile();
  }

  /**
   * Initialize.
   */
  public void initialize() {

    setSize(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize());
    setMaximumSize(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize());
    setResizable(false);

    _desktop = new JDesktopPane();
    setContentPane(_desktop);

    _desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    final JInternalFrame frames[] = _desktop.getAllFrames();
    final Dimension frameSize = new Dimension(_desktop.getSize());
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
        }
      }
      frame.setSize(frameSize);
      frame.setLocation(x, y);
      x += xShift;
      y += yShift;
    }
  }

}
