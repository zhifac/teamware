/*
 *  WorkflowTestData.java
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
package gleam.executive.workflow;

public class WorkflowTestData {

	 public static final String PROCESS_ARCHIVE = "main.zip";
	 public static final String SUB_PROJECT_SETUP_ARCHIVE = "setup.zip";
	 public static final String SUB_AUTOMATIC_ANNOTATION_ARCHIVE = "automaticAnnotation.zip";
	 public static final String SUB_MANUAL_ANNOTATION_ARCHIVE = "manualAnnotation.zip";
	 public static final String SUB_REVIEW_ARCHIVE = "review.zip";

	 public static final String PROCESS_ARCHIVE_NAME = "main";
	 public static final String SUB_PROCESS_SETUP_NAME =  "setup";
	 public static final String SUB_AUTOMATIC_ANNOTATION_NAME = "automatic annotation";
	 public static final String SUB_MANUAL_ANNOTATION_NAME = "manual annotation";
	 public static final String PROJECT_SETTINGS_OVERVIEW_TASK_NODE = "project settings overview";

	 public static final String ON ="on";
	 public static final String OFF ="off";
	 public static final String INITIATOR ="agaton";
	 public static final String CURATOR_CSV_LIST ="milan,milena";
	 public static final String ANNOTATOR_CSV_LIST ="lemel,clandestino,thomas";
	 public static final String ANNOTATION_SCHEMA_CSV_URLS="Measurement.xml,Reference.xml";
	 public static final String ANNOTATORS_PER_DOCUMENT="2";
	 public static final String ANNOTATOR_HAS_TO_BE_UNIQUE_FOR_DOCUMENT= ON;
	 public static final String CAN_CANCEL = ON;
	 public static final String CORPUS_ID="CorpusX";
	 public static final String DOCUMENT_CSV_LIST="doc1,doc2";
	 public static final String ANONYMOUS_ANNOTATION= ON;

	 public static final String DO_SETUP = ON;
	 public static final String DO_AUTOMATIC = ON;
	 public static final String DO_MANUAL = ON;
	 
	 public static final String GAS_1_NAME="preprocessing1"; 
	 public static final String GAS_2_NAME="preprocessing2";
	 
	 public static final String GAS_1_URL = "http://localhost:8080/matrixware/preprocessing-service/services/GATEService";
	 public static final String GAS_2_URL = "http://localhost:8080/matrixware/preprocessing-service/services/GATEService";
	 
	 public static final String TRANSITION_SPECIFY_NEXT_GAS = "Specify Next GAS";
	 public static final String TRANSITION_FINISH= "Finish";
	 public static final String TRANSITION_RUN = "Run";
	 public static final String TRANSITION_CHANGE_CORPUS = "Change Corpus";
	 public static final String TRANSITION_CANCEL_PROJECT = "Cancel Project";


}
