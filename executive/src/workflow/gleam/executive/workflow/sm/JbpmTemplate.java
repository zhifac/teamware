/*
 *  JbpmTemplate.java
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
package gleam.executive.workflow.sm;

import gleam.executive.util.DateUtil;
import gleam.executive.workflow.core.JbpmGraphSession;
import gleam.executive.workflow.core.JbpmTaskInstance;
import gleam.executive.workflow.core.JbpmTaskMgmtSession;
import gleam.executive.workflow.model.AnnotationMetricInfo;
import gleam.executive.workflow.model.AnnotationMetricMatrix;
import gleam.executive.workflow.model.AnnotationStatusInfo;
import gleam.executive.workflow.model.TimeMetricInfo;
import gleam.executive.workflow.util.AnnotationUtil;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.file.def.FileDefinition;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.util.StringUtils;
import org.springmodules.workflow.jbpm31.JbpmAccessor;
import org.springmodules.workflow.jbpm31.JbpmCallback;
import org.springmodules.workflow.jbpm31.JbpmUtils;

/**
 * Jbpm 3.1 Template. Requires a jbpmConfiguration and accepts also a
 * hibernateTemplate and processDefinition. Jbpm Persistence Service can be
 * managed by Spring through the given HibernateTemplate, allowing jBPM to work
 * with a user configured session factory and thread-bound session depending on
 * the HibernateTemplate settings. However, due to the nature of jBPM
 * architecture, on each execute the jbpmContext will try to close the user
 * Hibernate session which is undesireable when working with a thread-bound
 * session or a transaction. One can overcome this undesired behavior by setting
 * 'exposeNative' property on the HibernateTemplate to false (default).
 * 
 * @see org.springframework.orm.hibernate3.HibernateTemplate
 * @author Costin Leau
 * @author Milan Agatonovic
 * 
 */
public class JbpmTemplate extends JbpmAccessor implements JbpmOperations {

	protected final Log log = LogFactory.getLog(getClass());

	// TODO: persistence is not always required
	/**
	 * Optional process definition.
	 */
	private ProcessDefinition processDefinition;

	/**
	 * Required if jBPM has persistence services.
	 */
	private HibernateTemplate hibernateTemplate;

	/**
	 * Boolean used to determine if the persistence service is used or not. If
	 * so, hibernateTemplate will be required and used internally.
	 */
	private boolean hasPersistenceService;

	/**
	 * jBPM context name. defaults to null which is equivalent to
	 * JbpmContext.DEFAULT_JBPM_CONTEXT_NAME
	 */
	private String contextName = JbpmContext.DEFAULT_JBPM_CONTEXT_NAME;

	public JbpmTemplate() {
	}

	public JbpmTemplate(JbpmConfiguration jbpmConfiguration) {
		this.jbpmConfiguration = jbpmConfiguration;
	}

