/*
 *  WorkflowUtil.java
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
 *  Milan Agatonovic 8th January 2007
 *
 *  $Id$
 */
package gleam.executive.workflow.util;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import gleam.executive.workflow.util.CSVUtil;
import gleam.executive.workflow.util.JPDLConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class WorkflowUtil.
 */
/**
 * @author agaton
 * 
 */
public class WorkflowUtil {

	/** The log. */
	private static Log log = LogFactory.getLog(WorkflowUtil.class);

	/**
	 * Checks if variable should be rendered as input type=hidden.
	 * 
	 * @param variableName
	 *            the name of the JPDL variable
	 * 
	 * @return true or false
	 */

	public static boolean isHidden(String variableName) {
		return variableName.startsWith(JPDLConstants.JPDL_HIDDEN_PREFIX + "_");
	}

	/**
	 * Checks if variable should be rendered in ajax tooltip on mouseover help
	 * button.
	 * 
	 * @param variableName
	 *            the name of the JPDL variable
	 * 
	 * @return true or false
	 */

	public static boolean isHelp(String variableName) {
		return variableName.startsWith(JPDLConstants.JPDL_HELP_PREFIX + "_");
	}

	/**
	 * Checks if variable should be rendered as input type=file.
	 * 
	 * @param variableName
	 *            the name of the JPDL variable
	 * 
	 * @return true or false
	 */

	public static boolean isFile(String variableName) {
		return variableName.startsWith(JPDLConstants.JPDL_FILE_PREFIX + "_");
	}

	/**
	 * Checks if variable should be rendered as label.
	 * 
	 * @param variableName
	 *            the name of the JPDL variable
	 * 
	 * @return true or false
	 */

	public static boolean isLabel(String variableName) {
		return variableName.startsWith(JPDLConstants.JPDL_LABEL_PREFIX + "_");
	}

	/**
	 * Checks if variable should be rendered as checkbox.
	 * 
	 * @param variableName
	 *            the name of the JPDL variable
	 * 
	 * @return true or false
	 */
	public static boolean isCheckBox(String variableName) {
		return variableName
				.startsWith(JPDLConstants.JPDL_CHECKBOX_PREFIX + "_");
	}

	/**
	 * Checks if variable should be rendered as text area.
	 * 
	 * @param variableName
	 *            the name of the JPDL variable
	 * 
	 * @return true or false
	 */

	public static boolean isTextArea(String variableName) {
		return variableName
				.startsWith(JPDLConstants.JPDL_TEXTAREA_PREFIX + "_");
	}

	/**
	 * Checks if variable should be rendered as link.
	 * 
	 * @param variableName
	 *            the name of the JPDL variable
	 * 
	 * @return true or false
	 */
	public static boolean isLink(String variableName) {
		return variableName.startsWith(JPDLConstants.JPDL_LINK_PREFIX + "_");
	}

	/**
	 * Checks if variable should be rendered as URL.
	 * 
	 * @param variableName
	 *            the name of the JPDL variable
	 * 
	 * @return true or false
	 */

	public static boolean isURL(String variableName) {
		return variableName.startsWith(JPDLConstants.JPDL_URL_PREFIX + "_");
	}

	/**
	 * Checks if variable should be rendered as popup.
	 * 
	 * @param variableName
	 *            the name of the JPDL variable
	 * 
	 * @return true or false
	 */

	public static boolean isPopUp(String variableName) {
		return variableName.startsWith(JPDLConstants.JPDL_POPUP_PREFIX + "_");
	}

	/**
	 * Checks if variable should be rendered as section.
	 * 
	 * @param variableName
	 *            the name of the JPDL variable
	 * 
	 * @return true or false
	 */

	public static boolean isSection(String variableName) {
		return variableName.startsWith(JPDLConstants.JPDL_SECTION_PREFIX + "_");
	}

	/**
	 * Checks if variable should be rendered as select box.
	 * 
	 * @param variableName
	 *            the name of the JPDL variable
	 * 
	 * @return true or false
	 */

	public static boolean isCombo(String variableName) {
		return variableName.startsWith(JPDLConstants.JPDL_SELECTBOX_PREFIX
				+ "_")
				|| variableName.startsWith(JPDLConstants.JPDL_MULTIBOX_PREFIX
						+ "_");
	}

