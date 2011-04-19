/*
 *  HelpWindow.java
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

package gleam.annic.gui;

import gleam.annic.AnnicGUI;
import gleam.annic.Constants;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * User help window to briefly describe Annic GUI functions. <br>
 *
 * It seems that one help window should be enought so
 * this class is singleton.<br>
 *
 * @author Andrey Shafirin
 */
public class HelpWindow extends JDialog implements Constants {

    /** Internal reference to the instance of this action. */
    private static HelpWindow ourInstance;

    /**
     * Returns reference to the instance of help farme.
     *
     * @return instance of this action
     * */
    public synchronized static HelpWindow getInstance() {
        if (ourInstance == null) {
            ourInstance = new HelpWindow();
        }
        return ourInstance;
    }

    /**
     * Constructs user help window. <br>
     * Window will contain html document <code>AnnicUserHelp.html</code>
     * located in resources <code>gleam/annicgui/resource</code>.
     * */
    private HelpWindow() {
        super(MainFrame.getInstance(), APP_TITLE + " Client Help", false);
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        JTextPane jtp = new JTextPane();
        java.net.URL helpURL = AnnicGUI.getResourceURL("AnnicUserHelp.html");
        if (helpURL != null) {
            try {
                jtp.setPage(helpURL);
            } catch (IOException e) {
                jtp.setText("Help not available. An error occured.\n\n" +
                        "Attempted to read a bad URL: " + helpURL);
            }
        } else {
            jtp.setText("Help not available. An error occured.\n\n" +
                    "Couldn't find help file: AnnicUserHelp.html in application resources\n" +
                    "Resource path: " + AnnicGUI.getResourcePath());
        }
        this.getContentPane().add(new JScrollPane(jtp), BorderLayout.CENTER);
        this.setSize(600, 600);
        this.setLocationRelativeTo(null); // center on screen
    }
}
