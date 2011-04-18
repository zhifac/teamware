package gleam.annic.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gate.gui.OptionsDialog;

/**
 * This
 */
public class AnnicGUIOptionsDialog extends OptionsDialog {

	protected boolean closedByOK = false;

	public AnnicGUIOptionsDialog(Frame owner) {
		super(owner);
	}

	@Override
	protected void initGuiComponents() {
		super.initGuiComponents();

		// remove all the tabs except Appearance.
		for (int i = mainTabbedPane.getTabCount() - 1; i >= 0; i--) {
			if (!mainTabbedPane.getTitleAt(i).contains("Appearance")) {
				mainTabbedPane.remove(i);
			}
		}
	}

	@Override
	protected void initListeners() {
		super.initListeners();

		// add a listener so we know whether the user closed the dialog with
		// OK rather than cancel.
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closedByOK = true;
			}
		});
	}

	/**
	 * Was this dialog closed by the user pressing OK?
	 */
	public boolean wasClosedByOK() {
		return closedByOK;
	}

}
