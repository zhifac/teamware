package gleam.executive.webapp.taglib;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import gleam.executive.model.AnnotationSchema;
import gleam.executive.model.AnnotationService;
import gleam.executive.model.Corpus;
import gleam.executive.model.LabelValue;
import gleam.executive.model.User;
import gleam.executive.model.WebAppBean;
import gleam.executive.service.AnnotationServiceManager;
import gleam.executive.service.DocServiceManager;
import gleam.executive.service.GateServiceManager;
import gleam.executive.service.GosManager;
import gleam.executive.service.SafeManagerException;
import gleam.executive.service.UserManager;
import gleam.executive.workflow.manager.WorkflowManager;
import gleam.gateservice.client.GateServiceClientException;

/**
 * 
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 * 
 * @jsp.tag name="customSelect" bodycontent="JSP"
 * 
 */

public class CustomSelectTag extends BodyTagSupport {

	private static Log log = LogFactory.getLog(CustomSelectTag.class);
	private static final long serialVersionUID = 2004095567803546495L;

	private String name;

	// in case of multiselect it can be csv string
	private String selected;

	private String method;

	private String clazz;

	private String params;

	private String multiple;
	
	private boolean readOnly;

	private String task;

	/**
	 * Property used to pass taskInstanceId to the tag
	 * 
	 * @param method
	 * 
	 * @jsp.attribute required="true" rtexprvalue="true"
	 */
	public void setTask(String task) {
		this.task = task;
	}

	/**
	 * @param clazz
	 *            The class to set.
	 * 
	 * @jsp.attribute required="false" rtexprvalue="true"
	 */
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	/**
	 * @param multiple
	 *            . Indicate if select box is multiple
	 * 
	 * @jsp.attribute required="false" rtexprvalue="true"
	 */
	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

	/**
	 * @param params
	 *            Comma separated param. values
	 * 
	 * @jsp.attribute required="false" rtexprvalue="true"
	 */
	public void setParams(String params) {
		this.params = params;
	}

	/**
	 * @param name
	 *            The name to set.
	 * 
	 * @jsp.attribute required="true" rtexprvalue="true"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param selected
	 *            The selected option.
	 * @jsp.attribute required="false" rtexprvalue="true"
	 */
	public void setSelected(String selected) {
		this.selected = selected;
	}

	/**
	 * Property used to simply method that populate combo box option - values
	 * map
	 * 
	 * @param method
	 * 
	 * @jsp.attribute required="true" rtexprvalue="true"
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @param readOnly
	 *            The readOnly option.
	 * @jsp.attribute required="false" rtexprvalue="true"
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	/**
	 * Process the start of this tag.
	 * 
	 * @return int status
	 * 
	 * @exception JspException
	 *                if a JSP exception has occurred
	 * 
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {

		// Locale userLocale = pageContext.getRequest().getLocale();

		String methodName = this.method;
		String[] parameters = null;
		String[] methodParameters = null;
		// construct method parameters from parameters.
		// the thing is, that among the parameters it may be some
		// referenced taskInstance variable.
		String isMultiple = "";

		if (this.params != null && !this.params.equals("")) {
			parameters = params.split(",");
			methodParameters = new String[parameters.length];

		}
		
		String isDisabled = "";
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(pageContext
						.getServletContext());
		WorkflowManager workflowManager = (WorkflowManager) ctx
				.getBean("workflowManager");

		List result = new ArrayList();
		try {

			// Fetch the method
			Class[] parametersClass = new Class[] {};
			if (parameters != null && parameters.length != 0) {
				parametersClass = new Class[parameters.length];
				for (int i = 0; i < parameters.length; i++) {
					if (workflowManager.findVariable(new Long(task),
							parameters[i]) != null) {
						methodParameters[i] = workflowManager.findVariable(
								new Long(task), parameters[i]).toString();
						log.debug("found variable: NAME: " + parameters[i]
								+ ", VALUE: " + methodParameters[i]);
					} else {
						log.debug("NOT found variable: NAME: " + parameters[i]
								+ " Treat is as string constant");
						methodParameters[i] = parameters[i];
					}
					parametersClass[i] = methodParameters[i].getClass();
					log.debug("found variable class: " + parametersClass[i]);

				}
			}
			try {
				Method method = CustomSelectTag.class.getMethod(methodName,
						parametersClass);
				log.debug("found method name: " + method.getName());

				// Call fetched method
				Object[] methodParams = methodParameters;
				result = (List) method.invoke(this, methodParams);
			} catch (NoSuchMethodException e) {
				// if there is not such method, trying find CSV variable with
				// that name.
				log.debug("there is no method name: " + methodName);
				String csvString = (String) workflowManager.findVariable(
						new Long(task), methodName);
				if (csvString != null) {
					log.debug("found WF variable: " + csvString);
					// create list of LAbelValue
					String[] tmpArray = StringUtils
							.commaDelimitedListToStringArray(csvString);
					result = new ArrayList<LabelValue>();
					for (int k = 0; k < tmpArray.length; k++) {
						LabelValue lv = new LabelValue();
						lv.setLabel(tmpArray[k]);
						lv.setValue(tmpArray[k]);
						result.add(lv);
					}

				} else {
					log.debug("not found WF variable: " + csvString);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e);
		}

		
		StringBuffer resultBuffer = new StringBuffer();
		// check if there is anything in result
		if(result!=null && result.size()>0){
		log.debug("OPTIONS SIZE: " + result.size());
		int size = calculateSize(result.size());
		log.debug("CALCULATED SIZE TO DISPLAY: " +size);
		if (this.multiple != null && this.multiple.equals("true")){
			isMultiple = "multiple=\"multiple\" size=\" " + size + "\"";
		}
		
		if(this.readOnly){
			isDisabled = "disabled";
		}
		resultBuffer.append("<select name=\"" + name + "\" id=\"" + name
				+ "\" class=\"" + clazz + "\" " + isMultiple + " " + isDisabled + ">\n");

		String[] selectedArray = StringUtils
				.commaDelimitedListToStringArray(selected);

		for (Iterator i = result.iterator(); i.hasNext();) {
			LabelValue lv = (LabelValue) i.next();
			resultBuffer.append("    <option value=\"" + lv.getValue() + "\"");
			for (int j = 0; j < selectedArray.length; j++) {
				if ((selectedArray[j] != null)
						&& selectedArray[j].equals(lv.getValue())) {
					resultBuffer.append(" selected ");
				}
			}
			resultBuffer.append(">" + lv.getLabel() + "</option>\n");
		}

		resultBuffer.append("</select>");
		}
		try {
			pageContext.getOut().write(resultBuffer.toString());
		} catch (IOException io) {
			throw new JspException(io);
		}

		return super.doStartTag();
	}

	/**
	 * Release aquired resources to enable tag reusage.
	 * 
	 * @see javax.servlet.jsp.tagext.Tag#release()
	 */
	public void release() {
		super.release();
	}

