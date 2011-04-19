/*
 *  LogFrame.java
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

import gate.gui.LogArea;
import gate.teamware.richui.annotatorgui.Constants;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * @author Andrey Shafirin
 */
public class LogFrame extends JFrame implements Constants {

    /** Internal reference to the instance of this action. */
    private static LogFrame ourInstance;

    /**
     * Returns reference to the instance of log farme.
     *
     * @return instance of this action
     * */
    public synchronized static LogFrame getInstance() {
        if (ourInstance == null) {
            ourInstance = new LogFrame();
        }
        return ourInstance;
    }

    private LogFrame() {
        super(APP_TITLE + " Application Log");
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        LogArea logArea = new LogArea();
        this.getContentPane().add(new JScrollPane(logArea), BorderLayout.CENTER);
        this.setSize(600, 600);
        this.setLocationRelativeTo(null); // center on screen
    }
}
