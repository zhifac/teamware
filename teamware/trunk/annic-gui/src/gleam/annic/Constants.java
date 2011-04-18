/*
 *  Constants.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 09/Jun/2006
 */

package gleam.annic;

/**
 * This interface defines common constants for Annotator GUI application.
 * 
 * @author Andrey Shafirin
 */
public interface Constants {
	public static final String APP_TITLE = "ANNIC GUI";

	// ======== application parameters (all modes) =========

	public static final String SITE_CONFIG_URL_PARAMETER_NAME = "sitecfg";

	public static final String AUTOCONNECT_PARAMETER_NAME = "autoconnect";

	public static final String LOAD_PLUGINS_PARAMETER_NAME = "load-plugins";

	/** That plugin will be always loaded */
	public static final String BASE_PLUGIN_NAME = "base-plugin";

	public static final String DEBUG_PARAMETER_NAME = "debug";

	// ======== application parameters (direct mode) =========

	public static final String DOCSERVICE_URL_PARAMETER_NAME = "docservice-url";

	public static final String CORPUS_ID_PARAMETER_NAME = "corpus-id";

	// ======== values for application parameters =========

	public static final String DEBUG_TRUE = "true";

	public static final String DEBUG_FALSE = "false";

	public static final String DIRECT_MODE = "direct";

	public static final String AUTOCONNECT_TRUE = "true";

	public static final String AUTOCONNECT_FALSE = "false";

	// ugly hardcodings, fix this
	public static final String DOCSERVICE_DEFAULT_URL = "http://localhost:8080/docservice/services/docservice";

	public static final String CONTEXT = "context";

	public static final String DEFAULT_CONTEXT = "/annicgui";

	public static final String ANNIC_GUI_DIALOG_TITLE = APP_TITLE
			+ " Status:";

}
