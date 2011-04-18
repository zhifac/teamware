package gleam.executive.webapp.form;

import gleam.executive.workflow.model.SwimlaneBean;

import java.util.List;

/**
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 * 
 * @struts.form name="processDefPropertiesForm"
 */
public class ProcessDefPropertiesForm extends BaseForm {
	private static final long serialVersionUID = 1967999873896482349L;

	private List<SwimlaneBean> swimlanes;

	public List<SwimlaneBean> getSwimlanes() {
		return swimlanes;
	}

	public void setSwimlanes(List<SwimlaneBean> swimlanes) {
		this.swimlanes = swimlanes;
	}

}
