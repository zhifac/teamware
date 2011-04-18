/*
 *  LogFrame.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 09/Jun/2006
 */

package gate.teamware.richui.annic.gui;

import gate.gui.LogArea;
import gate.teamware.richui.annic.Constants;

import javax.swing.*;
import java.awt.*;

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
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        LogArea logArea = new LogArea();
        this.getContentPane().add(new JScrollPane(logArea), BorderLayout.CENTER);
        this.setSize(600, 600);
        this.setLocationRelativeTo(null); // center on screen
    }
}
