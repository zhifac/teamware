/*
 *  TaskDocumentEditor.java
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.TimerTask;

import gate.creole.ontology.ocat.OntologyViewerOptions;
import gate.creole.ontology.ocat.OntologyViewer;
import gate.gui.docview.AnnotationSetsView;
import gate.gui.docview.DocumentEditor;
import gate.gui.docview.DocumentView;
import gate.gui.docview.TextualDocumentView;
import gate.teamware.richui.annotatorgui.*;

public class TaskDocumentEditor extends DocumentEditor implements Constants {

	private static final long serialVersionUID = 6710946051804223498L;
	private AnnotatorTask annotatorTask;

	protected void initViews() {
		super.initViews();
		remove(topBar);
		topSplit.setOneTouchExpandable(true);
		horizontalSplit.setOneTouchExpandable(true);
		setTopView(0);
		setRightView(0);

		// disable text editing
		((TextualDocumentView) getCentralView()).setEditable(false);

		DocumentView dv = getRightView();
		if (dv instanceof AnnotationSetsView) {
			((AnnotationSetsView) dv).setNewAnnSetCreationEnabled(false);
			AnnotatorGUI.highlightAnnotations((AnnotationSetsView) dv);
		}

		if (this.annotatorTask != null
				&& this.annotatorTask.getOntology() != null) {
			List views = getVerticalViews();
			for (int i = 0; i < views.size(); i++) {
				if (views.get(i) instanceof OntologyViewer) {
					// onto options are not initialized at this moment
					// they will be initialized at the first attempt to display
					// this component will try to perform following operations
					// little later
					OntologyViewer ov = (OntologyViewer) views.get(i);
					setRightView(i);

					/*
					 * Enable the following code to be able to store annotations
					 * under the user's annotation set String userId =
					 * this.annotatorTask.getConnection() .getUserId();
					 */

					// we for the moment store all annotations under the default
					// annotation set
					OntologyViewerOptions opt = ov.getOntologyViewerOptions();
					opt
							.disableAnnotationSetSelection(OntologyViewerOptions.DEFAULT_ANNOTATION_SET/* userId */);

					String classesToHide = (String) AnnotatorGUI
							.getProperties().get(
									Constants.CLASSES_TO_HIDE_PARAMETER_NAME);
					if (classesToHide != null) {
						String[] classes = classesToHide.split(";");
						opt.addToClassesToHide(new HashSet<String>(Arrays
								.asList(classes)));

						if (opt.getClassesToHide().isEmpty()) {
							opt.disableFiltering(true);
						}
					}

					String classesToShow = (String) AnnotatorGUI
							.getProperties().get(
									Constants.CLASSES_TO_SHOW_PARAMETER_NAME);
					if (classesToShow != null) {
						String[] classes = classesToShow.split(";");
						opt.addToClassesToShow(new HashSet<String>(Arrays
								.asList(classes)));

						if (opt.getClassesToShow().isEmpty()) {
							opt.disableFiltering(true);
						}
					}

					break;
				}
			}
		}
	}

	public AnnotatorTask getAnnotatorTask() {
		return annotatorTask;
	}

	public void setAnnotatorTask(AnnotatorTask aTask) {
		annotatorTask = aTask;
	}
}