	/**
	 * Extracts the path which should be passed to CustomPopupTag as parameter
	 * The full coded name is: P_/popupEditUploadCorpus.html_Add Corpus P
	 * denotes popup /popupEditUploadCorpus.html is path the label is Add
	 * Corpus, at the end.
	 * 
	 * @param variableName
	 *            the name of the JPDL variable
	 * 
	 * @return the name of the method
	 */

	public static String getPopupPath(String variableName) {
		log.debug("VARIABLE MAPPED NAME : " + variableName);
		String afterRemovedPopupPrefix = variableName
				.substring(JPDLConstants.JPDL_POPUP_PREFIX.length() + 1);
		log.debug("AFTER POPUP PREFIX IS REMOVED: " + afterRemovedPopupPrefix);
		String popupPath = afterRemovedPopupPrefix.substring(0,
				afterRemovedPopupPrefix.indexOf("_"));
		log.debug("popupPath : " + popupPath);
		return popupPath;
	}
	
	/**
	 * Extracts the path which should be passed to CustomURLTag as parameter
	 * The full coded name is: U_/popupEditUploadCorpus.html_Add Corpus 
	 * denotes popup /popupEditUploadCorpus.html is path the label is Add
	 * Corpus, at the end.
	 * 
	 * @param variableName
	 *            the name of the JPDL variable
	 * 
	 * @return the name of the method
	 */

	public static String getURLPath(String variableName) {
		log.debug("VARIABLE MAPPED NAME : " + variableName);
		String afterRemovedURLPrefix = variableName
				.substring(JPDLConstants.JPDL_URL_PREFIX.length() + 1);
		log.debug("AFTER URL PREFIX IS REMOVED: " + afterRemovedURLPrefix);
		String urlPath = afterRemovedURLPrefix.substring(0,
				afterRemovedURLPrefix.indexOf("?"));
		log.debug("urlPath : " + urlPath);
		return urlPath;
	}

	/**
	 * Extracts the name of the method that should be invoked from the
	 * CustomSelectTag. The full coded name is:
	 * M_:usersInRoleList#curator_Curators M denotes multibox usersInRoleList is
	 * the name of the method which takes one parameter: curator the label is
	 * Curaots, at the end.
	 * 
	 * @param variableName
	 *            the name of the JPDL variable
	 * 
	 * @return the name of the method
	 */

	public static String[] getComboBoxMethodSignature(String variableName) {
		String methodName = null;
		String parameterCSVList = null;
		log.debug("VARIABLE MAPPED NAME : " + variableName);
		String afterRemovedComboPrefix = variableName
				.substring(JPDLConstants.JPDL_SELECTBOX_PREFIX.length() + 1);
		log.debug("AFTER COMBO PREFIX IS REMOVED: " + afterRemovedComboPrefix);
		if (afterRemovedComboPrefix.startsWith(":")
				&& (afterRemovedComboPrefix.indexOf("_") != -1))
			if (afterRemovedComboPrefix.indexOf(";") != -1
					&& afterRemovedComboPrefix.indexOf(";") < afterRemovedComboPrefix
							.indexOf("_")) {
				methodName = afterRemovedComboPrefix.substring(1,
						afterRemovedComboPrefix.indexOf(";"));
				parameterCSVList = afterRemovedComboPrefix.substring(
						afterRemovedComboPrefix.indexOf(";") + 1,
						afterRemovedComboPrefix.indexOf("_"));
			} else {
				// no csv params suplied
				if (afterRemovedComboPrefix.indexOf("_") != -1) {
					methodName = afterRemovedComboPrefix.substring(1,
							afterRemovedComboPrefix.indexOf("_"));
				} else {
					methodName = afterRemovedComboPrefix.substring(1);
				}
			}
		log.debug("METHOD NAME : " + methodName);
		log.debug("PARAMETER CSV LIST : " + parameterCSVList);
		return new String[] { methodName, parameterCSVList };
	}

	

