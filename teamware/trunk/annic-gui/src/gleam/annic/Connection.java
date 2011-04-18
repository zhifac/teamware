package gleam.annic;

/**
 * Connection interface
 * @author niraj
 *
 */
public abstract class Connection {
	public abstract String getConnectionStatus();
	public abstract void cleanup() throws AnnicGUIExeption;
}
