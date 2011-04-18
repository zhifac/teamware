package gate.teamware.richui.annic;

import gate.teamware.richui.common.RichUIException;

/**
 * Connection interface
 * @author niraj
 *
 */
public abstract class Connection {
	public abstract String getConnectionStatus();
	public abstract void cleanup() throws RichUIException;
}
