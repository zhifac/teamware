package gleam.executive.workflow.util;

import java.util.regex.Pattern;

/*
 *  JPDLConstants.java
 *
 *  Copyright (c) 1998-2006, The University of Sheffield.
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  @author <a href="mailto:M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 */
public class JPDLConstants {

	/**
	 * This class defines constants which are used for Dynamic Forms Constants
	 * are characters in Dynamic Form variable mapped name that indicates type
	 * of input field on HTML form It is followed by underscore and in the most
	 * cases Label name, example: A_Address except for select boxes, example:
	 * C_:country_Country where country is a method in CustomSelectTag and
	 * Country is a label name
	 */

	// /////////////////////////////////////////////////////
	// JPDL CONSTANTS /////////////////////////////////////
	// /////////////////////////////////////////////////////
	/*
	 * Text area prefix Example <variable name="address"
	 * access="read,write,required" mapped-name="A_Address"></variable>
	 */
	public static final String JPDL_TEXTAREA_PREFIX = "A";

	/*
	 * Link prefix Example <variable name="annotatorGUIURL"
	 * access="read,write,required" mapped-name="B_AnnotatorGUIURL"></variable>
	 */
	public static final String JPDL_LINK_PREFIX = "B";

	/*
	 * Select box prefix Example <variable name="country"
	 * access="read,write,required" mapped-name="C_:country_Country"></variable>
	 */
	public static final String JPDL_SELECTBOX_PREFIX = "C";
	
	/*
	 * Checkbox prefix Example <variable name="enable"
	 * access="read,write,required" mapped-name="X_:enable"></variable>
	 */
	public static final String JPDL_CHECKBOX_PREFIX = "X";

	/*
	 * Error message prefix Example <variable name="error"
	 * access="read,write,required" mapped-name="E_Error"></variable>
	 */
	public static final String JPDL_ERROR_MESSAGE_PREFIX = "E";

	/*
	 * Input file prefix Example <variable name="file"
	 * access="read,write,required" mapped-name="F_File"></variable>
	 */
	public static final String JPDL_FILE_PREFIX = "F";

	// public static final String JPDL_DATE_PREFIX = "G";

	/*
	 * Input hidden prefix Example <variable name="userId"
	 * access="read,write,required" mapped-name="H_UserId"></variable>
	 */
	public static final String JPDL_HIDDEN_PREFIX = "H";

	/*
	 * Input Help prefix Example <variable name="help" access="read"
	 * mapped-name="I_help"></variable>
	 */
	public static final String JPDL_HELP_PREFIX = "I";

	// public static final String JPDL_DEADLINE_CHOOSER_PREFIX = "J";

	/*
	 * Label prefix Example <variable name="info" access="read,write,required"
	 * mapped-name="L_Info"></variable>
	 */
	public static final String JPDL_LABEL_PREFIX = "L";

	/*
	 * Multiselect box prefix Example <variable name="users"
	 * access="read,write,required" mapped-name="M_Users"></variable>
	 */
	public static final String JPDL_MULTIBOX_PREFIX = "M";

	/*
	 * Popup prefix Example <variable name="somePopup"
	 * access="read,write,required" mapped-name="P_Some popup"></variable>
	 */
	public static final String JPDL_POPUP_PREFIX = "P";

	/*
	 * Radio button prefix Example <variable name="delivery"
	 * access="read,write,required" mapped-name="R_Delivery"></variable>
	 */
	public static final String JPDL_RADIO_PREFIX = "R";

	// public static final String JPDL_SIGNATURE_PREFIX = "S";

	/*
	 * Text Field prefix Example <variable name="name"
	 * access="read,write,required" mapped-name="T_Name"></variable>
	 */
	public static final String JPDL_TEXT_PREFIX = "T";

	/*
	 * URL prefix Example <variable name="someUrl" access="read,write,required"
	 * mapped-name="U_Some URL"></variable>
	 */
	public static final String JPDL_URL_PREFIX = "U";

	public static final String JPDL_PASSWORD_PREFIX = "Q";
	
	
	public static final String JPDL_SECTION_PREFIX = "N";

	// /////////////////////////////////////////////////////
	// CALLBACK CONSTANTS /////////////////////////////////
	// /////////////////////////////////////////////////////

	/*
	 * prefix in task description tag denoting that it will be created tasks for
	 * only one user in pool
	 */
	public static final String JPDL_COMPLETION_STRATEGY_PREFIX_ANY = "_any";

	/*
	 * prefix in task description tag denoting that it will be created tasks for
	 * all users in pool
	 */
	public static final String JPDL_COMPLETION_STRATEGY_PREFIX_ALL = "_all";

	/*
	 * delimiter among values passed inside callback parameter e.g:
	 * processId;tokenId
	 */
	public static final String CALLBACK_PARAMETER_DELIMITER = ";";

