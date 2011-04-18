package gate.teamware.richui.annotationdiffgui;

import gate.Document;
import gate.teamware.richui.common.RichUIException;

public abstract class Connection {
	public abstract String getConnectionStatus();
	public abstract Document getDocument();
	public abstract void cleanup() throws RichUIException;
	public abstract void saveDocument() throws RichUIException;
}
