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

package gleam.annotationdiffgui;

/**
 * This interface defines common constants for Annotator GUI application.
 * 
 * @author Andrey Shafirin
 */
public interface Constants {
	public static final String APP_TITLE = "Annotation Diff GUI";

	public static final String DOCUMENT_CLASS_NAME = "gate.corpora.DocumentImpl";

	// ======== application parameters (all modes) =========
	
	public static final String MODE_PARAMETER_NAME = "mode";
	
  public static final String REPOSITORY_PARAMETER_NAME = "repository-name";
  
  public static final String OWLIMSERVICE_URL_PARAMETER_NAME = "owlimservice-url";
  
	public static final String SITE_CONFIG_URL_PARAMETER_NAME = "sitecfg";

	public static final String AUTOCONNECT_PARAMETER_NAME = "autoconnect";
	
	public static final String LOAD_PLUGINS_PARAMETER_NAME = "load-plugins";

  /** That plugin will be always loaded */
  public static final String BASE_PLUGIN_NAME = "base-plugin";

  public static final String LOAD_ANN_SCHEMAS_NAME = "load-ann-schemas";

  public static final String ONTOLOGY_URL_PARAMETER_NAME = "ontology-url";

	public static final String ONTOLOGY_TYPE_PARAMETER_NAME = "ontology-type";

  public static final String ONTOLOGY_TYPE_RDFXML = "rdfxml";

	public static final String ONTOLOGY_TYPE_TURTLE = "turtle";
	
  public static final String ONTOLOGY_TYPE_NTRIPPLES = "ntripples";

	public static final String DEBUG_PARAMETER_NAME = "debug";

  public static final String SELECT_AS_PARAMETER_NAME = "select-as";

  /** Comma separated list of annotation types to select */
  public static final String SELECT_ANN_TYPES_PARAMETER_NAME = "select-ann-types";

  // ======== application parameters (direct mode) =========
	
	public static final String DOCSERVICE_URL_PARAMETER_NAME = "docservice-url";

  
	public static final String DOC_ID_PARAMETER_NAME = "doc-id";

	public static final String ANNSET_NAME_PARAMETER_NAME = "annotationset-name";

  /** A value for default annotation set name. */
  public static String DEFAULT_AS_NAME_PARAMETER_VALUE = "DEFAULT_ANNOTATION_SET";

  // ======== application parameters (pool mode) =========
	
  public static final String EXECUTIVE_SERVICE_URL_PARAMETER_NAME = "executiveservice-url";

  public static final String USER_ID_PARAMETER_NAME = "user";

	public static final String USER_PASSWORD_PARAMETER_NAME = "password";

  public static final String CAN_CANCEL_PARAMETER_NAME = "can-cancel";

  public static final String EXECUTIVE_PROXY_FACTORY_PARAMETER_NAME = "executive-proxy-factory";

  // ======== values for application parameters =========

	public static final String DEBUG_TRUE = "true";

	public static final String DEBUG_FALSE = "false";
	
	public static final String POOL_MODE = "pool";
	
	public static final String DIRECT_MODE = "direct";

	public static final String AUTOCONNECT_TRUE = "true";
	
	public static final String AUTOCONNECT_FALSE = "false";

	public static final String ONTOLOGY_DEFAULT_URL = "http://proton.semanticweb.org/2005/04/protonu";
	
	// ugly hardcodings, fix this
	public static final String DOCSERVICE_DEFAULT_URL = "http://localhost:8080/docservice/services/docservice";
	
	public static final String EXECUTIVE_SERVICE_DEFAULT_URL = "http://localhost:8080/executive/services/ExecutiveCallbackService";
  
  public static final String OWLIMSERVICE_DEFAULT_URL = "http://localhost:8080/owlim-service/services/OWLIMService";

  public static final String CONTEXT = "context";
  
  public static final String DEFAULT_CONTEXT = "/annotation-diff-gui";
  
  public static final String ANNOTATOR_GUI_DIALOG_TITLE = APP_TITLE + " Status:";

}
