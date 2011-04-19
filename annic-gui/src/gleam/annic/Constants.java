/*
 *  Constants.java
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