	public JbpmTemplate(JbpmConfiguration jbpmConfiguration,
			ProcessDefinition processDefinition) {
		this.jbpmConfiguration = jbpmConfiguration;
		this.processDefinition = processDefinition;
	}

	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		// see if persistence is required
		// we don't have any other way to get the services then by creating
		// a
		// jbpm context
		// secured in try/finally block
		JbpmContext dummy = getContext();
		try {
			if (JbpmUtils.hasPersistenceService(dummy)) {
				hasPersistenceService = true;
				logger.debug("jBPM persistence service present");
			}
			if (hibernateTemplate != null)
				logger
						.debug("hibernateTemplate present - jBPM persistence service will be managed by Spring");
			else {
				if (dummy.getSessionFactory() != null) {
					logger
							.debug("creating hibernateTemplate based on jBPM SessionFactory");
					hibernateTemplate = new HibernateTemplate(dummy
							.getSessionFactory());
					logger.debug("CHECK exposeNativeSession "
							+ hibernateTemplate.isExposeNativeSession());
				} else
					logger
							.debug("hibernateTemplate missing - jBPM will handle its own persistence");
			}
		} finally {
			dummy.close();
			logger.debug("jBPM configured");
		}
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#suspendProcessInstance(long)
	 */
	public void cancelProcessInstance(final long processInstanceId) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessInstance processInstance = context
						.getProcessInstance(processInstanceId);
				// Delete process instance with associated tasks, timers and
				// messages
				if (processInstance != null) {
					((JbpmGraphSession) context.getGraphSession())
							.deleteProcessInstance(processInstance, true, true);
				}
				return null;
			}
		});
	}
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#endProcessInstance(long)
	 */
	public void endProcessInstance(final long processInstanceId) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessInstance processInstance = context.getProcessInstanceForUpdate(processInstanceId);
				
				if (processInstance != null && !processInstance.hasEnded()) {
					// suspend all sub processes:
					List<ProcessInstance> subProcessInstances = ((JbpmGraphSession) context
							.getGraphSession())
							.findAllSubProcessInstances(processInstance);
					Iterator<ProcessInstance> it = subProcessInstances
							.iterator();
					while (it.hasNext()) {
						ProcessInstance pi = it.next();
						if (pi != null && !pi.hasEnded()) {
							log.debug("end subprocess instance: "
									+ pi.getId());
							pi.getTaskMgmtInstance().endAll();
							pi.end();
						}
					}
					processInstance.end();
					log.debug("end process instance: "
							+ processInstance.getId());

				}
				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#cancelTaskInstance(long)
	 */
	public void cancelTaskInstance(final long taskInstanceId) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				TaskInstance taskInstance = context
						.getTaskInstance(taskInstanceId);
				if (taskInstance != null)
					taskInstance.cancel();
				return null;
			}
		});
	}
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#endTaskInstance(long)
	 */
	public void endTaskInstance(final long taskInstanceId) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				JbpmTaskInstance taskInstance = (JbpmTaskInstance)context.getTaskInstance(taskInstanceId);
				log.debug("ending task: " + taskInstance.getId());
				String statusVariableName = WorkflowUtil
				.createAnnotationStatusVariableName(taskInstance
						.getProcessInstance().getProcessDefinition()
						.getName(), taskInstance.getProcessInstance()
						.getId(), taskInstance.getDocumentId());
		if (statusVariableName != null) {
			log.debug("look for variable: " + statusVariableName);
			AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) taskInstance
					.getContextInstance().getVariable(statusVariableName);
			if (annotationStatusInfo != null && !taskInstance.hasEnded()) {
				annotationStatusInfo = AnnotationUtil.markDocumentAsAborted(annotationStatusInfo);
				annotationStatusInfo.setTimeWorkedOn(taskInstance.getTimeWorkedOn());
				taskInstance.getContextInstance().setVariable(statusVariableName, annotationStatusInfo);
			}
		}
				if (taskInstance != null)
					taskInstance.end();
				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#acceptTaskInstance(long)
	 */
	public void acceptTaskInstance(final long taskInstanceId,
			final String actorId) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				TaskInstance taskInstance = context
						.getTaskInstance(taskInstanceId);
				if (taskInstance != null)
					taskInstance.setActorId(actorId);
				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#createProcessInstance(long)
	 */
	public ProcessInstance createProcessInstance(final long definitionId) {
		return (ProcessInstance) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessInstance processInstance = null;
				ProcessDefinition processDef = context.getGraphSession()
						.getProcessDefinition(definitionId);
				if (processDef != null) {
					processInstance = (ProcessInstance) processDef
							.createInstance();
					if (processInstance != null) {
						context.save(processInstance);
					}
				}
				return processInstance;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#createProcessInstance(String)
	 */
	public ProcessInstance createProcessInstance(final String processName) {
		return (ProcessInstance) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.newProcessInstance(processName);
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#createProcessInstance(long,
	 *      java.util.Map, String)
	 */

	public ProcessInstance createProcessInstance(final long definitionId,
			final Map variableMap, final String key) {
		return (ProcessInstance) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessInstance processInstance = null;
				ProcessDefinition processDef = context.getGraphSession()
						.getProcessDefinition(definitionId);

				if (processDef != null) {
					log.debug("processDef " + processDef.getId());
					if (key == null) {
						String defaultKey = processDef.getName();
						processInstance = (ProcessInstance) processDef
								.createProcessInstance(variableMap, defaultKey);

					} else {
						processInstance = (ProcessInstance) processDef
								.createProcessInstance(variableMap, key);
					}

				}
				return processInstance;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#createStartProcessInstance(long,
	 *      java.util.Map, String)
	 */

	public ProcessInstance createStartProcessInstance(final long definitionId,
			final Map variableMap, final String key) {
		return (ProcessInstance) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessInstance processInstance = null;
				ProcessDefinition processDef = context.getGraphSession()
						.getProcessDefinition(definitionId);

				log.debug("canCancel before starting process: "+ variableMap.get(JPDLConstants.CAN_CANCEL));
				
				if (processDef != null) {
					log.debug("processDef " + processDef.getId());
					if (key == null) {
						String defaultKey = processDef.getName();
						processInstance = (ProcessInstance) processDef
								.createProcessInstance(variableMap, defaultKey);

					} else {
						processInstance = (ProcessInstance) processDef
								.createProcessInstance(variableMap, key);
					}

					if (processInstance != null) {
						if (processInstance.getTaskMgmtInstance()
								.getTaskMgmtDefinition().getStartTask() != null) {
							log.debug("Created StartTask Instance");
							processInstance.getTaskMgmtInstance()
									.createStartTaskInstance();
						} else {
							processInstance.getRootToken().signal();
							log.debug("Signaled ProcessInstance");
						}

						context.save(processInstance);
					} else {
						log.debug("ProcessInstance is null");
					}
				}
				return processInstance;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#startProcessInstance(org.jbpm.graph.exe.ProcessInstance)
	 */

	public ProcessInstance startProcessInstance(final ProcessInstance processInstance) {
		return (ProcessInstance) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				
					if (processInstance != null) {
						if (processInstance.getTaskMgmtInstance()
								.getTaskMgmtDefinition().getStartTask() != null) {
							log.debug("Created StartTask Instance");
							processInstance.getTaskMgmtInstance()
									.createStartTaskInstance();
						} else {
							processInstance.getRootToken().signal();
							log.debug("Signaled ProcessInstance");
						}

						context.save(processInstance);
					} else {
						log.debug("ProcessInstance is null");
					}
				return processInstance;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#createStartProcessInstance(long)
	 */
	public ProcessInstance createStartProcessInstance(final long definitionId) {
		return createStartProcessInstance(definitionId, null, null);
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#definitionExists(String)
	 */
	public Boolean definitionExists(final String definitionName) {
		return (Boolean) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessDefinition processDefinition = findLatestProcessDefinition(definitionName);
				if (processDefinition == null) {
					return Boolean.FALSE;
				}
				return Boolean.TRUE;
			}
		});
	}

	// added methods
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#deployProcessDefinition(org.jbpm.graph.def.ProcessDefinition)
	 */
	public ProcessDefinition deployProcessDefinition(
			final ProcessDefinition newProcessDefinition) {
		this.processDefinition = (ProcessDefinition) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				context.deployProcessDefinition(newProcessDefinition);
				return newProcessDefinition;
			}
		});
		return this.processDefinition;
	}

	/**
	 * @throws IOException 
	 * @see gleam.executive.workflow.sm.JbpmOperations#deployProcessFromArchive(java.io.InputStream)
	 */
	public void deployProcessFromArchive(InputStream inputStream) throws IOException {
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		ProcessDefinition processDefinition = ProcessDefinition
				.parseParZipInputStream(zipInputStream);
		deployProcessDefinition(processDefinition);
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#deployProcessFromArchive(String)
	 */
	public ProcessDefinition deployProcessFromArchive(String filePath) {
		ProcessDefinition processDefinition = null;
		try {
			ZipInputStream zipInputStream = new ZipInputStream(
					new FileInputStream(filePath));
			processDefinition = ProcessDefinition
					.parseParZipInputStream(zipInputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return deployProcessDefinition(processDefinition);
	}

	/**
	 * Execute the action specified by the given action object within a
	 * JbpmSession.
	 * 
	 * @param callback
	 * @return
	 */
	public Object execute(final JbpmCallback callback) {
		final JbpmContext context = getContext();
		try {
			// use the hibernateTemplate is present and if needed
			if (hibernateTemplate != null && hasPersistenceService) {
				// use hibernate template
				return hibernateTemplate.execute(new HibernateCallback() {
					/**
					 * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
					 */
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						// inject the session in the context
						context.setSession(session);
						return callback.doInJbpm(context);
					}
				});
			}
			// plain callback invocation (no template w/ persistence)
			return callback.doInJbpm(context);
		} catch (JbpmException ex) {
			throw convertJbpmException(ex);
		} finally {
			releaseContext(context);
		}
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllPooledTaskInstances(String)
	 */
	public List findAllPooledTaskInstances(final String actorId) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return ((JbpmTaskMgmtSession) context.getTaskMgmtSession())
						.findAllPooledTaskInstances(actorId);
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllProcessInstances()
	 */
	public List findAllProcessInstances() {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return ((JbpmGraphSession) context.getGraphSession())
						.findAllProcessInstances();
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllProcessInstancesExcludingSubProcessInstances()
	 */
	public List findAllProcessInstancesExcludingSubProcessInstances() {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return ((JbpmGraphSession) context.getGraphSession())
						.findAllProcessInstancesExcludingSubProcessInstances();
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findProcessInstancesExcludingSubProcessInstancesByKeyAndName(java.lang.String)
	 */
	public List findProcessInstancesExcludingSubProcessInstancesByKeyAndName(
			final String key, final String name) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return ((JbpmGraphSession) context.getGraphSession())
						.findProcessInstancesExcludingSubProcessInstancesByKeyAndName(
								key, name);
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllProcessInstancesByProcessDefinitionIdExcludingSubProcessInstances(long)
	 */
	public List findAllProcessInstancesByProcessDefinitionIdExcludingSubProcessInstances(
			final long processDefinitionId) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return ((JbpmGraphSession) context.getGraphSession())
						.findAllProcessInstancesByProcessDefinitionIdExcludingSubProcessInstances(processDefinitionId);
			}
		});
	}
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllProcessInstancesByProcessDefinitionNameExcludingSubProcessInstances(long)
	 */
	public List findAllProcessInstancesByProcessDefinitionNameExcludingSubProcessInstances(
			final String processDefinitionName) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return ((JbpmGraphSession) context.getGraphSession())
						.findAllProcessInstancesByProcessDefinitionNameExcludingSubProcessInstances(processDefinitionName);
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllSubProcessInstances(org.jbpm.graph.exe.ProcessInstance)
	 */
	public List findAllSubProcessInstances(final ProcessInstance processInstance) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return ((JbpmGraphSession) context.getGraphSession())
						.findAllSubProcessInstances(processInstance);
			}
		});
	}

	public void addCommentToRootToken(final ProcessInstance processInstance,
			final String comment) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				if (processInstance != null) {
					processInstance.getRootToken().addComment(comment);
				}
				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllProcessInstances(long)
	 */
	public List findAllProcessInstances(final long definitionId) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getGraphSession().findProcessInstances(
						definitionId);
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllTaskInstances(String)
	 */
	public List findAllTaskInstances(final String actorId) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return ((JbpmTaskMgmtSession) context.getTaskMgmtSession())
						.findAllTaskInstances(actorId);
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findTaskInstancesForProcessInstance(long)
	 */
	public List findTaskInstancesForProcessInstance(final long processInstanceId) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessInstance processInstance = context.getGraphSession()
						.getProcessInstance(processInstanceId);
				return ((JbpmTaskMgmtSession) context.getTaskMgmtSession())
						.findTaskInstancesForProcessInstance(processInstance);
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findProcessDefinition(long)
	 */
	public FileDefinition findFileDefinition(final long processDefinitionId) {
		return (FileDefinition) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessDefinition processDefinition = context.getGraphSession()
						.getProcessDefinition(processDefinitionId);
				return processDefinition.getFileDefinition();
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findLatestProcessDefinition(String)
	 */
	public ProcessDefinition findLatestProcessDefinition(
			final String processName) {
		return (ProcessDefinition) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getGraphSession().findLatestProcessDefinition(
						processName);
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findPendingPooledAnnotationTaskInstances(String)
	 */
	public List findPendingPooledAnnotationTaskInstances(final String actorId) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return ((JbpmTaskMgmtSession) context.getTaskMgmtSession())
						.findPendingPooledAnnotationTaskInstancesByActorId(actorId);
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findPooledTaskInstances(java.util.List)
	 */
	public List findPooledTaskInstances(final List actorIds) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getTaskMgmtSession().findPooledTaskInstances(
						actorIds);
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findPooledTaskInstances(java.lang.String)
	 */
	public List findPooledTaskInstances(final String actorId) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getTaskMgmtSession().findPooledTaskInstances(
						actorId);
			}
		});
	}
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findProcessDefinition(long)
	 */
	public ProcessDefinition findProcessDefinition(final long definitionId) {
		return (ProcessDefinition) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getGraphSession().getProcessDefinition(
						definitionId);
			}
		});
	}
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findProcessDefinitionByNameAndVersion(String, Integer)
	 */
	public ProcessDefinition findProcessDefinitionByNameAndVersion(final String processDefinitionName, final Integer version) {
		return (ProcessDefinition) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getGraphSession().findProcessDefinition(
						processDefinitionName, version);
			}
		});
	}
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findProcessDefinitionByName(String)
	 */
	public List findProcessDefinitionsByName(final String processDefinitionName) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getGraphSession().findAllProcessDefinitionVersions(
						processDefinitionName);
			}
		});
	}
	
	
	
	

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#loadProcessDefinition(long)
	 */
	public ProcessDefinition loadProcessDefinition(final long definitionId) {
		return (ProcessDefinition) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getGraphSession().loadProcessDefinition(
						definitionId);
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findProcessDefinitionByProcessInstanceId(Long)
	 */
	public ProcessDefinition findProcessDefinitionByProcessInstanceId(
			final Long processInstanceId) {
		return (ProcessDefinition) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessDefinition processDefinition = null;
				ProcessInstance processInstance = context
						.getProcessInstance(processInstanceId.longValue());
				if (processInstance != null)
					processDefinition = processInstance.getProcessDefinition();
				return processDefinition;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findProcessDefinitionByTaskId(long)
	 */
	public ProcessDefinition findProcessDefinitionByTaskInstanceId(
			final long taskId) {
		return (ProcessDefinition) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessDefinition processDefinition = null;
				TaskInstance taskInstance = context.getTaskInstance(taskId);
				if (taskInstance != null) {
					processDefinition = taskInstance.getToken()
							.getProcessInstance().getProcessDefinition();
				}
				return processDefinition;
			}
		});
	}

	// end of added methods
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findProcessInstance(java.lang.Long)
	 */
	public ProcessInstance findProcessInstance(final Long processInstanceId) {
		return (ProcessInstance) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getGraphSession().getProcessInstance(
						processInstanceId.longValue());
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findProcessInstances()
	 */
	public List findProcessInstances() {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getGraphSession().findProcessInstances(
						processDefinition.getId());
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findStartableProcessDefinitions()
	 */
	public List findStartableProcessDefinitions() {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getGraphSession().findAllProcessDefinitions();
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findSwimlaneNames(Long)
	 */
	public String[] findSwimlaneNames(final Long taskId) {
		return (String[]) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				String[] swimlanes = null;
				ProcessDefinition processDefinition = null;
				TaskInstance taskInstance = context.getTaskInstance(taskId
						.longValue());
				if (taskInstance != null) {
					processDefinition = taskInstance.getToken()
							.getProcessInstance().getProcessDefinition();
					Map swimlanesMap = processDefinition
							.getTaskMgmtDefinition().getSwimlanes();
					swimlanes = (String[]) swimlanesMap.keySet().toArray(
							new String[swimlanesMap.size()]);
				}
				return swimlanes;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findSwimlanes(Long)
	 */
	public Swimlane[] findSwimlanes(final Long instanceId) {
		return (Swimlane[]) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				Swimlane[] swimlanes = null;
				ProcessDefinition processDefinition = null;
				ProcessInstance processInstance = context
						.getProcessInstance(instanceId.longValue());
				if (processInstance != null) {
					processDefinition = processInstance.getProcessDefinition();
					Map swimlanesMap = processDefinition
							.getTaskMgmtDefinition().getSwimlanes();
					if (swimlanesMap != null && swimlanesMap.size() != 0) {
						swimlanes = (Swimlane[]) swimlanesMap.keySet().toArray(
								new Swimlane[swimlanesMap.size()]);
					}
				}
				return swimlanes;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findTaskInstances(java.util.List)
	 */
	public List findTaskInstances(final List actorIds) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getTaskMgmtSession().findTaskInstances(actorIds);
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findPendingTaskInstancesByActorId(java.lang.String)
	 */

	public List findPendingTaskInstancesByActorId(final String actorId) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return ((JbpmTaskMgmtSession) context.getTaskMgmtSession())
						.findPendingTaskInstancesByActorId(actorId);
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findTaskInstances(java.lang.String[])
	 */
	public List findTaskInstances(final String[] actorIds) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getTaskMgmtSession().findTaskInstances(actorIds);
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findTaskInstancesByToken(long)
	 */
	public List findTaskInstancesByToken(final long tokenId) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getTaskMgmtSession().findTaskInstancesByToken(
						tokenId);
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findTaskInstancesByToken(org.jbpm.graph.exe.Token)
	 */
	public List findTaskInstancesByToken(Token token) {
		return findTaskInstancesByToken(token.getId());
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findTasksByProcessDefinitionId(long)
	 */
	public Map findTasksByProcessDefinitionId(final long definitionId) {
		return (Map) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				Map tasks = null;
				ProcessDefinition processDef = context.getGraphSession()
						.getProcessDefinition(definitionId);
				if (processDef != null) {
					tasks = processDef.getTaskMgmtDefinition().getTasks();
					Task startTask = processDef.getTaskMgmtDefinition()
							.getStartTask();
					if (startTask != null) {
						tasks.put(startTask.getName(), startTask);
					}
				}
				return tasks;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findVariable(Long,
	 *      String)
	 */
	public Object findVariable(final Long taskId, final String variableName) {
		return (Object) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				Object result = null;
				TaskInstance taskInstance = context.getTaskInstance(taskId
						.longValue());

				if (taskInstance != null)
					result = taskInstance.getVariable(variableName);
				return result;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findVariableAccesses(Long)
	 */
	public List findVariableAccesses(final Long taskInstanceId) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				log.debug("############# JT:findVariableAccesses");
				List variableAccesses = null;
				TaskInstance taskInstance = context
						.getTaskInstance(taskInstanceId.longValue());
				if (taskInstance != null) {
					log.debug("JT:taskInstance: " + taskInstance.toString());
					TaskController taskController = taskInstance.getTask()
							.getTaskController();
					if (taskController != null) {
						log.debug("JT:taskController: "
								+ taskController.toString());
						variableAccesses = taskController.getVariableAccesses();
						if (variableAccesses != null) {
							log.debug("JT:variableAccesses: "
									+ variableAccesses.toString());
						} else {
							log.debug("JT:variableAccesses: NULL");
						}
					} else {
						log.debug("JT:taskController: NULL");
					}
				} else {
					log.debug("JT:taskInstance: NULL");
				}
				return variableAccesses;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findVariableNames(Long)
	 */
	public String[] findVariableNames(final Long taskId) {
		return (String[]) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				String[] result = null;
				TaskInstance taskInstance = context.getTaskInstance(taskId
						.longValue());
				if (taskInstance != null) {
					Map variables = taskInstance.getVariableInstances();
					if (variables != null && variables.size() != 0)
						result = (String[]) variables.keySet().toArray(
								new String[variables.size()]);
				}
				return result;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findVariables(Long)
	 */
	public Map findVariables(final TaskInstance taskInstance) {
		return (Map) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return taskInstance.getVariableInstances();
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#getAvailableTransitions(Long)
	 */
	public List getAvailableTransitions(final Long taskInstanceId) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				TaskInstance taskInstance = context
						.getTaskInstance(taskInstanceId.longValue());
				List transitions = null;
				if (taskInstance != null)
					transitions = taskInstance.getAvailableTransitions();
				return transitions;
			}
		});
	}

	/**
	 * Hook for subclasses for adding custom behavior.
	 * 
	 * @return created of fetched from the thread jbpm context.
	 */
	protected JbpmContext getContext() {
		JbpmContext context = jbpmConfiguration.createJbpmContext(contextName);
		return context;
	}

	/**
	 * @return Returns the contextName.
	 */
	public String getContextName() {
		return contextName;
	}

	/**
	 * @return Returns the hibernateTemplate.
	 */
	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	// ******************************************************************************************
	// *************************** PROCESS DEFINITIONS
	// *****************************************
	// ******************************************************************************************
	/**
	 * @return Returns the processDefinition.
	 */
	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#getProcessInstance(long)
	 */
	public ProcessInstance getProcessInstance(final long processInstanceId) {
		return (ProcessInstance) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getProcessInstance(processInstanceId);
			}
		});
	}
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#getProcessInstanceForUpdate(long)
	 */
	public ProcessInstance getProcessInstanceForUpdate(final long processInstanceId) {
		return (ProcessInstance) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getProcessInstanceForUpdate(processInstanceId);
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#getTaskInstance(Long)
	 */
	public TaskInstance getTaskInstance(final Long taskId) {
		return (TaskInstance) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getTaskInstance(taskId.longValue());
			}
		});
	}

	/**
	 * Hook for subclasses for adding custom behavior.
	 * 
	 * @param jbpmContext
	 */
	protected void releaseContext(JbpmContext jbpmContext) {
		jbpmContext.close();
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#resumeProcessInstance(long)
	 */
	public void resumeProcessInstance(final long processInstanceId) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessInstance processInstance = context
						.getProcessInstance(processInstanceId);

				if (processInstance != null && !processInstance.hasEnded()) {
					processInstance.resume();
					log.debug("resumed process instance: "
							+ processInstance.getId());
					List<ProcessInstance> subProcessInstances = ((JbpmGraphSession) context
							.getGraphSession())
							.findAllSubProcessInstances(processInstance);
					Iterator<ProcessInstance> it = subProcessInstances
							.iterator();
					while (it.hasNext()) {
						ProcessInstance pi = it.next();
						if (pi != null && !pi.hasEnded()) {
							pi.resume();
							log
									.debug("resumed process instance: "
											+ pi.getId());
						}
					}
				}
				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#resumeTaskInstance(long)
	 */
	public void resumeTaskInstance(final long taskInstanceId) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				TaskInstance taskInstance = context
						.getTaskInstance(taskInstanceId);
				if (taskInstance != null)
					taskInstance.resume();
				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#saveOrUpdateTask(org.jbpm.taskmgmt.def.Task)
	 */
	public void saveOrUpdateTask(final Task task) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				((JbpmGraphSession) context.getGraphSession())
						.saveOrUpdateTask(task);
				return null;
			}
		});
	}
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#updateStatus(long)
	 */
	public void updateStatus(final long taskInstanceId) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				JbpmTaskInstance taskInstance = (JbpmTaskInstance) context.getTaskInstance(taskInstanceId);
				String statusVariableName = WorkflowUtil.createAnnotationStatusVariableName(
						taskInstance.getProcessInstance().getProcessDefinition().getName(), 
						taskInstance.getProcessInstance().getId(), taskInstance.getDocumentId());
				if (statusVariableName != null) {
					log.debug("look for variable: " + statusVariableName);
					AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) taskInstance
							.getContextInstance().getVariable(statusVariableName);
					annotationStatusInfo.setTimeWorkedOn(taskInstance.getTimeWorkedOn());
					if (annotationStatusInfo != null && !taskInstance.hasEnded()) {
						taskInstance.getContextInstance().setVariable(statusVariableName, annotationStatusInfo);
					}
				}
				context.save(taskInstance);
				return taskInstance;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#saveOrUpdateSwimalne(org.jbpm.taskmgmt.def.Swimlane)
	 */
	public void saveOrUpdateSwimlane(final Swimlane swimlane) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				((JbpmGraphSession) context.getGraphSession())
						.saveOrUpdateSwimlane(swimlane);
				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#saveProcessInstance(org.jbpm.graph.exe.ProcessInstance)
	 */
	public Long saveProcessInstance(final ProcessInstance processInstance) {
		return (Long) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				context.save(processInstance);
				return new Long(processInstance.getId());
			}
		});
	}

	/**
	 * @param contextName
	 *            The contextName to set.
	 */
	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	/**
	 * @param hibernateTemplate
	 *            The hibernateTemplate to set.
	 */
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	/**
	 * @param processDefinition
	 *            The processDefinition to set.
	 */
	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#signal(org.jbpm.graph.exe.ProcessInstance)
	 */
	public void signal(final ProcessInstance processInstance) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				processInstance.signal();
				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#signal(org.jbpm.graph.exe.ProcessInstance,
	 *      java.lang.String)
	 */
	public void signal(final ProcessInstance processInstance,
			final String transitionId) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				processInstance.signal(transitionId);
				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#signal(org.jbpm.graph.exe.ProcessInstance,
	 *      org.jbpm.graph.def.Transition)
	 */
	public void signal(final ProcessInstance processInstance,
			final Transition transition) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				processInstance.signal(transition);
				return null;
			}
		});
		throw new UnsupportedOperationException();
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#signalToken(org.jbpm.graph.exe.ProcessInstance,
	 *      String),
	 */
	public void signalToken(final ProcessInstance processInstance,
			final String tokenName) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				Token token = processInstance.getRootToken().findToken(
						tokenName);
				if (token == null) {
					processInstance.signal();
				} else {
					token.signal();
				}
				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#signalToken(org.jbpm.graph.exe.ProcessInstance,
	 *      String, String),
	 */
	public void signalToken(final ProcessInstance processInstance,
			final String tokenName, final String transitionId) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				Token token = processInstance.getRootToken().findToken(
						tokenName);
				if (token == null) {
					processInstance.signal(transitionId);
				} else {
					token.signal(transitionId);
				}
				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#startProcessInstance(long)
	 */
	public ProcessInstance startProcessInstance(final long processInstanceId) {
		return (ProcessInstance) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				// create a new process instance to run
				ProcessInstance processInstance = context
						.getProcessInstance(processInstanceId);
				// create a new taskinstance for the start task
				if (processInstance.getTaskMgmtInstance()
						.getTaskMgmtDefinition().getStartTask() != null) {
					processInstance.getTaskMgmtInstance()
							.createStartTaskInstance();
				} else {
					processInstance.getRootToken().signal();
				}
				// Save the process instance along with the task instance
				context.save(processInstance);
				return processInstance;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#startProcessInstance(String)
	 */
	public ProcessInstance startProcessInstance(final String definitionName) {
		return (ProcessInstance) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessInstance processInstance = null;
				ProcessDefinition processDef = findLatestProcessDefinition(definitionName);
				if (processDef != null) {
					processInstance = (ProcessInstance) processDef
							.createInstance();
					if (processInstance != null)
						context.save(processInstance);
				}
				return processInstance;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#suspendProcessInstance(long)
	 */
	public void suspendProcessInstance(final long processInstanceId) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessInstance processInstance = context
						.getProcessInstance(processInstanceId);
				if (processInstance != null && !processInstance.hasEnded()) {
					// suspend all sub processes:
					List<ProcessInstance> subProcessInstances = ((JbpmGraphSession) context
							.getGraphSession())
							.findAllSubProcessInstances(processInstance);
					Iterator<ProcessInstance> it = subProcessInstances
							.iterator();
					while (it.hasNext()) {
						ProcessInstance pi = it.next();
						if (pi != null && !pi.hasEnded()) {
							log.debug("suspended process instance: "
									+ pi.getId());
							pi.suspend();
						}
					}
					processInstance.suspend();
					log.debug("suspended process instance: "
							+ processInstance.getId());

				}
				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#suspendTaskInstance(long)
	 */
	public void startTaskInstance(final long taskInstanceId) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				TaskInstance taskInstance = context
						.getTaskInstance(taskInstanceId);
				if (taskInstance != null) {
					if (taskInstance.getStart() == null) {
						taskInstance.start();
					} else {
						log.error("########### TASK ALREADY STARTED!");
					}
				}
				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#suspendTaskInstance(long)
	 */
	public void suspendTaskInstance(final long taskInstanceId) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				TaskInstance taskInstance = context
						.getTaskInstance(taskInstanceId);
				if (taskInstance != null)
					taskInstance.suspend();
				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#undeployProcessDefinition(long)
	 */
	public void undeployProcessDefinition(final long definitionId) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessDefinition processDef = context.getGraphSession()
						.getProcessDefinition(definitionId);
				context.getGraphSession().deleteProcessDefinition(processDef);
				return null;
			}
		});
	}

	public void undeployProcessDefinition(final ProcessDefinition definition) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				context.getGraphSession().deleteProcessDefinition(definition);
				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#executeCallback(long,
	 *      String, String),
	 */
	public void executeCallbackAndSignalToken(final long tokenId,
			final String error) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				// get token for update
				log.debug("tokenId: " + tokenId);
				Token token = context.getToken(tokenId);
				if ("".equals(error)) {
					token.signal();

				} else {
					log.error("Callback failed: " + error);
					token.signal(JPDLConstants.TRANSITION_ERROR);
				}

				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#executeCallback(long,
	 *      String),
	 */
	public void executeCallbackAndEndTask(final long taskInstanceId,
			final String error) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				// get token for update
				log.debug("@@@@taskInstanceId: " + taskInstanceId);
				JbpmTaskInstance taskInstance = null;
				taskInstance = (JbpmTaskInstance) context.getTaskInstanceForUpdate(taskInstanceId);
				String statusVariableName = WorkflowUtil.createAnnotationStatusVariableName(
						taskInstance.getProcessInstance().getProcessDefinition().getName(), 
						taskInstance.getProcessInstance().getId(), 
						taskInstance.getDocumentId()
				);
				// log.debug("look for variable: " + statusVariableName);
				AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) 
					taskInstance.getContextInstance().getVariable(statusVariableName);
				annotationStatusInfo.setTimeWorkedOn(taskInstance.getTimeWorkedOn());

				if ("".equals(error)) {
					AnnotationUtil.markDocumentAsAnnotated(taskInstance.getActorId(), annotationStatusInfo);
					taskInstance.getContextInstance().setVariable(statusVariableName, annotationStatusInfo);
					taskInstance.end();

				} else {
					AnnotationUtil.markDocumentAsFailed(error, annotationStatusInfo);
					taskInstance.getContextInstance().setVariable(statusVariableName, annotationStatusInfo);
					taskInstance.end(JPDLConstants.TRANSITION_ERROR);
				}

				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#cancelTaskInstance(long,
	 *      java.lang.String)
	 */
	public void cancelTaskInstance(final long taskInstanceId,
			final String transitionName) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				JbpmTaskInstance taskInstance = (JbpmTaskInstance) context
						.getTaskInstance(taskInstanceId);
				List transitions = taskInstance.getAvailableTransitions();

				if (taskInstance != null) {

					if (isTransitionAvailable(transitions, transitionName)) {
						taskInstance.cancel(transitionName);
					} else {
						taskInstance.cancel();
					}
				}

				return null;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#rejectTaskInstance(long)
	 */
	public void rejectTaskInstance(final long taskInstanceId) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				JbpmTaskInstance taskInstance = (JbpmTaskInstance) context
						.getTaskInstance(taskInstanceId);

				if (taskInstance != null) {
					// return task to the pool
					String actor = taskInstance.getActorId();
					taskInstance.setActorId(null, false);
					// reset start date
					taskInstance.setStart(null);
					if (taskInstance.getDocumentId() != null) {
						String statusVariableName = WorkflowUtil
								.createAnnotationStatusVariableName(
										taskInstance.getProcessInstance()
												.getProcessDefinition()
												.getName(), taskInstance
												.getProcessInstance().getId(),
										taskInstance.getDocumentId());
						log.debug("look for variable: " + statusVariableName);
						AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) taskInstance
								.getContextInstance().getVariable(statusVariableName);
						annotationStatusInfo.setTimeWorkedOn(taskInstance.getTimeWorkedOn());
						
						int poolSize = taskInstance.getPooledActors().size();

						annotationStatusInfo = AnnotationUtil.markDocumentAsRejected(
								actor, annotationStatusInfo, poolSize);

						log.debug("rejected list: " + annotationStatusInfo.getRejectedByList());
						taskInstance.getContextInstance().setVariable(statusVariableName, annotationStatusInfo);
					}
					log.debug("document marked as rejected. authorId: "
							+ taskInstance.getActorId());
				}

				return null;
			}
		});
	}

	/**
	 * Return true if the given transition name belongs to any of the
	 * transitions in list
	 * 
	 * @param transitions
	 *            - available transitions
	 * @param transitionName
	 *            - searched name
	 * @return true or false
	 */
	private static boolean isTransitionAvailable(List<Transition> transitions,
			String transitionName) {
		boolean flag = false;
		if (transitionName != null) {
			Iterator<Transition> it = transitions.iterator();
			while (it.hasNext()) {
				Transition transition = it.next();
				if (transition.getName().equals(transitionName)) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#completeTask(long,
	 *      java.util.Map, String)
	 */
	public TaskInstance completeTask(final long taskInstanceId,
			final Map variables, final String transition) {
		return (TaskInstance) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				JbpmTaskInstance taskInstance = (JbpmTaskInstance) context
						.getTaskInstance(taskInstanceId);

				if (variables != null && variables.size() != 0) {
					Set keys = variables.keySet();
					Iterator it = keys.iterator();
					while (it.hasNext()) {
						String key = (String) it.next();
						if (variables.get(key) != null)
							taskInstance.setVariable(key, variables.get(key));
					}
				}
				context.save(taskInstance);

				String statusVariableName = WorkflowUtil
						.createAnnotationStatusVariableName(taskInstance
								.getProcessInstance().getProcessDefinition()
								.getName(), taskInstance.getProcessInstance()
								.getId(), taskInstance.getDocumentId());
				if (statusVariableName != null) {
					log.debug("look for variable: " + statusVariableName);
					AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) taskInstance
							.getContextInstance().getVariable(statusVariableName);
					annotationStatusInfo.setTimeWorkedOn(taskInstance.getTimeWorkedOn());
					if (annotationStatusInfo != null && !taskInstance.hasEnded()) {
						annotationStatusInfo = AnnotationUtil
								.markDocumentAsAnnotated(taskInstance
										.getActorId(), annotationStatusInfo);
						taskInstance.getContextInstance().setVariable(
								statusVariableName, annotationStatusInfo);
					}
				}
				// end task instance
				if (transition != null && !transition.equals(""))
					taskInstance.end(transition);
				else
					taskInstance.end();
				context.save(taskInstance);
				return taskInstance;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#getNextTask(java.lang.String)
	 */
	public TaskInstance getNextTask(final String actorId) {
		return (TaskInstance) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				JbpmTaskInstance taskInstance = (JbpmTaskInstance) ((JbpmTaskMgmtSession) context
						.getTaskMgmtSession()).getNextAnnotationTask(actorId);
				if (taskInstance != null) {
					log.debug("found next task " + taskInstance.getId()
							+ " for " + actorId);
				} else {
					log
							.debug("THERE ARE NO STARTED TASKS FOR actor "
									+ actorId);
				}
				return taskInstance;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#checkIfThereIsNextTaskInstanceForActor(java.lang.String)
	 */
	public Boolean checkIfThereIsNextTaskInstanceForActor(final String actorId) {
		return (Boolean) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				List<JbpmTaskInstance> pooledTaskInstances = ((JbpmTaskMgmtSession) context
						.getTaskMgmtSession())
						.findPendingPooledAnnotationTaskInstancesByActorId(actorId);

				Iterator<JbpmTaskInstance> it = pooledTaskInstances.iterator();
				boolean found = false;
				if (pooledTaskInstances != null
						&& pooledTaskInstances.size() > 0) {
					while (it.hasNext() && !found) {
						JbpmTaskInstance taskInstance = it.next();
						log.debug("found task instance: "
								+ taskInstance.getId());
						String documentId = taskInstance.getDocumentId();
						log.debug("found documentId: " + documentId);
						AnnotationStatusInfo annotationStatusInfo = null;
						if (documentId != null) {
							// create status variable name
							String statusVariableName = WorkflowUtil
									.createAnnotationStatusVariableName(
											taskInstance.getProcessInstance()
													.getProcessDefinition()
													.getName(), taskInstance
													.getProcessInstance()
													.getId(), taskInstance
													.getDocumentId());
							log.debug("look for variable: "
									+ statusVariableName);
							annotationStatusInfo = (AnnotationStatusInfo) taskInstance
									.getContextInstance().getVariable(statusVariableName);
							
							if (taskInstance.getStart() == null
									&& taskInstance.getActorId() == null
									&& AnnotationUtil
											.isDocumentAvailableToAnnotator(
													actorId,
													annotationStatusInfo)) {
								taskInstance.setActorId(actorId, false);
								found = true;
							}

						} else {
							log.warn("documentId is NULL for taskInstance ID: "
									+ taskInstance.getId());
						}
					}
				}
				return found;
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findUncompletedProcessInstancesForProcessDefinition(long)
	 */
	public int findUncompletedProcessInstancesForProcessDefinition(
			final long processDefinitionId) {
		return (Integer) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return ((JbpmGraphSession) context.getGraphSession())
						.findUncompletedProcessInstancesForProcessDefinition(processDefinitionId);
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllProcessDefinitions()
	 */
	public List findAllProcessDefinitions() {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return context.getGraphSession().findAllProcessDefinitions();
			}
		});
	}

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#executeCallbackAndStartTask(long,
	 *      String)
	 */
	public void executeCallbackAndStartTask(final long taskInstanceId,
			final String error) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				JbpmTaskInstance taskInstance = (JbpmTaskInstance) context
						.getTaskInstance(taskInstanceId);
				if (taskInstance != null) {
					if (taskInstance.getStart() == null) {
						taskInstance.start();
					} else {
						log.error("########### TASK ALREADY STARTED!");
					}

					String statusVariableName = WorkflowUtil
							.createAnnotationStatusVariableName(taskInstance
									.getProcessInstance()
									.getProcessDefinition().getName(),
									taskInstance.getProcessInstance().getId(),
									taskInstance.getDocumentId());
					log.debug("look for variable: " + statusVariableName);
					AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) taskInstance
							.getContextInstance().getVariable(statusVariableName);
					annotationStatusInfo.setTimeWorkedOn(taskInstance.getTimeWorkedOn());

					if ("".equals(error)) {
						AnnotationUtil.markDocumentAsTaken(taskInstance
								.getActorId(), annotationStatusInfo);
						log.debug("marked document as taken by: "
								+ taskInstance.getActorId());
						taskInstance.getContextInstance().setVariable(
								statusVariableName, annotationStatusInfo);
					} else {
						AnnotationUtil.markDocumentAsFailed(error,
								annotationStatusInfo);
						taskInstance.getContextInstance().setVariable(
								statusVariableName, annotationStatusInfo);

						taskInstance.end(JPDLConstants.TRANSITION_ERROR);
					}
				}
				return null;
			}
		});
	}

	public List findPendingTaskInstancesByProcessInstanceAndName(
			final long processInstanceId, final String taskName) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessInstance processInstance = context.getGraphSession()
						.getProcessInstance(processInstanceId);
				return ((JbpmTaskMgmtSession) context.getTaskMgmtSession())
						.findPendingTaskInstancesByProcessInstanceAndName(
								processInstance, taskName);
			}
		});

	}

	public Map findSwimlanesByProcessDefinitionId(final long processDefinitionId) {
		return (Map) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessDefinition processDefinition = context.getGraphSession()
						.getProcessDefinition(processDefinitionId);
				return processDefinition.getTaskMgmtDefinition().getSwimlanes();
			}
		});
	}

	// CONFIGURATION OPTIONS - MANAGER INTERFACE

	public List findPendingConfigurationOptionsByActorId(final String actorId) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return ((JbpmTaskMgmtSession) context.getTaskMgmtSession())
						.findPendingConfigurationOptionsByActorId(actorId);
			}
		});

	}

	public List findPendingConfigurationOptionsByProcessInstancesAndActorId(
			final ProcessInstance[] processInstances, final String actorId) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return ((JbpmTaskMgmtSession) context.getTaskMgmtSession())
						.findPendingConfigurationOptionsByProcessInstancesAndActorId(
								processInstances, actorId);
			}
		});

	}

	public List findPendingConfigurationOptionsByProcessInstanceKeyAndActorId(
			final String key, final String actorId) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				return ((JbpmTaskMgmtSession) context.getTaskMgmtSession())
						.findPendingConfigurationOptionsByProcessInstanceKeyAndActorId(
								key, actorId);
			}
		});
	}

	public List getDocumentStatusList(final long processInstanceId) {
		return (List) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				List<AnnotationStatusInfo> list = new ArrayList<AnnotationStatusInfo>();
				ProcessInstance processInstance = context.getGraphSession()
						.getProcessInstance(processInstanceId);
				String processDefinitionName = processInstance
						.getProcessDefinition().getName();
				Map<String, Object> variableMap = processInstance
						.getContextInstance().getVariables();
				String prefix = WorkflowUtil
						.createAnnotationStatusVariablePrefix(
								processDefinitionName, processInstanceId);
				Iterator<Map.Entry<String, Object>> it = variableMap.entrySet()
						.iterator();
				while (it.hasNext()) {
					Map.Entry<String, Object> entry = it.next();
					if (entry.getKey() != null
							&& entry.getKey().startsWith(prefix)) {
						AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) variableMap
								.get(entry.getKey());
						list.add(annotationStatusInfo);
					}
				}
				return list;
			}
		});
	}

	public AnnotationMetricMatrix getDocumentMatrix(final long processInstanceId) {
		return (AnnotationMetricMatrix) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessInstance processInstance = context.getGraphSession()
						.getProcessInstance(processInstanceId);
				String processDefinitionName = processInstance
						.getProcessDefinition().getName();
				Map<String, Object> variableMap = processInstance.getContextInstance().getVariables();
				String prefix = WorkflowUtil
						.createAnnotationStatusVariablePrefix(
								processDefinitionName, processInstanceId);
				Iterator<Map.Entry<String, Object>> it = variableMap.entrySet().iterator();
				AnnotationMetricMatrix annotationMetricMatrix = new AnnotationMetricMatrix();
				int numberOfAnnotated = 0;
				int numberOfDocuments = 0;
				String initiator = (String) variableMap
						.get(JPDLConstants.INITIATOR);
				annotationMetricMatrix.setInitiator(initiator);
				String annotationSchemaCSVList = (String) variableMap
						.get(JPDLConstants.ANNOTATION_SCHEMA_CSV_URLS);
				annotationMetricMatrix
						.setAnnotationSchemaCSVList(annotationSchemaCSVList);
				long totalExecutionTime = 0; 
				while (it.hasNext()) {
					Map.Entry<String, Object> entry = it.next();
					if (entry.getKey() != null
							&& entry.getKey().startsWith(prefix)) {
						AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) variableMap
								.get(entry.getKey());
						AnnotationMetricInfo annotationMetricInfo = annotationMetricMatrix
								.getMetricMap().get(annotationStatusInfo.getStatus());
						int count = annotationMetricInfo.getCount();
						annotationMetricInfo.setCount(count + 1);

						if(annotationStatusInfo.getStartDate() != null && annotationStatusInfo.getEndDate() != null) {
						
							totalExecutionTime += annotationStatusInfo.getTimeWorkedOn();
							numberOfAnnotated++;
							log.debug("numberOfAnnotated: " + numberOfAnnotated + 
									  ", totalExecutionTime: " + totalExecutionTime);
						}
						// put it back into metric map
						log.debug("put annotationMetricInfo in the map for status: " 
								+ annotationStatusInfo.getStatus());
						annotationMetricMatrix.getMetricMap().put(
								annotationStatusInfo.getStatus(),
								annotationMetricInfo);
						numberOfDocuments++;
					}
				}

				String globalAverageTime = JPDLConstants.INIT_TIME;
				String totalTime = JPDLConstants.INIT_TIME;
				if (totalExecutionTime > 0) {
					totalTime = DateUtil.getElapsedTimeHoursMinutesSecondsString(totalExecutionTime);
				}
				log.debug("totalExecutionTime: "+ totalExecutionTime + " ::: totalTime: "+ totalTime);
				
				if (numberOfAnnotated > 0 && numberOfDocuments > 0) {
					double tmp = ((double) numberOfAnnotated / (double) numberOfDocuments) * 100.0;
					//completionPercentage = (int) Math.rint(tmp);
					tmp = totalExecutionTime / numberOfAnnotated;
					globalAverageTime = DateUtil
							.getElapsedTimeHoursMinutesSecondsString((long) (Math.rint(tmp)));
				}
				annotationMetricMatrix.setTimeMetricInfo(new TimeMetricInfo(totalTime, globalAverageTime));
				annotationMetricMatrix.setTotalNumber(numberOfDocuments);

				return annotationMetricMatrix;
			}
		});
	}

	public AnnotationMetricMatrix getAnnotatorMatrix(
			final long processInstanceId, final String username) {
		return (AnnotationMetricMatrix) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessInstance processInstance = context.getGraphSession()
						.getProcessInstance(processInstanceId);
				String processDefinitionName = processInstance
						.getProcessDefinition().getName();
				Map<String, Object> variableMap = processInstance
						.getContextInstance().getVariables();
				String prefix = WorkflowUtil
						.createAnnotationStatusVariablePrefix(
								processDefinitionName, processInstanceId);
				//log.debug(prefix);
				Iterator<Map.Entry<String, Object>> it = variableMap.entrySet()
						.iterator();
				AnnotationMetricMatrix annotationMetricMatrix = new AnnotationMetricMatrix();

				while (it.hasNext()) {
					Map.Entry<String, Object> entry = it.next();
					if (entry.getKey() != null
							&& entry.getKey().startsWith(prefix)) {
						AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) variableMap
								.get(entry.getKey());
						if (annotationStatusInfo.getTakenByList() != null
								&& annotationStatusInfo.getTakenByList()
										.contains(username)) {
							AnnotationMetricInfo annotationMetricInfo = annotationMetricMatrix
									.getMetricMap()
									.get(
											AnnotationStatusInfo.STATUS_IN_PROGRESS);
							int count = annotationMetricInfo.getCount();
							annotationMetricInfo.setCount(count + 1);
							annotationMetricMatrix.getMetricMap().put(
									AnnotationStatusInfo.STATUS_IN_PROGRESS,
									annotationMetricInfo);
						}
						if (annotationStatusInfo.getAnnotatedByList() != null
								&& annotationStatusInfo.getAnnotatedByList()
										.contains(username)) {
							AnnotationMetricInfo annotationMetricInfo = annotationMetricMatrix
									.getMetricMap()
									.get(AnnotationStatusInfo.STATUS_ANNOTATED);
							int count = annotationMetricInfo.getCount();
							annotationMetricInfo.setCount(count + 1);
							annotationMetricMatrix.getMetricMap().put(
									AnnotationStatusInfo.STATUS_ANNOTATED,
									annotationMetricInfo);
						}
						if (annotationStatusInfo.getRejectedByList() != null
								&& annotationStatusInfo.getRejectedByList()
										.contains(username)) {
							AnnotationMetricInfo annotationMetricInfo = annotationMetricMatrix
									.getMetricMap()
									.get(AnnotationStatusInfo.STATUS_CANCELED);
							int count = annotationMetricInfo.getCount();
							annotationMetricInfo.setCount(count + 1);
							annotationMetricMatrix.getMetricMap().put(
									AnnotationStatusInfo.STATUS_CANCELED,
									annotationMetricInfo);
						}

					}
				}

				// sort map
				annotationMetricMatrix
						.setTimeMetricInfo(getAnnotatorTimeMetricInfo(
								processInstance, username));
				// log.debug("----------------------------------------------");
				//log.debug(annotationMetricMatrix);
				return annotationMetricMatrix;
			}
		});
	}

	public TimeMetricInfo getAnnotatorTimeMetricInfo(
			final ProcessInstance processInstance, final String username) {
		return (TimeMetricInfo) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {

				// now calculate average time annotator per document
				// find all completed tasks by this annotator in this process

				List<JbpmTaskInstance> completedTaskInstances = ((JbpmTaskMgmtSession) context
						.getTaskMgmtSession())
						.findCompletedTasksByActorIdAndProcessInstance(
								processInstance, username);

				int numberOfAnnotated = completedTaskInstances.size();
				long totalExecutionTime = 0;
				
				Iterator<JbpmTaskInstance> itr = completedTaskInstances.iterator();
				while (itr.hasNext()) {
					JbpmTaskInstance taskInstance = itr.next();
					totalExecutionTime += taskInstance.getTimeWorkedOn();
				}
				String averageTime = JPDLConstants.INIT_TIME;
				String totalTime = JPDLConstants.INIT_TIME;
				
				if (totalExecutionTime > 0) {
					totalTime = DateUtil.getElapsedTimeHoursMinutesSecondsString(totalExecutionTime);
				}
				if (numberOfAnnotated > 0) {
					double tmp = (double) totalExecutionTime / (double) numberOfAnnotated;
					// log.debug("average time: "+averageTime);
					averageTime = DateUtil.getElapsedTimeHoursMinutesSecondsString((long) (Math.rint(tmp)));
				}
				return new TimeMetricInfo(totalTime, averageTime);
			}
		});
	}

	public AnnotationMetricMatrix getGlobalAnnotatorMatrix(
			final long processInstanceId, final String roleName) {
		return (AnnotationMetricMatrix) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				ProcessInstance processInstance = context.getGraphSession()
						.getProcessInstance(processInstanceId);
				String processDefinitionName = processInstance
						.getProcessDefinition().getName();
				Map<String, Object> variableMap = processInstance
						.getContextInstance().getVariables();
				String prefix = WorkflowUtil
						.createAnnotationStatusVariablePrefix(
								processDefinitionName, processInstanceId);
				Iterator<Map.Entry<String, Object>> it = variableMap.entrySet()
						.iterator();

				// find variable with name = roleName + "CSVList"
				String variableName = roleName + "CSVList";
				// log.debug("variableName: "+variableName);
				String userCSVList = (String) variableMap.get(variableName);
				// log.debug("userCSVList: "+userCSVList);
				Collection<String> users = StringUtils
						.commaDelimitedListToSet(userCSVList);

				AnnotationMetricMatrix annotationMetricMatrix = new AnnotationMetricMatrix(
						users);

				while (it.hasNext()) {
					Map.Entry<String, Object> entry = it.next();
					if (entry.getKey() != null
							&& entry.getKey().startsWith(prefix)) {
						AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) variableMap
								.get(entry.getKey());
						Iterator<String> itr = users.iterator();
						while (itr.hasNext()) {
							String username = itr.next();
							if (annotationStatusInfo.getAnnotatedByList() != null
									&& annotationStatusInfo
											.getAnnotatedByList().contains(
													username)) {
								AnnotationMetricInfo annotationMetricInfo = annotationMetricMatrix
										.getMetricMap().get(username);
								int count = annotationMetricInfo.getCount();
								annotationMetricInfo.setCount(count + 1);
								annotationMetricInfo
										.setTimeMetricInfo(getAnnotatorTimeMetricInfo(
												processInstance, username));
								annotationMetricMatrix.getMetricMap().put(
										username, annotationMetricInfo);
							}
						}

					}
				}
				// log.debug("----------------------------------------------");
				// log.debug(annotationMetricMatrix);
				return annotationMetricMatrix;
			}
		});
	}

	public void updateVariables(final ProcessInstance processInstance,
			final Map variableMap) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				JbpmGraphSession graphSession = (JbpmGraphSession) context
						.getGraphSession();
				List<ProcessInstance> subprocesses = graphSession
						.findAllSubProcessInstances(processInstance);
				Iterator<Map.Entry<String, Object>> it = variableMap.entrySet()
						.iterator();
				while (it.hasNext()) {
					Map.Entry<String, Object> entry = it.next();
					if (entry.getKey() != null) {
						processInstance.getContextInstance().setVariable(
								entry.getKey(), entry.getValue());
						log.debug("setting variable key: " + entry.getKey()
								+ "  to value: " + entry.getValue());
						Iterator<ProcessInstance> itr = subprocesses.iterator();
						while (itr.hasNext()) {
							itr.next().getContextInstance().setVariable(
									entry.getKey(), entry.getValue());
						}
					}
				}
				return null;
			}
		});
	}

	public void updatePooledActors(final String variableName,
			final String[] newActorIds, final ProcessInstance processInstance) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				JbpmGraphSession graphSession = (JbpmGraphSession) context
						.getGraphSession();
				JbpmTaskMgmtSession taskMgmtSession = (JbpmTaskMgmtSession) context
						.getTaskMgmtSession();
				List<ProcessInstance> processInstanceList = graphSession
						.findAllSubProcessInstances(processInstance);
				processInstanceList.add(processInstance);
				ProcessInstance[] processInstances = (ProcessInstance[]) processInstanceList
						.toArray(new ProcessInstance[processInstanceList.size()]);
				String oldActorCSVString = (String) processInstance
						.getContextInstance().getVariables().get(variableName);
				String[] oldActorIds = StringUtils
						.commaDelimitedListToStringArray(oldActorCSVString);
				List<TaskInstance> pooledTaskInstances = taskMgmtSession
						.findPooledTaskInstancesByActorIdsAndProcessInstances(
								oldActorIds, processInstances);
				log.debug("found taskinstances which pool should be changed: "
						+ pooledTaskInstances.size());
				Iterator<TaskInstance> itr = pooledTaskInstances.iterator();
				while (itr.hasNext()) {
					TaskInstance taskInstance = itr.next();
					if (taskInstance.getSwimlaneInstance() != null
							&& variableName.startsWith(taskInstance
									.getSwimlaneInstance().getName())) {
						log.debug("changing pool of task instance: "
								+ taskInstance.getId());
						taskInstance.setPooledActors(newActorIds);
					} else {
						log.debug("do not change pool of task instance: "
								+ taskInstance.getId());
					}
				}
				// update variable
				String newActorCSVString = StringUtils
						.arrayToCommaDelimitedString(newActorIds);
				log.debug("oldActorCSVString: " + oldActorCSVString
						+ ";   newActorCSVString: " + newActorCSVString);
				List<ProcessInstance> subprocesses = graphSession
						.findAllSubProcessInstances(processInstance);
				processInstance.getContextInstance().setVariable(variableName,
						newActorCSVString);
				// now set the same variable in all subprocesses which have it
				// set already
				Iterator<ProcessInstance> it = subprocesses.iterator();
				while (it.hasNext()) {
					ProcessInstance subProcessInstance = it.next();
					if (subProcessInstance.getContextInstance().getVariable(
							variableName) != null) {
						subProcessInstance.getContextInstance().setVariable(
								variableName, newActorCSVString);
					} else {
						log.debug("do not set variable: " + variableName);
					}
				}

				return null;
			}
		});
	}

	public void deleteTaskInstances(final Collection<Long> taskInstanceIds) {
		execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				JbpmTaskMgmtSession taskMgmtSession = ((JbpmTaskMgmtSession) context
						.getTaskMgmtSession());

				taskMgmtSession.deleteTaskInstances(taskInstanceIds);

				return null;
			}
		});
	}

	public Boolean isAnyProcessInstanceRunningAgainstTheCorpus(
			final String processDefinitionName, final String corpusId) {
		return (Boolean) execute(new JbpmCallback() {
			public Object doInJbpm(JbpmContext context) {
				boolean flag = false;
				List processInstances = ((JbpmGraphSession) context
						.getGraphSession())
						.findAllProcessInstancesByProcessDefinitionNameExcludingSubProcessInstances(processDefinitionName);
				Iterator it = processInstances.iterator();

				while (it.hasNext() && !flag) {
					ProcessInstance processInstance = (ProcessInstance) it
							.next();
					Map variableMap = processInstance.getContextInstance()
							.getVariables();
					String cId = (String) variableMap
							.get(JPDLConstants.CORPUS_ID);
					if (corpusId.equals(cId)
							&& !(processInstance.hasEnded() || processInstance
									.isSuspended())) {
						flag = true;
						log.debug("found running process for corpusId: "
								+ corpusId);
					}
				}
				return flag;
			}
		});
	}

	public AnnotationMetricMatrix getAnnotatorMatrixForAllProcesses(
			String username) {
		// TODO Auto-generated method stub
		return null;
	}

}
