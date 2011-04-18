/**
 * Created on Jan 23, 2006
 *
 * $Id$
 * $Revision$
 */
package gleam.executive.workflow.sm;

import gleam.executive.workflow.util.InvalidVariableNameException;
import gleam.executive.workflow.util.InvalidVariableValueException;
import gleam.executive.workflow.util.WorkflowException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.DecisionHandler;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.def.TaskControllerHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.dom4j.Attribute;
import org.dom4j.Element;

/**
 * Handler that allow retrieval of beans defined in the Spring container from
 * within jBPM. The class implements the family of jBPM handler interfaces
 * namely: ActionHandler, AssignmentHandler, DecisionHandler,
 * TaskControllerHandler. The class can be used through jBPM delegation
 * facilities, for example:
 * 
 * <pre>
 *       	&lt;action config-type=&quot;beanName&quot; class=&quot;org.springframework.workflow.jbpm.SpringHandler&quot;&gt;
 *      		&lt;beanName&gt;SpringJbpmAction&lt;/beanName&gt;
 *            &lt;factoryKey&gt;myFactoryInstance&lt;factoryName&gt;
 *      	&lt;/action&gt;
 * </pre>
 * 
 * where beanName represents a jBPM actionHandler defined inside Spring
 * container using it's capabilities (IoC, AOP, etc). The optional factoryKey
 * parameter is used to specify the key under which the bean factory can be
 * found; if there is only one JbpmFactoryLocator inside the classloader,
 * factoryKey can be skipped.
 * 
 * <p>
 * The <b>process definition</b> can also inject property values into the target
 * handler by specifying a <code>&lt;targetProperties></code> section in the
 * &lt;action> definition like this:
 * 
 * <pre>
 *    &lt;action config-type=&quot;bean&quot; class=&quot;org.springframework.workflow.jbpm.JbpmHandler&quot;&gt;
 *        &lt;beanName&gt;SpringJbpmAction&lt;/beanName&gt;
 *        &lt;factoryKey&gt;myFactoryInstance&lt;factoryName&gt;
 *        &lt;targetProperties&gt;
 *            &lt;somePropertyInSpringJbpmAction&gt;the new value for the property&lt;/somePropertyInSpringJbpmAction&gt;
 *            &lt;someIntegerProperty&gt;5&lt;/someIntegerProperty&gt;
 *            ...
 *        &lt;/targetProperties&gt;
 *    &lt;/action&gt;
 * </pre>
 * 
 * <p>
 * Since the resulting handler instance will be specific to that action, it is
 * important that the target Spring bean definition (in this example, the bean
 * named "SpringJpbmAction") be defined with the attribute
 * <code>singleton="false"</code> if any <code>&lt;targetProperties></code> are
 * defined. That way, a new instance will be created each time it is referenced
 * by the process definition.
 * <p>
 * Using <code>&lt;targetProperties></code> promotes reuse of handler code by
 * allowing you to parameterize certain handler functionality for use by the
 * process designer. Use Spring's IoC capabilities to wire an action handler
 * with infrastructure objects (such as data access objects or other service
 * objects). Use the process definition's ability to set
 * <code>&lt;targetProperties></code> to set parameters to the handler that is
 * specific to the process definition.
 * <p>
 * 
 * @author Costin Leau
 * @author Milan Agatonovic
 */
