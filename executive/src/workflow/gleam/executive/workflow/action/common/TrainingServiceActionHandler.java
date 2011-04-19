/*
 *  TrainingServiceActionHandler.java
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
package gleam.executive.workflow.action.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import gleam.executive.service.GateServiceManager;
import gleam.executive.workflow.jms.MessageProducer;
import gleam.executive.workflow.util.WorkflowUtil;
import gleam.gateservice.client.AnnotationTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.util.StringUtils;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class TrainingServiceActionHandler extends JbpmHandlerProxy {

	private static final long serialVersionUID = 1L;

	private GateServiceManager gateServiceManager;

	private MessageProducer messageProducer;

	protected final Log log = LogFactory.getLog(getClass());

	/*
	 * <inVarASKey>asTrainingKey</inVarASKey>
	 * <inVarTrainingTaskQueue>trainingTaskQueue</inVarTrainingTaskQueue>
	 * <inVarExtraASMappings>trainingExtraMappings</inVarExtraASMappings>
	 * <inVarParameterKey>parameterTrainingKey</inVarParameterKey>
	 * <inVarParameterValue>parameterTrainingValue</inVarParameterValue>
	 * <inOutVarNumberOfTrainingTasks>numberOfTrainingTasks</inOutVarNumberOfTrainingTasks>
	 */

	private String inVarASKey;

	private String inVarTrainingTaskQueue;

	private String inOutVarNumberOfTrainingTasks;

	private String inVarParameterKey;

	private String inVarParameterValue;

	/**
	 * Extra annotation set mappings. This is a comma-delimited list of
	 * <code>key=value</code> pairs. Any of the keys and/or values may be
	 * empty, indicating the default annotation set. Thus a value of
	 *
	 * data=fixed data,=ANNIE,dummy=
	 *
	 * denotes three mappings, and the GaS will receive
	 * <ul>
	 * <li>the contents of the <code>fixed</code> set from the doc service in
	 * its <code>data</code> annotation set,</li>
	 * <li>the contents of the <code>ANNIE</code> set from the doc service in
	 * its default set, and</li>
	 * <li>the contents of the default annotation set from the doc service in
	 * its <code>dummy</code> set.</li>
	 * </ul>
	 *
	 * To map the default set from the DS to the default set in the GaS, use a
	 * mapping consisting of just an equals sign with nothing either side.
	 *
	 * Whitespace either side of the commas and equals signs is ignored, but
	 * whitespace <i>within</i> annotation set names is preserved.
	 */
	private String inVarExtraASMappings;

	/**
	 * A message process variable is assigned the value of the message member.
	 * The process variable is created if it doesn't exist yet.
	 */
	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("TrainingServiceActionHandler START");

		// obtain targetProperties
		String asKey = (String) context.getVariable(getInVarASKey());
		log.debug("@@@@@@@ aSKey " + asKey);

		String trainingTaskQueue = (String) context
				.getVariable(getInVarTrainingTaskQueue());
		log.debug("@@@@@@@ trainingTaskQueue " + trainingTaskQueue);

		if (trainingTaskQueue != null) {
			Integer numberOfTrainingTasks = (Integer) context
					.getVariable(getInOutVarNumberOfTrainingTasks());
			log.debug("@@@@@@@ numberOfTrainingTasks " + numberOfTrainingTasks);

			if (numberOfTrainingTasks == null) {
				numberOfTrainingTasks = 0;
			}
			String extraASMappings = (String) context
					.getVariable(getInVarExtraASMappings());
			log.debug("@@@@@@@ extraASMappings " + extraASMappings);
			String parameterKey = (String) context
					.getVariable(getInVarParameterKey());
			log.debug("@@@@@@@ parameterKey " + parameterKey);
			String parameterValue = (String) context
					.getVariable(getInVarParameterValue());
			log.debug("@@@@@@@ parameterValue " + parameterValue);

			long processInstanceId = context.getProcessInstance().getId();
			log.debug("current ProcessInstance " + processInstanceId);
			long tokenId = context.getToken().getId();
			log.debug("current tokenId " + tokenId);
			String tokenName = context.getToken().getName();
			log.debug("current tokenName " + tokenName);

			Map<String, String> parameterMappings = null;
			if (parameterKey != null && parameterValue != null) {
				parameterMappings = new HashMap<String, String>();
				parameterMappings.put(parameterKey, parameterValue);
			}

			List<AnnotationTask> annotationTasks = new ArrayList<AnnotationTask>();

			String[] taskArray = WorkflowUtil
					.getTrainingTasks(trainingTaskQueue);

			int currentQueueLength = 0;
			if (taskArray != null) {
				currentQueueLength = taskArray.length;

				log.debug("@@@@@@@ currentQueueLength " + currentQueueLength);

				for (int i = 0; i < currentQueueLength; i++) {
					String[] elements = WorkflowUtil.splitToken(taskArray[i]);
					String documentId = elements[0];
					String asValue = elements[1];

					Map<String, String> asMappings = new HashMap<String, String>();
					// main AS mapping, typically involving the annotator name
					if (asKey != null && asValue != null) {
						asMappings.put(asKey, asValue);
					}

					// process extra AS mappings
					if (extraASMappings != null) {
						String[] mappings = StringUtils
								.commaDelimitedListToStringArray(extraASMappings);
						if (mappings != null) {
							for (String m : mappings) {
								int indexOfEquals = m.indexOf('=');
								String k = m.substring(0, indexOfEquals).trim();
								String v = m.substring(indexOfEquals + 1)
										.trim();
								asMappings.put(k, v);
							}
						}
					}
					AnnotationTask annotationtask = new AnnotationTask(
							documentId, asMappings);
					annotationTasks.add(annotationtask);

				}
				/*
				gateServiceManager.processRemoteDocuments(String
						.valueOf(tokenId), annotationTasks, parameterMappings);
						*/
			} else {
				log.debug("QUEUE IS EMPTY");
			}
			// empty the queue.
			context.setVariable(JPDLConstants.TRAINING_TASK_QUEUE, null);

			// increase number of training tasks performed
			context.setVariable(JPDLConstants.NUMBER_OF_TRAINING_TASKS,
					numberOfTrainingTasks + currentQueueLength);
		} else {
			log.debug("TRAINING_TASK_QUEUE IS NULL - DO NOT DO ANYTHING ");
		}

		log.debug("TrainingServiceActionHandler END");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public GateServiceManager getGateServiceManager() {
		return gateServiceManager;
	}

	public void setGateServiceManager(GateServiceManager gateServiceManager) {
		this.gateServiceManager = gateServiceManager;
	}

	public String getInVarASKey() {
		return inVarASKey;
	}

	public void setInVarASKey(String inVarASKey) {
		this.inVarASKey = inVarASKey;
	}

	public String getInVarExtraASMappings() {
		return inVarExtraASMappings;
	}

	public void setInVarExtraASMappings(String inVarExtraASMappings) {
		this.inVarExtraASMappings = inVarExtraASMappings;
	}

	public String getInVarParameterKey() {
		return inVarParameterKey;
	}

	public void setInVarParameterKey(String inVarParameterKey) {
		this.inVarParameterKey = inVarParameterKey;
	}

	public String getInVarParameterValue() {
		return inVarParameterValue;
	}

	public void setInVarParameterValue(String inVarParameterValue) {
		this.inVarParameterValue = inVarParameterValue;
	}

	public String getInVarTrainingTaskQueue() {
		return inVarTrainingTaskQueue;
	}

	public void setInVarTrainingTaskQueue(String inVarTrainingTaskQueue) {
		this.inVarTrainingTaskQueue = inVarTrainingTaskQueue;
	}

	public String getInOutVarNumberOfTrainingTasks() {
		return inOutVarNumberOfTrainingTasks;
	}

	public void setInOutVarNumberOfTrainingTasks(
			String inOutVarNumberOfTrainingTasks) {
		this.inOutVarNumberOfTrainingTasks = inOutVarNumberOfTrainingTasks;
	}

	public MessageProducer getMessageProducer() {
		return messageProducer;
	}

	public void setMessageProducer(MessageProducer messageProducer) {
		this.messageProducer = messageProducer;
	}

}