	/*
	 * delimiter inside single token in csv list
	 */
	public static final String INTER_TOKEN_SEPARATOR = "###";

	/*
	 * the name of process variable fired by callback handler
	 */
	public static final String CALLBACK_ERROR_NAME = "callbackError";

	/*
	 * the value of process variable CALLBACK_ERROR_NAME fired by callback
	 * handler in case that everything is OK. If some error occurs, the value of
	 * CALLBACK_ERROR_NAME will be specified by GaS
	 */
	public static final String CALLBACK_ERROR_EMPTY = "";

	/*
	 * the name of the transition which will be fired if the value of
	 * CALLBACK_ERROR_NAME is empty string
	 */
	public static final String TRANSITION_SUCCESS = "Success";

	/*
	 * the name of the transition which will be fired if the value of
	 * CALLBACK_ERROR_NAME is not empty string (Error occured)
	 */
	public static final String TRANSITION_FAILURE = "Failure";

	/*
	 * the name of the transition which will be fired if the task is cancelled
	 */
	public static final String TRANSITION_CANCEL = "Cancel";

	/*
	 * The name of the transition to fire if a document needs manual review
	 */
	public static final String TRANSITION_NEEDS_REVIEW = "Needs Review";

	/*
	 * The name of the transition to fire if a document does not need manual
	 * review (i.e. inter-annotator agreement was high enough).
	 */
	public static final String TRANSITION_NO_REVIEW = "No Review";

	/*
	 * The name of the transition to fire if annottaor should go back to the
	 * pool
	 */
	public static final String TRANSITION_BACK_TO_POOL = "Back to pool";

	/*
	 * The name of the generic YES transition
	 */
	public static final String TRANSITION_YES = "Yes";

	/*
	 * The name of the generic NO transition
	 */
	public static final String TRANSITION_NO = "No";



	/*
	 * The name of the training transition
	 */
	public static final String TRANSITION_TRAINING = "Training";

	/*
	 * the name of the status if the document is available The workflow
	 * definition should have the transition with this name
	 */
	public static final String DOCUMENT_AVAILABLE = "Available";

	public static final String DOCUMENT_AVAILABLE_FOR_SUPERANNOTATION = "Super Available";

	/*
	 * the name of the status if the document is not available. The workflow
	 * definition should have the transition with this name
	 */
	public static final String DOCUMENT_NOT_AVAILABLE = "Not Available";

	/*
	 * the name of the status if the document is annotated. The workflow
	 * definition should have the transition with this name
	 */
	public static final String DOCUMENT_ANNOTATED = "Annotated";

	/*
	 * the name of the status if the document is super annotated. The workflow
	 * definition should have the transition with this name
	 */
	public static final String DOCUMENT_SUPER_ANNOTATED = "Super Annotated";

	public static final String NEW_TOKEN_PREFIX = "foreach_";

	public static final String ANNOTATION_MODE = "annotationMode";

	public static final String POOL_MODE = "pool";

	public static final String RANDOM_MODE = "random";

	public static final String DIRECT_MODE = "direct";

	// /////////////////////////////////////////////////////
	// COMMON VARIABLE NAMES //////////////////////////////
	// /////////////////////////////////////////////////////



	public static final String POSSIBLE_PERFORMERS = "possiblePerformers";

	public static final String TASK_NAME = "taskName";

	public static final String TASK_INSTANCE_ID = "taskInstanceId";

	public static final String TOKEN_ID = "tokenId";

	public static final String ROLE_NAME = "executiveRoleName";

	public static final String NUMBER_OF_ANNOTATORS = "numberOfAnnotators";

	public static final String NUMBER_OF_DOCUMENTS = "numberOfDocuments";


	public static final String TOKEN_CSV_LIST = "tokenCSVList";

	public static final String ALIAS = "alias";

	public static final String DUE_DATE = "dueDate";


	public static final String ANNOTATED_BY_PREFIX = "A_";

	public static final String CANCELED_BY_PREFIX = "C_";

	public static final String FINISHED_BY_PREFIX = "F_";

	public static final String SUPERANNOTATED_BY_PREFIX = "S_";

	public static final String ANNOTATED_BY = "annotatedBy";

	public static final String CANCELED_BY = "canceledBy";

	public static final String SUPERANNOTATED_BY = "superAnnotatedBy";


	public static final String REVIEW_INFO = "reviewInfo";

	public static final String ACCEPT_INFO = "acceptInfo";

	public static final String TASK_PERFORMER = "taskPerformer";

	public static final String TRAINING_TASK_QUEUE = "trainingTaskQueue";

	public static final String NUMBER_OF_TRAINING_TASKS = "numberOfTrainingTasks";

	// doc-centric

