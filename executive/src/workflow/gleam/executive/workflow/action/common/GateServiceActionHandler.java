package gleam.executive.workflow.action.common;

import java.util.HashMap;
import java.util.Map;
import gleam.executive.service.GateServiceManager;
import gleam.executive.workflow.jms.MessageProducer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.util.StringUtils;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class GateServiceActionHandler extends JbpmHandlerProxy {

	private static final long serialVersionUID = 1L;

	private GateServiceManager gateServiceManager;

	private MessageProducer messageProducer;

	protected final Log log = LogFactory.getLog(getClass());

	/*
	       <inVarMode>test</inVarMode>
	       <inVarASKey>asTrainingKey</inVarASKey>
		   <inVarASValue>annotationSetName</inVarASValue>
		   <inVarExtraASMappings>trainingExtraMappings</inVarExtraASMappings>
		   <inVarParameterKey>parameterTrainingKey</inVarParameterKey>
		   <inVarParameterValue>parameterTrainingValue</inVarParameterValue>
		   <inVarDocumentId>documentId</inVarDocumentId>
	 */

	/**
	 *  defined globally in process definition tells if process is in test or
	 *  production mode. If ommitted, the default mode is 'production'
	 */
	private String inVarMode;

	private String inVarASKey;

	private String inVarASValue;

	private String inVarParameterKey;

	private String inVarParameterValue;

	private String inVarDocumentId;

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
		log.debug("GateServiceActionHandler START");


			// obtain targetProperties
		    String mode = (String) context.getVariable(getInVarMode());
		    log.debug("@@@@@@@ mode " + mode);
			String asKey = (String) context.getVariable(getInVarASKey());
			log.debug("@@@@@@@ aSKey " + asKey);
			String asValue = (String) context.getVariable(getInVarASValue());
			log.debug("@@@@@@@ aSValue " + asValue);
			String extraASMappings = (String) context
					.getVariable(getInVarExtraASMappings());
			log.debug("@@@@@@@ extraASMappings " + extraASMappings);
			String parameterKey = (String) context
					.getVariable(getInVarParameterKey());
			log.debug("@@@@@@@ parameterKey " + parameterKey);
			String parameterValue = (String) context
					.getVariable(getInVarParameterValue());
			log.debug("@@@@@@@ parameterValue " + parameterValue);
			String documentId = (String) context.getContextInstance()
					.getVariable(getInVarDocumentId(), context.getToken());
			log.debug("@@@@@@@ documentId " + documentId);

			long processInstanceId = context.getProcessInstance().getId();
			log.debug("current ProcessInstance " + processInstanceId);
			long tokenId = context.getToken().getId();
			log.debug("current tokenId " + tokenId);
			String tokenName = context.getToken().getName();
			log.debug("current tokenName " + tokenName);

			if(!JPDLConstants.TEST_MODE.equals(mode)){
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
						String v = m.substring(indexOfEquals + 1).trim();
						asMappings.put(k, v);
					}
				}
			}

			Map<String, String> parameterMappings = null;
			if (parameterKey != null && parameterValue != null) {
				parameterMappings = new HashMap<String, String>();
				parameterMappings.put(parameterKey, parameterValue);
			}
			/*
			gateServiceManager.processRemoteDocument(String.valueOf(tokenId),
					documentId, asMappings, parameterMappings);
			*/
			}
			else {
				log.debug("TEST MODE");
				messageProducer.sendMessage(String.valueOf(tokenId));

			}
		log.debug("GateServiceActionHandler END");
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

	public String getInVarASValue() {
		return inVarASValue;
	}

	public void setInVarASValue(String inVarASValue) {
		this.inVarASValue = inVarASValue;
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

	public String getInVarDocumentId() {
		return inVarDocumentId;
	}

	public void setInVarDocumentId(String inVarDocumentId) {
		this.inVarDocumentId = inVarDocumentId;
	}

	public String getInVarMode() {
		return inVarMode;
	}

	public void setInVarMode(String inVarMode) {
		this.inVarMode = inVarMode;
	}

	public MessageProducer getMessageProducer() {
		return messageProducer;
	}

	public void setMessageProducer(MessageProducer messageProducer) {
		this.messageProducer = messageProducer;
	}

}
