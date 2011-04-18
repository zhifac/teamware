package gleam.annotatorgui;

import gate.Document;

public abstract class Connection {
	public abstract String getConnectionStatus();
	public abstract Document getDocument();
	public abstract boolean isDocumentAnnotationsModified();
	public abstract void cleanup() throws AnnotatorGUIExeption;
	public abstract void saveDocument() throws AnnotatorGUIExeption;
	public abstract void setAnnotatorTask(AnnotatorTask task) throws AnnotatorGUIExeption;
}
