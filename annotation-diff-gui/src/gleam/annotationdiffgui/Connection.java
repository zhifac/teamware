package gleam.annotationdiffgui;

import gate.Document;

public abstract class Connection {
	public abstract String getConnectionStatus();
	public abstract Document getDocument();
	public abstract void cleanup() throws AnnotationDiffGUIException;
	public abstract void saveDocument() throws AnnotationDiffGUIException;
}
