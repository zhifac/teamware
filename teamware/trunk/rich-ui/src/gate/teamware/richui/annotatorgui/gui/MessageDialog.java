/*
 *  MessageDialog.java
 *
 *  Copyright (c) 2006-2011, The University of Sheffield.
 *
 *  This file is part of GATE Teamware (see http://gate.ac.uk/teamware/), 
 *  and is free software, licenced under the GNU Affero General Public License,
 *  Version 3, November 2007 (also included with this distribution as file 
 *  LICENCE-AGPL3.html).
 *
 *  A commercial licence is also available for organisations whose business
 *  models preclude the adoption of open source and is subject to a licence
 *  fee charged by the University of Sheffield. Please contact the GATE team
 *  (see http://gate.ac.uk/g8/contact) if you require a commercial licence.
 *
 *  $Id$
 */
package gate.teamware.richui.annotatorgui.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class MessageDialog extends JDialog {
  /** Debug flag */
  private static final boolean DEBUG = false;

  private volatile Thread disposerThread;

  public MessageDialog(JFrame owner, String title, String message, long timeout) {
    super(owner, title, true);
    initGUI(message);
    hookupEvents();
    disposeByTimeout(timeout);
  }

  /**
   * Constructs all needed GUI elements of this dialog.
   */
  private void initGUI(String message) {
    this.setSize(400, 100);
    this.getContentPane().setLayout(new GridBagLayout());    
    JLabel l = new JLabel(message);
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    c.weighty = 0;
    c.anchor = GridBagConstraints.CENTER;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = 1;
    this.getContentPane().add(l, c);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setLocation(Math.min(getToolkit().getScreenSize().width - getWidth(),
        getOwner().getX() + getOwner().getWidth() / 2 - getWidth() / 2), Math
        .min(getToolkit().getScreenSize().height - getHeight(), getOwner()
            .getY()
            + getOwner().getHeight() / 2 - getHeight() / 2));
    this.setResizable(false);
  }

  /**
   * Adds event handling for this dialog and its elements.
   */
  private void hookupEvents() {
    // allow close dialog by 'Esc' or 'Enter'
    getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escPressed");
    getRootPane().getActionMap().put("escPressed", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        cancel();
      }
    });
    getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterPressed");
    getRootPane().getActionMap().put("enterPressed", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        cancel();
      }
    });

    addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE
            || e.getKeyCode() == KeyEvent.VK_ENTER) {
          cancel();
        }
      }
    });
  }

  private void disposeByTimeout(final long timeout) {
    // Called by event thread, but can be safely called by any thread.
    Runnable disposer = new Runnable() {
      public void run() {
        long timeStarted = System.currentTimeMillis();
        try {
          while ((System.currentTimeMillis() - timeStarted) < timeout) {
            if (Thread.currentThread().isInterrupted()) {
              dispose();
              if (DEBUG) {
                System.out
                    .println("DEBUG: TimedDialog: disposed before timeout.");
              }
              return;
            }
            try {
              Thread.sleep(1000);
            }
            catch(InterruptedException ie) {
              // re-interrupt ourselves to cause early dispose next time round
              // the loop.
              Thread.currentThread().interrupt();
            }
          }
          dispose();
          if (DEBUG) {
            System.out.println("DEBUG: TimedDialog.Disposer: dialog disposed.");
          }
        } catch (Exception e) {
          e.printStackTrace();
          dispose();
        }
      }
    };
    disposerThread = new Thread(disposer, "disposerThread");
    disposerThread.start();
  }

  /**
   * Cancells connection process or closes this dialog if no connection in
   * progress.
   */
  private void cancel() {
    ensureEventThread();
    if (null != disposerThread) {
      disposerThread.interrupt();
      disposerThread = null;
    } else {
      this.dispose();
    }
  }

  /**
   * Controls that we are in event thread. Throws an exception if called not
   * from event thread.
   */
  private void ensureEventThread() {
    // throws an exception if not invoked by the event thread.
    if (SwingUtilities.isEventDispatchThread()) {
      return;
    }
    throw new RuntimeException(
        "only the event thread should invoke this method");
  }
}
