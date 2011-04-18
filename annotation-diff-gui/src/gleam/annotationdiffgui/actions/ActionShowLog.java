/*
 *  ActionShowLog.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 29/Jun/2006
 */

package gleam.annotationdiffgui.actions;

import gleam.annotationdiffgui.AnnotationDiffGUI;
import gleam.annotationdiffgui.gui.LogFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Shows Log window to user. <br>
 *
 * The purpose of this class is: <br>
 *  - formalize operation of the client application <br>
 *  - provide easy access to this operation (it's Singleton) <br>
 *  - symplify control for enabling/disabling of GUI elements based on this action <br>
 *
 * @author Andrey Shafirin
 */
public class ActionShowLog extends AbstractAction {

    /** Internal reference to the instance of this action. */
    private static ActionShowLog ourInstance;

    /**
     * Returns reference to the instance of this action.
     *
     * @return instance of this action
     * */
    public synchronized static ActionShowLog getInstance() {
        if (ourInstance == null) {
            ourInstance = new ActionShowLog();
        }
        return ourInstance;
    }

    /**
     * Creates this action and defines action icon.
     * */
    private ActionShowLog() {
        super("Show Application Log", AnnotationDiffGUI.createIcon("log-32.gif"));
    }

    /**
     * Displays brief help information for the user.
     *
     * @param e not used
     * */
    public void actionPerformed(ActionEvent e) {
        LogFrame.getInstance().setVisible(true);
    }
}
