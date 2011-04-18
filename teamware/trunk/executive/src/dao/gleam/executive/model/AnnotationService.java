package gleam.executive.model;

import gleam.executive.util.MapUtil;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is used to generate the Struts Validator Form as well as the This
 * class is used to generate Spring Validation rules as well as the Hibernate
 * mapping file.
 * 
 * <p>
 * <a href="AnnotationService.java.html"><i>View Source</i></a>
 * 
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 * 
 * @struts.form include-all="true" extends="gleam.executive.webapp.form.BaseForm"
 * @hibernate.class table="annotation_service"
 */
public class AnnotationService extends BaseObject {

	private final static Log log = LogFactory.getLog(AnnotationService.class);
	 
	private Long id;
	private String name;
	private String url;

	private String description;
	private String parameters;
	private boolean canUsePrivateUrls;

	private Long annotationServiceTypeId;
	private AnnotationServiceType annotationServiceType;
	
	public AnnotationService() {
	}

    public AnnotationService(String name) {
	    this.name = name;
	}

	/**
	 * @hibernate.id column="id" generator-class="native" unsaved-value="null"
	 * 
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @struts.validator type="required"
	 * @hibernate.property length="100" column="name" not-null="true"
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @struts.validator type="required"
	 * @hibernate.property length="100" column="url" not-null="true"	 
	 */
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @struts.validator type="required"
	 * @hibernate.property length="255" column="description" not-null="true"
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @hibernate.property column="parameters" type = "text"                    
	 */
	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	/**
	 * @hibernate.property column="annotation_service_type_id"
	 */
	public Long getAnnotationServiceTypeId() {
		return annotationServiceTypeId;
	}

	public void setAnnotationServiceTypeId(Long annotationServiceTypeId) {
		log.debug("setting: "+annotationServiceTypeId);
		this.annotationServiceTypeId = annotationServiceTypeId;
	}

	/**
	 * @hibernate.many-to-one insert="false" update="false"
	 *                        cascade="merge,persist"
	 *                        column="annotation_service_type_id"
	 *                        outer-join="true"
	 */
	public AnnotationServiceType getAnnotationServiceType() {
		return annotationServiceType;
	}

	public void setAnnotationServiceType(AnnotationServiceType annotationServiceType) {
		this.annotationServiceType = annotationServiceType;
	}
	
  /**
   * @hibernate.property column="can_use_private_urls" type="yes_no"
   */
  public boolean isCanUsePrivateUrls() {
    return canUsePrivateUrls;
  }

  public void setCanUsePrivateUrls(boolean canUsePrivateUrls) {
    this.canUsePrivateUrls = canUsePrivateUrls;
  }

  @Override
	  public boolean equals(Object o) {
	    if(this == o) return true;
	    if(!(o instanceof AnnotationService)) return false;
	    final AnnotationService annotationService = (AnnotationService)o;
	    if(name != null ? !name.equals(annotationService.getName()) : annotationService
	            .getName()!= null) return false;
	    return true;
	  }

	  @Override
	  public int hashCode() {
	    return (name!= null ? name.hashCode() : 0);
	  }

	  @Override
	  public String toString() {
	    ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
	      .append("name", this.name);
	    return sb.toString();
	  }

}