	/**
	 * Build a List of LabelValues for all the available countries. Uses the two
	 * letter uppercase ISO name of the country as the value and the localized
	 * country name as the label.
	 * 
	 * @param locale
	 *            The Locale used to localize the country names.
	 * 
	 * @return List of LabelValues for all available countries.
	 */
	public List countryList() {
		Locale locale = pageContext.getRequest().getLocale();
		final String EMPTY = "";
		final Locale[] available = Locale.getAvailableLocales();

		List countries = new ArrayList();

		for (int i = 0; i < available.length; i++) {
			final String iso = available[i].getCountry();
			final String name = available[i].getDisplayCountry(locale);

			if (!EMPTY.equals(iso) && !EMPTY.equals(name)) {
				LabelValue country = new LabelValue(name, iso);

				if (!countries.contains(country)) {
					countries.add(new LabelValue(name, iso));
				}
			}
		}
		Collections.sort(countries, new LabelValueComparator(locale));
		return countries;
	}

	/**
	 * Build a List of LabelValues for all users with specified role.
	 * 
	 * @param roleName
	 *            the role name
	 * @return List of LabelValues for all available users.
	 */
	public List usersInRoleList(String roleName) {
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(pageContext
						.getServletContext());
		UserManager userManager = (UserManager) ctx.getBean("userManager");
		List usersList = userManager.getUsersWithRole(roleName);
		List<LabelValue> users = new ArrayList<LabelValue>();
		Iterator it = usersList.iterator();
		while (it.hasNext()) {
			User user = (User) it.next();
			final String value = user.getUsername();
			final String option = user.getUsername();
			if (!"".equals(value) && !"".equals(option)
					&& !users.contains(user)) {
				users.add(new LabelValue(option, value));
			}
		}

		return users;
	}

	/**
	 * Build a List of LabelValues for all the available users.
	 * 
	 * @return List of LabelValues for all available users.
	 */
	public List usersList() {
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(pageContext
						.getServletContext());
		UserManager userManager = (UserManager) ctx.getBean("userManager");
		List usersList = userManager.getUsers();
		List<LabelValue> users = new ArrayList<LabelValue>();
		Iterator it = usersList.iterator();
		while (it.hasNext()) {
			User user = (User) it.next();
			// final String value = user.getId().toString();
			final String value = user.getUsername();
			final String option = user.getUsername();
			if (!"".equals(value) && !"".equals(option)
					&& !users.contains(user) && user.isEnabled()) {
				users.add(new LabelValue(option, value));
			}
		}
		return users;
	}