public class JbpmDataflowHandlerProxy implements ActionHandler,
		AssignmentHandler, DecisionHandler, TaskControllerHandler {
	private static final Log log = LogFactory
			.getLog(JbpmDataflowHandlerProxy.class);

	private String factoryKey;

	/**
	 * Spring beanName name.
	 */
	private String targetBean;

	/**
	 * The set of properties specified <i>in the process definition</i> (if any)
	 * to be passed along to the targetHandler. If no properties were specified,
	 * this will be null.
	 */
	private Element targetProperties;

	/**
	 * @return Returns the beanName.
	 */
	public String getTargetBean() {
		return targetBean;
	}

	/**
	 * @param targetBean
	 *            The beanName to set.
	 */
	public void setTargetBean(String bean) {
		log.debug("!!!!!!! set targetBean ");
		this.targetBean = bean;
	}

	/**
	 * @return the bean factory key
	 */
	public String getFactoryKey() {
		return factoryKey;
	}

	/**
	 * @param factoryKey
	 *            the bean factory key
	 */
	public void setFactoryKey(String factoryKey) {
		this.factoryKey = factoryKey;
	}

	public Element getTargetProperties() {
		return targetProperties;
	}

	public void setTargetProperties(Element targetProperties) {
		log.debug("!!!!!!! set targetProperties ");
		this.targetProperties = targetProperties;
	}

	/**
	 * A helper class used to forward properties set by the process definition
	 * to the target handler. This class extends
	 * org.jbpm.instantiation.BeanInstantiator to leverage the
	 * setPropertyValue() method (which does most of the work)
	 * 
	 * @see org.jbpm.instantiation.BeanInstantiator
	 */
	private static class PropertySetter extends
			org.jbpm.instantiation.BeanInstantiator {
		private static PropertySetter instance;

		public static void setProperties(Object targetHandler,
				Class targetType, Element targetProperties) {
			if (instance == null)
				instance = new PropertySetter();
			instance.setPropertyValue(targetType, targetHandler,
					"targetProperties", targetProperties);
			/*
			 * Iterator iter = targetProperties.elements().iterator(); while
			 * (iter.hasNext()) { Element propertyElement = (Element)
			 * iter.next(); Attribute nameAttribute =
			 * propertyElement.attribute("name");
			 * logger.debug("!!!!!!! nameAttribute " + nameAttribute.getText());
			 * Attribute typeAttribute = propertyElement.attribute("type");
			 * logger.debug("!!!!!!! typeAttribute " + typeAttribute.getText());
			 * Attribute scopeAttribute = propertyElement.attribute("scope");
			 * logger.debug("!!!!!!! scopeAttribute " +
			 * scopeAttribute.getText()); String propertyName =
			 * propertyElement.getName(); logger.debug("!!!!!!! propertyName " +
			 * propertyName); instance.setPropertyValue(targetType,
			 * targetHandler, nameAttribute.getText(), propertyElement); } //
			 * while
			 */
		}
	}

	/**
	 * Retrieves the bean factory.
	 * 
	 * @return
	 */
	protected BeanFactory retrieveBeanFactory() {
		BeanFactoryLocator factoryLocator = new JbpmFactoryLocator();
		BeanFactoryReference factory = factoryLocator
				.useBeanFactory(factoryKey);
		if (factory == null)
			throw new IllegalArgumentException(
					"no beanFactory found under key=" + factoryKey);
		try {
			return factory.getFactory();
		} finally {
			factory.release();
		}
	}

	/**
	 * Find the beanName inside the Spring container.
	 * 
	 * @return
	 */
	protected Object lookupBean(Class type) {

		BeanFactory beanFactory = retrieveBeanFactory();
		if (targetProperties != null) {
			// There are additional properties passed from the process
			// definition that need to be set
			// in the target handler.
			// 1. Insure what we are getting is NOT a singleton...
			if (beanFactory.isSingleton(getTargetBean()) == true) {
				log.debug("!!!!!!! targetBean is singleton!Throw Exception");
				// This is an error (its not thread safe for one thing!)
				throw new IllegalArgumentException(
						"Target bean "
								+ getTargetBean()
								+ " can not be defined as a singleton if properties are specified.  Set attribute singleton=\"false\".");
			}
			// 2. Create the target handler...
			Object targetHandler = beanFactory.getBean(getTargetBean(), type);
			log.debug("!!!!!!! lookup targetBean " + getTargetBean());
			// 3. Set the rest of the properties as specified in the process
			// definition...
			PropertySetter.setProperties(targetHandler, targetHandler
					.getClass(), targetProperties);

			return targetHandler;
		} else {
			log.debug("!!!!!!! target properties are null ");
			return beanFactory.getBean(getTargetBean(), type);
		}
	}

	/**
	 * @see org.jbpm.graph.node.DecisionHandler#decide(org.jbpm.graph.exe.ExecutionContext)
	 */
	public String decide(ExecutionContext executionContext) throws Exception {
		DecisionHandler handler = (DecisionHandler) lookupBean(DecisionHandler.class);
		if (log.isDebugEnabled())
			log.debug("using Spring-managed decisionHandler=" + handler);
		return handler.decide(executionContext);
	}

	/**
	 * @see org.jbpm.graph.def.ActionHandler#execute(org.jbpm.graph.exe.ExecutionContext)
	 */
	public void execute(ExecutionContext executionContext) throws Exception {
		ActionHandler action = (ActionHandler) lookupBean(ActionHandler.class);
		if (log.isDebugEnabled())
			log.debug("using Spring-managed actionHandler=" + action);
		action.execute(executionContext);
	}

	/**
	 * @see org.jbpm.taskmgmt.def.AssignmentHandler#assign(org.jbpm.taskmgmt.exe.Assignable,
	 *      org.jbpm.graph.exe.ExecutionContext)
	 */
	public void assign(Assignable assignable, ExecutionContext executionContext)
			throws Exception {
		AssignmentHandler handler = (AssignmentHandler) lookupBean(AssignmentHandler.class);
		if (log.isDebugEnabled())
			log.debug("using Spring-managed assignmentHandler=" + handler);
		handler.assign(assignable, executionContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jbpm.taskmgmt.def.TaskControllerHandler#initializeTaskVariables(org
	 * .jbpm.taskmgmt.exe.TaskInstance, org.jbpm.context.exe.ContextInstance,
	 * org.jbpm.graph.exe.Token)
	 */
	public void initializeTaskVariables(TaskInstance taskInstance,
			ContextInstance contextInstance, Token token) {
		TaskControllerHandler handler = (TaskControllerHandler) lookupBean(TaskControllerHandler.class);
		if (log.isDebugEnabled())
			log.debug("using Spring-managed taskControllerHandler=" + handler);
		handler.initializeTaskVariables(taskInstance, contextInstance, token);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jbpm.taskmgmt.def.TaskControllerHandler#submitTaskVariables(org.jbpm
	 * .taskmgmt.exe.TaskInstance, org.jbpm.context.exe.ContextInstance,
	 * org.jbpm.graph.exe.Token)
	 */
	public void submitTaskVariables(TaskInstance taskInstance,
			ContextInstance contextInstance, Token token) {
		TaskControllerHandler handler = (TaskControllerHandler) lookupBean(TaskControllerHandler.class);
		if (log.isDebugEnabled())
			log.debug("using Spring-managed taskControllerHandler=" + handler);
		handler.submitTaskVariables(taskInstance, contextInstance, token);
	}

	public void submitTokenVariables(ExecutionContext context,
			String className, Element targetProperties,
			Map<String, Object> variableMap, Token token)
			throws WorkflowException {

		Iterator<Element> iter = targetProperties.elements().iterator();

		while (iter.hasNext()) {
			String name = null;
			String scope = null;
			String type = null;
			String empty = null;
			String ref = null;
			Object value = null;
			Element propertyElement = iter.next();
			Attribute nameAttribute = propertyElement.attribute("name");
			if (nameAttribute != null)
				name = nameAttribute.getText();
			else
				throw new InvalidVariableNameException(
						className
								+ " does not have 'name' attribute defined inside tag: "
								+ propertyElement.asXML());
			// log.debug("!!!!!!! name: " + name);
			Attribute typeAttribute = propertyElement.attribute("type");
			if (typeAttribute != null)
				type = typeAttribute.getText();
			// log.debug("!!!!!!! type: " + type);
			Attribute scopeAttribute = propertyElement.attribute("scope");
			if (scopeAttribute != null)
				scope = scopeAttribute.getText();
			// log.debug("!!!!!!! scope: " + scope);
			Attribute emptyAttribute = propertyElement.attribute("empty");
			if (emptyAttribute != null)
				empty = emptyAttribute.getText();
			// log.debug("!!!!!!! empty: " + empty);
			Attribute refAttribute = propertyElement.attribute("ref");
			if (refAttribute != null)
				ref = refAttribute.getText();
			// log.debug("!!!!!!! ref: " + ref);
			String propertyValue = propertyElement.getText();
			// log.debug("!!!!!!! propertyValue " + propertyValue);
			if (isOutput(type)) {
				value = variableMap.get(name);

				if (isValid(name, value, empty)) {
					// put into map
					// check ref
					if (ref == null || "".equals(ref)) {
						// log.debug("set variable name: "+ name + " value: " +
						// value);
						if (isLocal(scope)) {
							if (context.getContextInstance().getVariable(name,
									token) == null) {
								context.getContextInstance().createVariable(
										name, value, token);
							} else {
								context.getContextInstance().setVariable(name,
										value, token);
							}

						} else {
							context.getContextInstance().setVariable(name,
									value);
						}
					} else {
						// log.debug("set variable ref: "+ ref + " value: " +
						// value);
						context.getContextInstance().setVariable(ref, value);
					}

				} else {
					throw new InvalidVariableValueException(className
							+ " threw Validation exception: " + name
							+ " must not be empty!");
				}

				// remove entry from map
				variableMap.remove(name);
			} else {
				// log.debug("variable name: "+ name +
				// " is not output variable.");
			}

		} // while

		// now go through map and set remaining variables on process level.
		Iterator<Map.Entry<String, Object>> it = variableMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			if (entry.getKey() != null && entry.getValue() != null) {
				context.getContextInstance().setVariable(entry.getKey(),
						entry.getValue());
			}
		}

	}

	public Map<String, String> fetchAndValidateVariables(
			ExecutionContext context, String className, Element targetProperties)
			throws WorkflowException {
		Map variableMap = new HashMap<String, Object>();
		if(targetProperties!=null){
		Iterator<Element> iter = targetProperties.elements().iterator();

		while (iter.hasNext()) {
			String name = null;
			String scope = null;
			String type = null;
			String empty = null;
			String ref = null;
			Object value = null;
			Element propertyElement = iter.next();
			Attribute nameAttribute = propertyElement.attribute("name");
			if (nameAttribute != null)
				name = nameAttribute.getText();
			else
				throw new InvalidVariableNameException(
						className
								+ " does not have 'name' attribute defined inside tag: "
								+ propertyElement.asXML());
			// log.debug("!!!!!!! name: " + name);
			Attribute typeAttribute = propertyElement.attribute("type");
			if (typeAttribute != null)
				type = typeAttribute.getText();
			// log.debug("!!!!!!! type: " + type);
			Attribute scopeAttribute = propertyElement.attribute("scope");
			if (scopeAttribute != null)
				scope = scopeAttribute.getText();
			// log.debug("!!!!!!! scope: " + scope);
			Attribute emptyAttribute = propertyElement.attribute("empty");
			if (emptyAttribute != null)
				empty = emptyAttribute.getText();
			// log.debug("!!!!!!! empty: " + empty);
			Attribute refAttribute = propertyElement.attribute("ref");
			if (refAttribute != null)
				ref = refAttribute.getText();
			// log.debug("!!!!!!! ref: " + ref);
			String propertyValue = propertyElement.getText();
			// log.debug("!!!!!!! propertyValue " + propertyValue);
			if (isInput(type)) {
				value = getVariableValue(context, name, propertyValue, scope,
						ref);
				// log.debug("name: "+ name + " value: " + value);
				if (isValid(name, value, empty)) {
					// put into map
					variableMap.put(name, value);
				} else {
					throw new InvalidVariableValueException(className
							+ " threw Validation exception: " + name
							+ " must not be empty!");
				}
			}

		} // while
		}
		return variableMap;
	}

	public boolean isValid(String name, Object value, String empty)
			throws InvalidVariableValueException {

		if ("false".equals(empty)
				&& (value == null || "".equals(value.toString()))) {
			return false;
		} else
			return true;
	}

	public boolean isInput(String type) {
		if (type == null || type.startsWith("in")) {
			return true;
		} else
			return false;
	}

	public boolean isOutput(String type) {
		if (type == null || type.endsWith("out")) {
			return true;
		} else
			return false;
	}

	public boolean isLocal(String scope) {
		if (scope == null || scope.equals("global")) {
			return false;
		} else
			return true;
	}

	public Object getVariableValue(ExecutionContext context,
			String propertyName, String propertyValue, String scope, String ref) {
		Object variableValue = null;
		if (propertyValue == null || "".equals(propertyValue)) {
			if (ref == null || "".equals(ref)) {
				if (isLocal(scope)) {
					variableValue = context.getContextInstance().getVariable(
							propertyName, context.getToken());
				} else {
					variableValue = context.getVariable(propertyName);
				}
			} else {
				// log.debug("find variable ref: "+ref);
				variableValue = context.getContextInstance().getVariable(ref,
						context.getToken());
			}
		} else {
			variableValue = propertyValue;
		}

		return variableValue;
	}

}
