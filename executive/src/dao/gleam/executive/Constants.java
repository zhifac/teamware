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
package gleam.executive;

/**
 * Constant values used throughout the application.
 * 
 * <p>
 * <a href="Constants.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class Constants {
  // ~ Static fields/initializers
  // =============================================
  /** The name of the ResourceBundle used in this application */
  public static final String BUNDLE_KEY = "ApplicationResources";

  /** The encryption algorithm key to be used for passwords */
  public static final String ENC_ALGORITHM = "algorithm";

  /** A flag to indicate if passwords should be encrypted */
  public static final String ENCRYPT_PASSWORD = "encryptPassword";

  /** File separator from System properties */
  public static final String FILE_SEP = System.getProperty("file.separator");

  /** User home from System properties */
  public static final String USER_HOME = System.getProperty("user.home")
          + FILE_SEP;

  /** The name of the configuration hashmap stored in application scope. */
  public static final String CONFIG = "appConfig";

  /**
   * Session scope attribute that holds the locale set by the user. By
   * setting this key to the same one that Struts uses, we get
   * synchronization in Struts w/o having to do extra work or have two
   * session-level variables.
   */
  public static final String PREFERRED_LOCALE_KEY = "org.apache.struts.action.LOCALE";

  /**
   * The request scope attribute under which an editable user form is
   * stored
   */
  public static final String USER_KEY = "userForm";

  /**
   * The request scope attribute that holds the user list
   */
  public static final String USER_LIST = "userList";

  /**
   * The request scope attribute for indicating a newly-registered user
   */
  public static final String REGISTERED = "registered";

  /**
   * The name of the super-admin user.
   */
  public static final String SUPERADMIN_USER = "superadmin";

  /**
   * The name of the Administrator role
   */
  public static final String ADMIN_ROLE = "admin";
  
  /**
   * The name of the SuperAdministrator role
   */
  public static final String SUPERADMIN_ROLE = "superadmin";

  /**
   * The name of the User role, as specified in web.xml
   */
  public static final String USER_ROLE = "user";

  
  public static final String MANAGER_ROLE = "manager";
  
  
  //public static final String CURATOR_ROLE = "curator";
  
  
  public static final String ANNOTATOR_ROLE = "annotator";
  
  /**
   * The name of the user's role list, a request-scoped attribute when
   * adding/editing a user.
   */
  public static final String USER_ROLES = "userRoles";
  
  
  

  /**
   * The name of the available roles list, a request-scoped attribute
   * when adding/editing a user.
   */
  public static final String AVAILABLE_ROLES = "availableRoles";
  
  /**
   * The name of the all roles list, a request-scoped attribute
   * when assigning a resource
   */
  public static final String ALL_ROLES = "allRoles";

  /**
   * The name of the available services list, a request-scoped attribute
   * when adding/editing a service.
   */
  public static final String AVAILABLE_SERVICES = "availableServices";
  
  /**
   * The request scope attribute under which an editable role form is
   * stored
   */
  public static final String ROLE_KEY = "roleForm";
  /**
   * The request scope attribute that holds the role list
   */
  public static final String ROLE_LIST = "roleList";
  
  
  public static final String PROJECT_LIST = "projectList";

  /**
   * The request scope attribute under which an editable resource form is
   * stored
   */
  public static final String RESOURCE_KEY = "resourceForm";
  /**
   * The request scope attribute that holds the resource list
   */
  public static final String RESOURCE_LIST = "resourceList";
  
  /**
   * The request scope attribute that holds the service list
   */
  public static final String SERVICE_LIST = "serviceList";
  
  /**
   * The name of the CSS Theme setting.
   */
  public static final String CSS_THEME = "csstheme";

  // Corpus-START
  /**
   * The request scope attribute that holds the corpus form.
   */
  public static final String CORPUS_KEY = "corpusForm";

  /**
   * The request scope attribute that holds the corpus list
   */
  public static final String CORPUS_LIST = "corpusList";
  
  public static final String UPLOADER = "uploader";
  
  public static final String ANNOTATOR_LIST = "annotatorList";
  
  
  public static final String MANAGER_LIST = "managerList";
  
  //public static final String CURATOR_LIST = "curatorList";
  
  public static final String docServiceURL ="docServiceURL";

  // Corpus-END
  // Document-START
  /**
   * The request scope attribute that holds the document form.
   */
  public static final String DOCUMENT_KEY = "documentForm";

  /**
   * The request scope attribute that holds the document list
   */
  public static final String DOCUMENT_LIST = "documentList";

  // Document-END
  // Annic Search-START
  /**
   * The request scope attribute that holds the Annic search result list
   */
  public static final String ANNIC_LIST = "annicList";

  // Annic Search-END
  // AnnotationDiffer-START
  /**
   * The request scope attribute that holds the Annotation Differ result
   * list
   */
  public static final String ANNOTATION_DIFFER_LIST = "annoDifferList";

  /**
   * The request scope attribute that holds the Annotation Differ Scores
   */
  public static final String ANNOTATION_DIFFER_SCORES = "annoDifferScores";

  /**
   * The request scope attribute that holds the annotation set names
   */
  public static final String ANNOTATION_SET_NAMES = "annoSetNames";

  public static final String ANNOTATION_STATUS_LIST = "annotationStatusList";
  
  public static final String ANNOTATION_METRIC_MATRIX= "annotationMetricMatrix";

  /**
   * The request scope attribute that holds the annotation types
   */
  public static final String ANNOTATION_Types = "annoTypes";

  // AnnotationDiffer-END
  public static final String PROCESS_DEFINITION_LIST = "processDefinitionList";

  public static final String PROCESS_INSTANCES_LIST = "processInstanceList";

  public static final String TASK_INSTANCES_LIST = "taskInstanceList";
  
  public static final String MY_TASK_INSTANCES_METHOD = "listByActor";
  
  public static final String ALL_TASK_INSTANCES_METHOD = "list";

  public static final String TASK_LIST = "taskList";

  public static final String TASK_FORM_PARAMETERS = "taskFormParameters";

  public static final String TASK_TRANSITIONS = "taskTransitions";
  
  public static final String ANNOTATOR_GUI_URL_PATTERN = "/annotator-gui/*";
  
  public static final String WORKFLOW_ACCESS_PATTERN = "/workflowMenu.html*";
  
  public static final String PROJECTS_PATTERN = "/projects.html?method=listAll*";
  
  //public static final String USER_PROJECTS_ACCESS_PATTERN = "/projects.html*";
  
  public static final String USER_TASKS_PATTERN = "/taskInstanceList.html?method=listByActor*";
  
  public static final String TASKS_PATTERN = "/taskInstanceList.html?method=list*";
  
  public static final String PROCESS_DEFINITION_PATTERN = "/processDefinitionList.html?method=list*";
  
  public static final String PROCESS_INSTANCE_PATTERN = "/processInstanceList.html?method=listAll*";
  
  //public static final String USER_PROCESS_INSTANCE_PATTERN = "/processInstanceList.html?method=listByUser*";
  
  public static final String ONTOLOGY_REPOSITORY_LIST ="ontologyRepositoryList";
  
  public static final String ONTOLOGY_REPOSITORY_DATA ="ontologyRepositoryData";
  
  public static final String AVAILABLE_AS_NAMES = "availableASNames";
  
  public static final String SELECTED_AS_NAMES = "selectedASNames";
  
  public static final String IAAResult_LIST = "iaaResultList";
  public static final String IAAResult_AllWays_List="iaaAllwaysResultList";
  public static final String IAAResult_AllWays_Response_List="iaaAllwaysResponseList";
  public static final String IAAResult_Pairwise_F_Measure="iaaPairwiseFMeasure";
  public static final String IAAResult_AllWays_F_Measure="iaaAllwaysFMeasure";
  public static final String IAA_SCORE = "iaaAgreement";

  public static final String IAAResult_Pairwise_Kappa_Overall="pairwiseKappaOverallList";
  public static final String IAAResult_Allways_Kappa_Overall="allwaysKappaOverallList";
  
  public static final String IAA_AS_NAMES = "iaaASNames";
  
  public static final String IAA_Algorithms_List = "iaaAlgorithms";

  public static final String IAAResult_LABEL_VALUES = "iaaLabelValues";
  
  /**
   * The request scope attribute that holds the AnnoSchema form.
   */
  public static final String SCHEMA_KEY = "schemaForm";
  
  /**
   * The request scope attribute that holds the annotation schema list
   */
  public static final String SCHEMA_LIST = "schemaList";
  
  public static final String ANNOTATION_SERVICE_LIST= "annotationServiceList";
  
  public static final String ANNOTATOR_GUI_LAUNCH_BEAN= "annotatorGUILaunchBean";
  
  public static final String ANNOTATION_DIFF_GUI_LAUNCH_BEAN= "annotationDiffGUILaunchBean";

  // WF forwards:
  
  public static final String FORWARD_PROCESSES_IN_PROJECT= "listTopProcessesWithTheSameKeyAndName";

  //public static final String FORWARD_MY_PROCESSES= "myProcessInstanceList";
  
  public static final String FORWARD_ALL_PROCESSES= "allProcessInstanceList";
  
  //public static final String FORWARD_MY_PROJECTS= "viewProjects";
  
  public static final String FORWARD_ALL_PROJECTS= "viewAllProjects";
  
  public static final String FORWARD_SUB_PROCESSES= "subProcessInstanceList";
  
  public static final String FORWARD_PROCESSES_FOR_PROCESSDEFINITION= "processInstanceListForProcessDefinition";

  public static final String PARAMETER_FROM = "from";

  public static final String MAIN_PROCESS_DEFINITION_NAME = "main";
  
  public static final String SETUP_PROCESS_DEFINITION_NAME = "setup";
  
  public static final String DEFAULT_PASSWORD = "tomcat";
}