	public static String getLinkParamsString(String variableName) {
		log.debug("VARIABLE MAPPED NAME : " + variableName);
		String afterRemovedLinkPrefix = variableName
				.substring(JPDLConstants.JPDL_LINK_PREFIX.length() + 1);
		String result = "";
		log.debug("AFTER LINK PREFIX IS REMOVED: " + afterRemovedLinkPrefix);
		if (afterRemovedLinkPrefix.indexOf("_") != -1){
			result = afterRemovedLinkPrefix.substring(
					0, afterRemovedLinkPrefix.lastIndexOf("_"));
			
	    }
		log.debug("PARAMETER STRING : " + result);
		return result;
	}
	
	public static String getURLParamsString(String variableName) {
		log.debug("VARIABLE MAPPED NAME : " + variableName);
		String afterRemovedURLPrefix = variableName
				.substring(JPDLConstants.JPDL_URL_PREFIX.length() + 1);
		String result = "";
		log.debug("AFTER URL PREFIX IS REMOVED: " + afterRemovedURLPrefix);
		if (afterRemovedURLPrefix.indexOf("?") != -1){
			result = afterRemovedURLPrefix.substring(
					afterRemovedURLPrefix.indexOf("?")+1, afterRemovedURLPrefix.lastIndexOf("_"));
			
	    }
		log.debug("PARAMETER STRING : " + result);
		return result;
	}
	
	
	/**
	 * Checks if is multi box.
	 * 
	 * @param variableName
	 *            the variable name
	 * 
	 * @return true, if is multi box
	 */
	public static boolean isMultiBox(String variableName) {
		return variableName
				.startsWith(JPDLConstants.JPDL_MULTIBOX_PREFIX + "_");
	}

	// generic method for resolving parameter name (label)
	public static String getLabel(String mappedName) {
		String label = "";
		log.debug("extracting label from: "+mappedName);
		if (isHidden(mappedName)) { // check if it is hidden
			label = mappedName.substring(JPDLConstants.JPDL_HIDDEN_PREFIX
					.length() + 1);
		} else if (isFile(mappedName)) { // check if it is file
			label = mappedName.substring(JPDLConstants.JPDL_FILE_PREFIX
					.length() + 1);
		} else if (isLabel(mappedName)) { // check if it label
			label = mappedName.substring(JPDLConstants.JPDL_LABEL_PREFIX
					.length() + 1);
		} else if (isTextArea(mappedName)) { // check if it is text area
			label = mappedName.substring(JPDLConstants.JPDL_TEXTAREA_PREFIX
					.length() + 1);
		} else if (isCombo(mappedName)) { // check if it is combo box
			label = mappedName.substring(JPDLConstants.JPDL_SELECTBOX_PREFIX
					.length() + 1);
			label = label.substring(label.indexOf("_") + 1);
		} else if (isLink(mappedName)) { // check if it is link
			label = mappedName.substring(mappedName.lastIndexOf("_") + 1);
		} else if (WorkflowUtil.isURL(mappedName)) { // check if it is URL
			label = mappedName.substring(mappedName.lastIndexOf("_") + 1);
		} else if (WorkflowUtil.isPopUp(mappedName)) { // check if it is Popup
			label = mappedName.substring(JPDLConstants.JPDL_POPUP_PREFIX
					.length() + 1);
			label = label.substring(label.indexOf("_") + 1);
		} else if (WorkflowUtil.isCheckBox(mappedName)) { // check if it is
															// CheckBox
			label = mappedName.substring(JPDLConstants.JPDL_CHECKBOX_PREFIX
					.length() + 1);
		} else if (WorkflowUtil.isSection(mappedName)) { // check if it is
															// CheckBox
			label = mappedName.substring(JPDLConstants.JPDL_SECTION_PREFIX
					.length() + 1);
		} else {
			label = mappedName;
		}
		log.debug("extracted label : "+label);
		return label;
	}

	/**
	 * Extracts the IDS from callback string.
	 * 
	 * @param callbackParameter
	 *            the value of the callback parameter
	 * 
	 * @return the tokens (IDs)
	 */
	public static String[] resolveCallbackParameter(String callbackParameter) {
		return callbackParameter
				.split(JPDLConstants.CALLBACK_PARAMETER_DELIMITER);
	}

