package gleam.executive.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.acegisecurity.GrantedAuthority;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * This class is used to represent available roles in the database.
 * </p>
 * 
 * <p>
 * <a href="Role.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 *         Version by Dan Kibler dan@getrolling.com Extended to
 *         implement Acegi GrantedAuthority interface by David Carter
 *         david@carter.net
 * 
 * @struts.form include-all="true" extends="gleam.executive.webapp.form.BaseForm"
 * @hibernate.class table="role"
 */
public class Role extends BaseObject implements Serializable, GrantedAuthority {
  private static final long serialVersionUID = 3690197650654049848L;

  private Long id;

  private String name;

  private String description;
  
  private String[] members; 

  protected Set<Resource> resources = new HashSet<Resource>();
  
  public Role() {
  }

  public Role(String name) {
    this.name = name;
  }

  /**
   * @hibernate.id column="id" generator-class="native"
   *               unsaved-value="null"
   */
  public Long getId() {
    return id;
  }

  /**
   * @see org.acegisecurity.GrantedAuthority#getAuthority()
   */
  public String getAuthority() {
    return getName();
  }

  /**
   * @struts.validator type="required"
   * @struts.validator type="mask" msgkey="roleForm.invalidName"
   * @struts.validator-var name="mask" value="^[a-zA-Z0-9_-]+$"
   * @hibernate.property column="name" length="20" unique="true"
   */
  public String getName() {
    return this.name;
  }

  /**
   * @struts.validator type="required"
   * @hibernate.property column="description" length="64"
   */
  public String getDescription() {
    return this.description;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String[] getMembers() {
		return members;
  }

  public void setMembers(String[] members) {
	    this.members = members;
  }

  /**
   * @hibernate.set table="resource_role" cascade="save-update" lazy="false"
   * @hibernate.collection-key column="role_id"
   * @hibernate.collection-many-to-many class="gleam.executive.model.Resource"
   *                                    column="resource_id"
   */
  public Set<Resource> getResources() {
    return resources;
  }

  /**
   * Adds a resource for the role
   *
   * @param resource
   */
  public void addResource(Resource resource) {
    getResources().add(resource);
  }
  /**
   * @param resources The resources to set.
   */
  public void setResources(Set<Resource> resources) {
    this.resources = resources;
  }

  public boolean equals(Object o) {
    if(this == o) return true;
    if(!(o instanceof Role)) return false;
    final Role role = (Role)o;
    return !(name != null ? !name.equals(role.name) : role.name != null);
  }

  public int hashCode() {
    return (name != null ? name.hashCode() : 0);
  }

  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(
            this.name).toString();
  }


}