	// /////////////////////////////////////////////////////
	// COMMON VARIABLE NAMES //////////////////////////////
	// /////////////////////////////////////////////////////

	// collected from jpdl handler definitions:

	/*
	 * annotationSetName
	 * annotatorsPerDocument
	 * anonymousAnnotation
	 * asTrainingKey
	 * corpusId
	 * docserviceURL
     * documentCSVList
     * annotatorCSVList
     * curatorCSVList
     * documentId
     * gosURL
     * mode
     * ontologyRepositoryName
     * parameterTrainingKey
     * parameterTrainingValue
     * performer
     * templateAnnotatorGUIURL
     * trainingExtraMappings
	 */



	public static final String CORPUS_ID = "corpusId";

	public static final String CORPUS_NAME = "corpusName";

	public static final String DOCUMENT_ID = "documentId";

	public static final String DOCUMENT_ANNOTATION_STATUS = "documentAnnotationStatus";

	public static final String CALLBACK = "callback";

	// prefixes for logging string
	public static final String DOCUMENT_PREFIX = "document_";

	public static final String PROCESS_DEFINITION_PREFIX = "process_";

	public static final String PROCESS_INSTANCE_PREFIX = "instance_";
	
	

	public static final String NODE_PREFIX = "node_";

	public static final String TASK_PREFIX = "task_";

	public static final String GAS_SUFFIX = "gate-service";

	public static final String HUMAN_ANNOTATION_SUFFIX = "human-annotation";
	
	public static final String REVIEW_SUFFIX = "review";

	public static final String CALLBACK_TASK_ID_FORMAT = PROCESS_DEFINITION_PREFIX
			+ "{0}." + PROCESS_INSTANCE_PREFIX + "{1}." + DOCUMENT_PREFIX + "{2}." + NODE_PREFIX + "{3}."
			+ TASK_PREFIX + "{4}.{5}";

	public static final String ANNOTATOR_RECORD_LINK_FORMAT = "/annotationStatus.html?method=showAnnotator&id={0}&username={1}";
	
	public static final String ANNOTATION_STATUS_FORMAT = PROCESS_DEFINITION_PREFIX
	+ "{0}." + PROCESS_INSTANCE_PREFIX + "{1}." + DOCUMENT_PREFIX + "{2}";

	public static final String ANNOTATION_STATUS_PREFIX_FORMAT = PROCESS_DEFINITION_PREFIX
	+ "{0}." + PROCESS_INSTANCE_PREFIX + "{1}";

	public static final String CORPUS_LINK_FORMAT = "/documentsInCorpus.html?corpusID={0}&corpusName={1}";
	
	public static final String MAIN_PROCESS_LINK_FORMAT = "/processInstanceList.html?method=listSubProcessesForProcessInstance&id={0}";
	
	public static final String LOAD_PROJECT_LINK_FORMAT ="/loadProject.html?method=load&corpusId={0}";
    
	public static final String START_PROCESS_FROM_PROJECT_LINK_FORMAT ="/editProcessInstance.html?method=edit&projectId={0}";
	
	public static final String RESUME_PROJECT_LINK_FORMAT ="/editProject.html?method=resume&id={0}";
	   
	public static final String INITIATOR = "initiator";

    public static final String AS_KEY = "asKey";

	public static final String AS_VALUE = "asValue";

	public static final String AS_EXTRA_MAPPINGS = "asExtraMappings";

	public static final String IMPLICIT_AS_KEY = "implicitAsKey";

	public static final String IMPLICIT_AS_VALUE = "implicitAsValue";

	public static final String PARAMETER_KEY = "parameterKey";

	public static final String PARAMETER_VALUE = "parameterValue";

	public static final String ANNOTATOR_HAS_TO_BE_UNIQUE_FOR_DOCUMENT = "annotatorHasToBeUniqueForDocument";

	public static final String MODE = "mode";
	
	public static final String ENDPOINT_URL = "endpointURL";

	public static final String TEST_MODE = "test";

	public static final String DOCSERVICE_URL = "docserviceURL";

	public static final String GOS_URL = "GosURL";

	public static final String DOCUMENT_NAME = "documentName";

	public static final String ONTOLOGY_REPOSITORY_NAME = "ontologyRepositoryName";

	public static final String OWLIM_SERVICE_URL = "owlimServiceURL";

	public static final String CAN_CANCEL = "canCancel";

	public static final String PLUGIN_CSV_LIST = "pluginCSVList";

	public static final String IAA_CONFIG = "iaaConfig";

	public static final String CONDITION_SCRIPT = "conditionScript";

	public static final String ANONYMOUS_ANNOTATION_SET_NAME_PREFIX = "annotator";

	public static final String ANNOTATORS_PER_DOCUMENT = "annotatorsPerDocument";