	/**
	 * Checks the document status 0: if csv string of document annotators
	 * contains particular name status 1:.
	 * 
	 * @param documentSuperAnnotationCSVString
	 *            the document super annotation CSV string
	 * @param performer
	 *            annotator's name = annotation set name
	 * @param unique
	 *            the unique
	 * @param documentAnnotationCSVString
	 *            csv string of document annotators
	 * @param documentCancelationCSVString
	 *            the document cancelation CSV string
	 * @param docId
	 *            the doc id
	 * @param numberOfAnnotators
	 *            - max number of elements in CSV string
	 * 
	 * @return true or false
	 */
	public static String checkDocumentStatus(String docId,
			String documentSuperAnnotationCSVString,
			String documentAnnotationCSVString,
			String documentCancelationCSVString, int numberOfAnnotators,
			String performer, boolean unique) {

		// String status = JPDLConstants.DOCUMENT_NOT_AVAILABLE;
		log.debug("@@@@@@ CHECK DOCUMENT STATUS @@@@@");
		if (documentSuperAnnotationCSVString != null
				&& documentSuperAnnotationCSVString.indexOf(performer) != -1) {
			log.debug("Found that performer '" + performer
					+ "' must superannotate the document '" + docId + "'.");
			return JPDLConstants.DOCUMENT_AVAILABLE_FOR_SUPERANNOTATION;
		}

		if (documentCancelationCSVString != null
				&& documentCancelationCSVString.indexOf(performer) != -1) {
			log.debug("Found that performer '" + performer
					+ "' canceled the document '" + docId + "' before.");
			return JPDLConstants.DOCUMENT_NOT_AVAILABLE;
		}

		if (documentAnnotationCSVString == null) {
			log.debug("Found that nobody has annotated the document '" + docId
					+ "' before.");
			return JPDLConstants.DOCUMENT_AVAILABLE;
		} else {
			String[] documentAnnotations = StringUtils
					.commaDelimitedListToStringArray(documentAnnotationCSVString);

			int numberOfAnnotatorsLeft = numberOfAnnotators
					- documentAnnotations.length;
			if (numberOfAnnotatorsLeft > 0) {
				if (!unique) {
					log
							.debug("Do not check if annotator has already annotated '"
									+ docId + "' before.");
					return JPDLConstants.DOCUMENT_AVAILABLE;
				} else {
					int i = 0;
					boolean found = false;
					while (i < documentAnnotations.length && !found) {
						if (documentAnnotations[i].equals(performer)) {
							found = true;
						}
						i++;
					}
					if (!found) {
						log.debug("Found that performer '" + performer
								+ "' did not annotate the document '" + docId
								+ "' before.");
						return JPDLConstants.DOCUMENT_AVAILABLE;

					} else {
						log.debug("Found that performer '" + performer
								+ "' annotated the document '" + docId
								+ "' before.");
						return JPDLConstants.DOCUMENT_NOT_AVAILABLE;
					}
				}
			}//
			else {
				log.debug("Found that document '" + docId
						+ "' is annotated enough times before.");
				return JPDLConstants.DOCUMENT_NOT_AVAILABLE;
			}

		}

	}

	/**
	 * Produce training task.
	 * 
	 * @param documentId
	 *            the document id
	 * @param annotationSetName
	 *            the annotation set name
	 * @param trainingTaskQueue
	 *            the training task queue
	 * 
	 * @return the string
	 */
	public static String produceTrainingTask(String trainingTaskQueue,
			String documentId, String annotationSetName) {
		String result = null;
		if (documentId != null && annotationSetName != null) {
			String[] elements = { documentId, annotationSetName };
			String token = CSVUtil.createTokenFromElements(elements,
					JPDLConstants.INTER_TOKEN_SEPARATOR);
			result = CSVUtil.appendTokenToCSVString(token, trainingTaskQueue);
		}
		return result;
	}

	/**
	 * Gets the training tasks.
	 * 
	 * @param trainingTaskQueue
	 *            the training task queue
	 * 
	 * @return the training tasks
	 */
	public static String[] getTrainingTasks(String trainingTaskQueue) {
		return StringUtils.commaDelimitedListToStringArray(trainingTaskQueue);
	}

