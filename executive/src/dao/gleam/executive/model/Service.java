package gleam.executive.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * This class is used to generate the Struts Validator Form as well as
 * the This class is used to generate Spring Validation rules as well as
 * the Hibernate mapping file.
 *
 * <p>
 * <a href="Service.java.html"><i>View Source</i></a>
 *
 * @author <a href="mailto:H.Sun@dcs.shef.ac.uk">Haotian Sun</a>
 *         
 * @struts.form include-all="true" extends="BaseForm"
 * @hibernate.class table="service"
 */
public class Service extends BaseObject implements Serializable{
  private static final long serialVersionUID = 3832626162173359433L;
  
  protected Long id;
  
  protected String name;
  
  protected boolean enabled;
  
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
   * 
   * @hibernate.property column="name" length="20" unique="true"
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * @hibernate.property column="service_enabled" type="yes_no"
   */
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public boolean equals(Object o) {
    if(this == o) return true;
    if(!(o instanceof Service)) return false;
    final Service service = (Service)o;
    if(name != null ? !name.equals(service.getName()) : service
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
