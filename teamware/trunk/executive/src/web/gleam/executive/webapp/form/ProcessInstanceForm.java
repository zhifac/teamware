package gleam.executive.webapp.form;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;

/**
 * @struts.form name="processInstanceForm"
 */
public class ProcessInstanceForm extends BaseForm implements java.io.Serializable {

	protected String projectId;

	protected String name;

	protected String corpusId;

	protected String[] annotators;

	// protected String[] curators;

	protected String manager;

	public String getName() {

		return this.name;
	}

	/**
	 * @struts.validator type="required"
	 */
	public void setName(String name) {

		this.name = name;
	}

	public String[] getAnnotators() {

		return annotators;
	}

	public void setAnnotators(String[] annotators) {

		this.annotators = annotators;
	}

	/*
	 * public String[] getCurators() { return curators; }
	 * 
	 * 
	 * public void setCurators(String[] curators) { this.curators = curators; }
	 */

	public String getManager() {

		return manager;
	}

	/**
	 * @struts.validator type="required"
	 */
	public void setManager(String manager) {

		this.manager = manager;
	}

	/**
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		// reset any boolean data types to false

	}

	public String getProjectId() {

		return projectId;
	}

	public void setProjectId(String projectId) {

		this.projectId = projectId;
	}

	public String getCorpusId() {

		return corpusId;
	}

	public void setCorpusId(String corpusId) {

		this.corpusId = corpusId;
	}

}
