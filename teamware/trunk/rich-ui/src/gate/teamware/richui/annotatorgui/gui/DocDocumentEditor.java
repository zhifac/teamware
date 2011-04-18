package gate.teamware.richui.annotatorgui.gui;

import java.util.Arrays;
import java.util.HashSet;

import gate.creole.ontology.ocat.OntologyViewerOptions;
import gate.creole.ontology.ocat.OntologyViewer;
import gate.gui.docview.AnnotationSetsView;
import gate.gui.docview.DocumentEditor;
import gate.gui.docview.DocumentView;
import gate.gui.docview.TextualDocumentView;
import gate.teamware.richui.annotatorgui.*;
import gate.teamware.richui.common.RichUIException;

public class DocDocumentEditor extends DocumentEditor {

	protected void initViews() {
		super.initViews();
		remove(topBar);
		topSplit.setOneTouchExpandable(true);
		horizontalSplit.setOneTouchExpandable(true);
		setTopView(0);
		setCentralView(0);

		// disable text editing
		((TextualDocumentView) getCentralView()).setEditable(false);

		if (AnnotatorGUI.getConnection() != null) {
			if (AnnotatorGUI.getConnection() instanceof DocserviceConnection) {
				if (((DocserviceConnection) AnnotatorGUI.getConnection())
						.getOntology() != null) {
					// with ontology
					java.util.List views = getVerticalViews();
					for (int i = 0; i < views.size(); i++) {
						if (views.get(i) instanceof OntologyViewer) {
							final OntologyViewer ov = (OntologyViewer) views
									.get(i);
							setRightView(i);
							String asName = ((DocserviceConnection) AnnotatorGUI
									.getConnection()).getAnnotationSetName();
							if (asName == null || !asName.equals("")) {
								OntologyViewerOptions opt = ov
										.getOntologyViewerOptions();
								opt.disableAnnotationSetSelection(asName);
							}

							String classesToHide = (String) AnnotatorGUI.getProperties().get(Constants.CLASSES_TO_HIDE_PARAMETER_NAME);
							if(classesToHide != null) {
								String [] classes = classesToHide.trim().split(";");
								OntologyViewerOptions opt = ov.getOntologyViewerOptions();
								opt.addToClassesToHide(new HashSet<String>(Arrays.asList(classes)));
								if(opt.getClassesToHide().isEmpty()) {
									opt.disableFiltering(true);
								}
							}

							String classesToShow = (String) AnnotatorGUI.getProperties().get(Constants.CLASSES_TO_SHOW_PARAMETER_NAME);
							if(classesToShow != null) {
								String [] classes = classesToShow.trim().split(";");
								OntologyViewerOptions opt = ov.getOntologyViewerOptions();
								opt.addToClassesToShow(new HashSet<String>(Arrays.asList(classes)));
								if(opt.getClassesToShow().isEmpty()) {
									opt.disableFiltering(true);
								}
							}

							break;
						}
					}
				} else {
					// without ontology
					setRightView(0);
					String asName = ((DocserviceConnection) AnnotatorGUI
							.getConnection()).getAnnotationSetName();
					DocumentView dv = getRightView();
					// disable anotation set creation if concrete annotation set
					// specifyed
					if (dv instanceof AnnotationSetsView) {
						if (asName == null || !asName.equals("")) {
							((AnnotationSetsView) dv)
									.setNewAnnSetCreationEnabled(false);
						}
						AnnotatorGUI
								.highlightAnnotations((AnnotationSetsView) dv);
					}
				}
			} else if (AnnotatorGUI.getConnection() instanceof ExecutiveConnection) {
				try {
					throw new RichUIException(
							"Internal error. Connection of type '"
									+ AnnotatorGUI.getConnection().getClass()
											.getName()
									+ "' handled in class TaskDocumentEditor.");
				} catch (RichUIException e) {
					e.printStackTrace();
				}
			} else {
				try {
					throw new RichUIException(
							"Internal error. Unsupported connection type: "
									+ AnnotatorGUI.getConnection().getClass()
											.getName());
				} catch (RichUIException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