	/**
	 * Split token.
	 * 
	 * @param token
	 *            the token
	 * 
	 * @return the string[]
	 */
	public static String[] splitToken(String token) {
		return StringUtils.delimitedListToStringArray(token,
				JPDLConstants.INTER_TOKEN_SEPARATOR);
	}

	/*
	 * public static String resolveAnnotationSetName(String
	 * documentAnnotationCSVString){ Set<String> documentAnnotationList =
	 * StringUtils.commaDelimitedListToSet(documentAnnotationCSVString); return
	 * resolveAnnotationSetName(documentAnnotationList); }
	 */

	/**
	 * Mark document as annotated.
	 * 
	 * @param performer
	 *            the performer
	 * @param documentAnnotationCSVString
	 *            the document annotation CSV string
	 * 
	 * @return the string
	 */
	public static String markDocumentAsAnnotated(String performer,
			String documentAnnotationCSVString) {

		return CSVUtil.appendTokenToCSVString(performer,
				documentAnnotationCSVString);

	}

	/**
	 * Mark document as not annotated.
	 * 
	 * @param performer
	 *            the performer
	 * @param documentAnnotationCSVString
	 *            the document annotation CSV string
	 * 
	 * @return the string
	 */
	public static String markDocumentAsNotAnnotated(String performer,
			String documentAnnotationCSVString) {

		return CSVUtil.removeTokenFromCSVString(performer,
				documentAnnotationCSVString);
	}

	/**
	 * Mark document as canceled.
	 * 
	 * @param performer
	 *            the performer
	 * @param documentCancelationCSVString
	 *            the document cancelation CSV string
	 * 
	 * @return the string
	 */
	public static String markDocumentAsCanceled(String performer,
			String documentCancelationCSVString) {

		return CSVUtil.appendTokenToCSVString(performer,
				documentCancelationCSVString);

	}

	/**
	 * Removes the document from list.
	 * 
	 * @param docId
	 *            the doc id
	 * @param documentCSVString
	 *            the document CSV string
	 * 
	 * @return the string
	 */
	public static String removeDocumentFromList(String docId,
			String documentCSVString) {

		return CSVUtil.removeTokenFromCSVString(docId, documentCSVString);
	}

	/**
	 * Adds the document to list.
	 * 
	 * @param docId
	 *            the doc id
	 * @param documentCSVString
	 *            the document CSV string
	 * 
	 * @return the string
	 */
	public static String addDocumentToList(String docId,
			String documentCSVString) {

		return CSVUtil.appendTokenToCSVString(docId, documentCSVString);
	}

	/**
	 * Creates the annotation schema CSV.
	 * 
	 * @param baseAnnotationSchemaURL
	 *            the base annotation schema URL
	 * @param annotationSchemaCSVNames
	 *            the annotation schema CSV names
	 * 
	 * @return the string
	 */
	public static String createAnnotationSchemaCSV(
			String annotationSchemaCSVNames, String baseAnnotationSchemaURL) {
		String result = "";
		if (baseAnnotationSchemaURL == null
				|| "".equals(baseAnnotationSchemaURL)) {
			result = annotationSchemaCSVNames;
		} else {
			String[] names = StringUtils
					.commaDelimitedListToStringArray(annotationSchemaCSVNames);
			int len = names.length;
			String[] resArray = new String[len];
			for (int i = 0; i < len; i++) {
				resArray[i] = baseAnnotationSchemaURL + names[i];
			}
			result = StringUtils.arrayToCommaDelimitedString(resArray);
		}

		return result;
	}

	/**
	 * Find document name by id.
	 * 
	 * @param docId
	 *            the doc id
	 * 
	 * @return the string
	 */
	public static String findDocumentNameById(String docId) {
		String documentName = null;
		int index = docId.indexOf("___");
		if (index != -1) {
			documentName = docId.substring(0, index);
		} else {
			documentName = docId;
		}
		return documentName;
	}