	public static final String ANONYMOUS_ANNOTATION = "anonymousAnnotation";

	public static final String DOCUMENT_CSV_LIST = "documentCSVList";

	public static final String ANNOTATOR_CSV_LIST = "annotatorCSVList";

	public static final String CURATOR_CSV_LIST = "curatorCSVList";
	
	public static final String MANAGER_CSV_LIST = "managerCSVList";

	public static final String BASE_ANNOTATION_SCHEMA_URL = "baseAnnotationSchemaURL";

	public static final String ANNOTATION_SCHEMA_CSV_URLS = "annotationSchemaCSVURLs";

	public static final String ANNOTATOR_GUI_URL = "annotatorGUIURL";

	public static final String ANNOTATION_SET_NAME = "annotationSetName";

	public static final String PERFORMER = "performer";

	// mail properties
	public static final String TO = "to";
	
	public static final String CC = "cc";

	public static final String SUBJECT = "subject";

	public static final String TEMPLATE = "template";

	public static final String CONTENT = "content";

	public static final String HANDLE_ERROR_NODE = "handle error";

	/*
	 * The name of the generic IGNORE transition
	 */
	public static final String TRANSITION_IGNORE = "Ignore";

	/*
	 * The name of the generic RETRY transition
	 */
	public static final String TRANSITION_RETRY = "Retry";

	/*
	 * The name of the generic Finish transition
	 */
	public static final String TRANSITION_FINISH = "Finish";

	/*
	 * The name of the generic REPEAT transition
	 */
	public static final String TRANSITION_REPEAT = "Repeat";

	/*
	 * The name of the generic ERROR transition
	 */
	public static final String TRANSITION_ERROR = "Error";


	public static final String START_DATE = "startDate";

	public static final String END_DATE = "endDate";
	
	public static final String LAST_OPENED_DATE = "lastOpened";
	
	public static final String LAST_SAVED_DATE = "lastSaved";
	
	public static final String TIME_WORKED_ON = "timeWorkedOn";

	public static final String TAKEN_BY_LIST = "takenByList";

	public static final String ANNOTATED_BY_LIST = "annotatedByList";

	public static final String REJECTED_BY_LIST = "rejectedByList";

	public static final String STATUS = "status";

	public static final String DO_SETUP = "doSetup";

	public static final String DO_AUTOMATIC = "doAutomatic";

	public static final String DO_MANUAL = "doManual";

	public static final String DO_POST_MANUAL = "doPostManual";

	public static final String DO_REVIEW = "doReview";
	
	public static final String DO_POST_PROCESSING = "doPostProcessing";

	public static final String CSV_LIST = "CSVList";

	public static final String COUNTER = "counter";

	public static final String MESSAGE = "message";

	public static final String ITEM = "item";

	public static final String SERVICE_ID = "serviceId";
	
	public static final String MANUAL_SERVICE_ID = "ManualServiceId";
	
	public static final String PRE_MANUAL_SERVICE_ID = "preManualServiceId";
	
	public static final String POST_MANUAL_SERVICE_ID = "postManualServiceId";
	
	public static final String POST_PROCESSING_SERVICE_ID = "postProcessingServiceId";

	public static final String PIPELINE_CSV_LIST = "PipelineCSVList";
	
	public static final String GAS_PIPELINE_CSV_LIST = "gasPipelineCSVList";

	public static final String PREPROCESSING_PIPELINE_CSV_LIST = "preProcessingPipelineCSVList";

	public static final String POSTPROCESSING_PIPELINE_CSV_LIST = "postProcessingPipelineCSVList";

	public static final String POSTMANUAL_PIPELINE_CSV_LIST = "postManualPipelineCSVList";
	
	public static final String ZIP_PATH = "zipPath";
	
	public static final String GAS_MAP = "gasMap";
	
	public static final String PROJECT_ID = "projectId";
	
	public static final String PROJECT_NAME = "projectName";
	
	public static final String PROJECT_DESCRIPTION = "projectDescription";
	
	public static final String PROCESS_INSTANCE_NAME = "processInstanceName";
	
	public static final String CALLBACK_TASK_ID = "callbackTaskId";
	
	// task instance priority constants
	
	public static final int PRIORITY_CONFIGURATION_STEP = 0;
	public static final int PRIORITY_ANNOTATION_TASK = 1;
	public static final int PRIORITY_CURATION_TASK = 2;
	public static final int PRIORITY_SYSTEM_TASK = 3;
	
	// process image scaleout ratio
	public static int PROCESS_IMAGE_SCALEOUT_RATIO = 1;
	
	public static String INIT_TIME = "00:00:00";
	
	public static final Pattern TASK_PATTERN = Pattern
	.compile("(?:^|\\.)(task)_(\\d+)(?:\\.|$)");
}