	/**
	 * Build a List of LabelValues for all the available corpora.
	 * 
	 * @return List of LabelValues for all available users.
	 */
	public List corporaList() throws SafeManagerException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(pageContext
						.getServletContext());
		DocServiceManager docServiceManager = (DocServiceManager) ctx
				.getBean("docServiceManager");

		List<Corpus> corporaList = docServiceManager.listCorpora();

		List<LabelValue> corpora = new ArrayList<LabelValue>();
		Iterator<Corpus> it = corporaList.iterator();
		while (it.hasNext()) {
			Corpus corpus = it.next();
			final String value = corpus.getCorpusID();
			final String option = corpus.getCorpusName();
			corpora.add(new LabelValue(option, value));
		}
		return corpora;
	}

	public List numOfPerformersPerTaskList() {

		List<LabelValue> list = new ArrayList<LabelValue>();
		list.add(new LabelValue("1", "1"));
		list.add(new LabelValue("2", "2"));
		list.add(new LabelValue("3", "3"));
		list.add(new LabelValue("4", "4"));
		list.add(new LabelValue("5", "5"));
		list.add(new LabelValue("6", "6"));
		list.add(new LabelValue("7", "7"));
		list.add(new LabelValue("8", "8"));
		list.add(new LabelValue("9", "9"));
		list.add(new LabelValue("10", "10"));

		return list;
	}

	public List assignmentStrategyList() {

		List<LabelValue> list = new ArrayList<LabelValue>();
		list.add(new LabelValue("random", "random"));
		// list.add(new LabelValue("least busy", "least busy"));
		return list;
	}

	public List gasRequiredParameterNamesList(String gateServiceURLString) {
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(pageContext
						.getServletContext());
		GateServiceManager gateServiceManager = (GateServiceManager) ctx
				.getBean("gateServiceManager");
		String[] paraNames = null;
		URI gateServiceURI = null;
		try {
			gateServiceURI = new URI(gateServiceURLString);
			paraNames = gateServiceManager
					.getRequiredParameterNames(gateServiceURI);
		} catch (GateServiceClientException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		List<LabelValue> list = new ArrayList<LabelValue>();
		if (paraNames != null) {
			for (int i = 0; i < paraNames.length; i++) {
				list.add(new LabelValue(paraNames[i], paraNames[i]));
			}
		}
		return list;
	}

	public List gasOptionalParameterNamesList(String gateServiceURLString) {
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(pageContext
						.getServletContext());
		GateServiceManager gateServiceManager = (GateServiceManager) ctx
				.getBean("gateServiceManager");
		String[] paraNames = null;
		URI gateServiceURI = null;
		try {
			gateServiceURI = new URI(gateServiceURLString);
			paraNames = gateServiceManager
					.getOptionalParameterNames(gateServiceURI);
		} catch (GateServiceClientException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		List<LabelValue> list = new ArrayList<LabelValue>();
		if (paraNames != null) {
			for (int i = 0; i < paraNames.length; i++) {
				list.add(new LabelValue(paraNames[i], paraNames[i]));
			}
		}
		return list;
	}

	public List gasInputAnnotationSetNamesList(String gateServiceURLString) {
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(pageContext
						.getServletContext());
		GateServiceManager gateServiceManager = (GateServiceManager) ctx
				.getBean("gateServiceManager");
		String[] paraNames = null;
		URI gateServiceURI = null;
		try {
			gateServiceURI = new URI(gateServiceURLString);
			paraNames = gateServiceManager
					.getInputAnnotationSetNames(gateServiceURI);
		} catch (GateServiceClientException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		List<LabelValue> list = new ArrayList<LabelValue>();
		if (paraNames != null) {
			for (int i = 0; i < paraNames.length; i++) {
				list.add(new LabelValue(paraNames[i], paraNames[i]));
			}
		}
		return list;
	}

	public List gasOutputAnnotationSetNamesList(String gateServiceURLString) {
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(pageContext
						.getServletContext());
		GateServiceManager gateServiceManager = (GateServiceManager) ctx
				.getBean("gateServiceManager");
		String[] paraNames = null;
		URI gateServiceURI = null;
		try {
			gateServiceURI = new URI(gateServiceURLString);
			paraNames = gateServiceManager
					.getOutputAnnotationSetNames(gateServiceURI);
		} catch (GateServiceClientException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		List<LabelValue> list = new ArrayList<LabelValue>();
		if (paraNames != null) {
			for (int i = 0; i < paraNames.length; i++) {
				list.add(new LabelValue(paraNames[i], paraNames[i]));
			}
		}
		return list;
	}

	/**
	 * Build a List of LabelValues for all the available corpora.
	 * 
	 * @return List of LabelValues for all available users.
	 */
	public List ontologyRepositoryList() throws SafeManagerException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(pageContext
						.getServletContext());
		GosManager gosManager = (GosManager) ctx.getBean("gosManager");

		

		List<LabelValue> repositories = new ArrayList<LabelValue>();
		/*
		List<String> ontologyRepositoryList = gosManager.listRepositories();
		Iterator<String> it = ontologyRepositoryList.iterator();
		while (it.hasNext()) {
			String ontologyRepositoryName = it.next();
			final String value = ontologyRepositoryName;
			final String option = ontologyRepositoryName;
			repositories.add(new LabelValue(option, value));
		}
		*/
		return repositories;
	}

	/**
	 * Build a List of LabelValues for all the available annotation schemas.
	 * 
	 * @return List of LabelValues for all available annotation schemas.
	 */
	public List annotationSchemaList() throws SafeManagerException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(pageContext
						.getServletContext());
		AnnotationServiceManager annotationServiceManager = (AnnotationServiceManager) ctx
				.getBean("annotationServiceManager");
		List<AnnotationSchema> schemaList = annotationServiceManager
				.listSchemas();
		List<LabelValue> schemas = new ArrayList<LabelValue>();
		Iterator<AnnotationSchema> it = schemaList.iterator();
		while (it.hasNext()) {
			AnnotationSchema schema = it.next();
			// final String value = user.getId().toString();
			final String value = schema.getName();
			final String option = schema.getName();
			if (!"".equals(value) && !"".equals(option)) {
				schemas.add(new LabelValue(option, value));
			}
		}
		return schemas;
	}

	public List annotationServiceList() throws SafeManagerException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(pageContext
						.getServletContext());

		AnnotationServiceManager annotationServiceManager = (AnnotationServiceManager) ctx
				.getBean("annotationServiceManager");

		List<AnnotationService> annotationServiceList = annotationServiceManager
				.getAnnotationServices();

		// first add "none" option with serviceId=0
		List<LabelValue> annotationServices = new ArrayList<LabelValue>();
		Iterator<AnnotationService> it = annotationServiceList.iterator();
		String noneOption = "None";
		String noneValue = "0";
		annotationServices.add(new LabelValue(noneOption, noneValue));
		while (it.hasNext()) {
			AnnotationService annotationService = it.next();
			// skip application service, since it cannot be used for automatic annotation

			final String option = annotationService.getAnnotationServiceType()
					.getName()
					+ ": " + annotationService.getName();
			final String value = String.valueOf(annotationService.getId());
			annotationServices.add(new LabelValue(option, value));
			
		}
		return annotationServices;
	}

	public List annotationServiceListByCSVIdsList(String csvIds)
			throws SafeManagerException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(pageContext
						.getServletContext());

		log.debug("csvIds: "+csvIds);
		String[] ids = StringUtils.commaDelimitedListToStringArray(csvIds);
		AnnotationServiceManager annotationServiceManager = (AnnotationServiceManager) ctx
				.getBean("annotationServiceManager");

		List<LabelValue> annotationServices = new ArrayList<LabelValue>();
        try{
		for (int i = 0; i < ids.length; i++) {
			//String option = "None"; 	
			if(!"0".equals(ids[i])){
		    	
			AnnotationService annotationService = annotationServiceManager
					.getAnnotationService(new Long(ids[i]));
		    String option = annotationService.getAnnotationServiceType()
					.getName()
					+ ": " + annotationService.getName();
			
			annotationServices.add(new LabelValue(option, ids[i]));
			}
		}
        } catch(NumberFormatException ne){
        	log.error(ne);
        }
		return annotationServices;
	}

	/**
	 * 
	 * Class to compare LabelValues using their labels with locale-sensitive
	 * behaviour.
	 */
	public class LabelValueComparator implements Comparator {
		private Comparator c;

		/**
		 * Creates a new LabelValueComparator object.
		 * 
		 * @param locale
		 *            The Locale used for localized String comparison.
		 */
		public LabelValueComparator(Locale locale) {
			c = Collator.getInstance(locale);
		}

		/**
		 * Compares the localized labels of two LabelValues.
		 * 
		 * @param o1
		 *            The first LabelValue to compare.
		 * @param o2
		 *            The second LabelValue to compare.
		 * 
		 * @return The value returned by comparing the localized labels.
		 */
		public final int compare(Object o1, Object o2) {
			LabelValue lhs = (LabelValue) o1;
			LabelValue rhs = (LabelValue) o2;
			return c.compare(lhs.getLabel(), rhs.getLabel());
		}
	}
	
	private int calculateSize(int numOfOptions){
		int size = 3;
		if(numOfOptions>3){
			if(numOfOptions<7){
			   size = numOfOptions-1;
			}
			else {
			   size = 6;
			}
		}
		return size;
	}

}