	/**
	 * Creates the callback task id.
	 * 
	 * @param processDefinitionName
	 *            the process definition name
	 * @param processInstanceId
	 *            the process instance id
	 * @param documentId
	 *            the document id
	 * @param nodeName
	 *            the node name
	 * @param taskInstanceId
	 *            (tokenId or TaskInstanceId)
	 * @param taskTypeSuffix
	 *            the task type suffix
	 * @return the callback task id
	 */
	public static String createCallbackTaskId(String processDefinitionName,
			long processInstanceId, String documentId, String nodeName,
			long taskInstanceId, String taskTypeSuffix) {
		String[] params = new String[6];
		params[0] = processDefinitionName;
		params[1] = String.valueOf(processInstanceId);
		params[2] = documentId;
		params[3] = nodeName;
		params[4] = String.valueOf(taskInstanceId);
		params[5] = taskTypeSuffix;
		String callbackTaskId = messageFormatter(
				JPDLConstants.CALLBACK_TASK_ID_FORMAT, params);
		log.debug("callbackTaskId: " + callbackTaskId);
		return callbackTaskId;
	}

	/**
	 * Creates the Annotation Status Variable Name.
	 * 
	 * @param nodeName
	 *            the node name
	 * @param documentId
	 *            the document id
	 * @param processInstanceId
	 *            the process instance id
	 * @param processDefinitionName
	 *            the process definition name
	 * 
	 * @return the Annotation Status Variable Name
	 */
	public static String createAnnotationStatusVariableName(
			String processDefinitionName, long processInstanceId,
			String documentId) {
		String variableName = "";
		String[] params = new String[3];
		params[0] = processDefinitionName;
		params[1] = String.valueOf(processInstanceId);
		params[2] = documentId;
		variableName = messageFormatter(JPDLConstants.ANNOTATION_STATUS_FORMAT,
				params);
		log.debug("variableName: " + variableName);
		return variableName;
	}

	public static String createAnnotationStatusVariablePrefix(
			String processDefinitionName, long processInstanceId) {
		String prefix = "";
		String[] params = new String[2];
		params[0] = processDefinitionName;
		params[1] = String.valueOf(processInstanceId);
		prefix = messageFormatter(
				JPDLConstants.ANNOTATION_STATUS_PREFIX_FORMAT, params);
		//log.debug("prefix: " + prefix);
		return prefix;
	}

	/*
	 * denotes if the task instance is "configuration step"
	 */
	public static boolean isConfigurationStep(int priority) {
		if (priority != JPDLConstants.PRIORITY_CONFIGURATION_STEP)
			return false;
		else
			return true;
	}

	/*
	 * denotes if the task instance is "annotation task"
	 */
	public static boolean isAnnotationTask(int priority) {
		if (priority != JPDLConstants.PRIORITY_ANNOTATION_TASK)
			return false;
		else
			return true;
	}
	
	/*
	 * denotes if the task instance is "review task"
	 */
	public static boolean isReviewTask(int priority) {
		if (priority != JPDLConstants.PRIORITY_CURATION_TASK)
			return false;
		else
			return true;
	}
	
	/*
	 * denotes if the task instance is "annotation task"
	 */
	public static boolean isSystemTask(int priority) {
		if (priority != JPDLConstants.PRIORITY_SYSTEM_TASK)
			return false;
		else
			return true;
	}
	
	/**
	 * Parameterizes string using MessageFormat.
	 * 
	 * @param template
	 * @param parameters
	 * @return String
	 */

	public static String messageFormatter(String template, String[] parameters) {
		String message = null;
		boolean proceed = true;
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i] == null) {
				proceed = false;
				break;
			}
		}
		if (proceed) {
			MessageFormat form = new MessageFormat(template);
			message = form.format(parameters);
		}
		return message;
	}
	
	  public static String collectionToFormattedCSVString(String prefix, Collection<String> coll, String template, String parameter){
		  
		  StringBuffer sb = new StringBuffer();
		  Iterator<String> it = coll.iterator();
		  
		  int i = 0;
		  while (it.hasNext()) {
			String str = it.next();
			String[] params = {parameter, str};
			String message = "<a href=\"" + prefix + messageFormatter(template, params) + "\">" + str + "</a>";
		    if (i > 0) {
		      sb.append(", ");
		    }
		    sb.append(message);
		    i++;
		  }
		  return sb.toString();
	  }


}
