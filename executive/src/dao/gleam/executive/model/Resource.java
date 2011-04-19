/*
 *  Resource.java
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
package gleam.executive.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.acegisecurity.GrantedAuthority;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * This class is used to generate the Struts Validator Form as well as
 * the This class is used to generate Spring Validation rules as well as
 * the Hibernate mapping file.
 *
 * <p>
 * <a href="Resource.java.html"><i>View Source</i></a>
 *
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 *         
 * @struts.form include-all="true" extends="BaseForm"
 * @hibernate.class table="resource"
 */

public class Resource extends BaseObject implements Serializable{
  private static final long serialVersionUID = 3832626162173359433L;
  
  protected Long id;
  
  protected Long service_id;
  
  protected Service service;
  
  protected String url;
  
  protected String description;
  
  protected Set<Role> roles = new HashSet<Role>();
  
  /**
   * @hibernate.id column="id" generator-class="native" unsaved-value="null"
   *               
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id The id to set.
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @struts.validator type="required"
   * @struts.validator type="mask" msgkey="resourceForm.invalidURL"
   * @struts.validator-var name="mask" value="^[a-zA-Z0-9_/\.\*-?]+$"
   * @hibernate.property column="url" length="100" not-null="true" unique="true"
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url The url to set.
   */
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

  /**
   * @param resourceDescription The resourceDescription to set.
   */
  public void setDescription(String description) {
    this.description = description;
  }
  
  /**
   * @hibernate.property column="service_id" 
   */
  public Long getService_id() {
    return service_id;
  }

  public void setService_id(Long service_id) {
    this.service_id = service_id;
  }
  
  /**
   * @hibernate.many-to-one insert="false" update="false" cascade="merge,persist"
   *  column="service_id" outer-join="true"
   */
  public Service getService() {
    return service;
  }

  public void setService(Service service) {
    this.service = service;
  }

  /**
   * @hibernate.set table="resource_role" cascade="save-update" outer-join="true"
   * @hibernate.collection-key column="resource_id"
   * @hibernate.collection-many-to-many class="gleam.executive.model.Role"
   *                                    column="role_id" outer-join="true"
   */
  public Set<Role> getRoles() {
    return roles;
  }

  /**
   * Adds a role for the resource
   *
   * @param role
   */
  public void addRole(Role role) {
    getRoles().add(role);
  }
  
  /**
   * @param roles
   */
  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }
  
  /**
   * @see org.acegisecurity.userdetails.UserDetails#getAuthorities()
   * 
   */
  public GrantedAuthority[] getAuthorities() {
    return (GrantedAuthority[])roles.toArray(new GrantedAuthority[0]);
  }
  
  @Override
  public String toString() {
    ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
      .append("url", this.url).append("description", this.description);
      
    GrantedAuthority[] auths = this.getAuthorities();
    if(auths != null) {
        sb.append("Granted Authorities: ");
        for(int i = 0; i < auths.length; i++) {
          if(i > 0) {
            sb.append(", ");
          }
          sb.append(auths[i].toString());
        }
    }else {
      sb.append("No Granted Authorities");
    }
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if(this == o) return true;
    if(!(o instanceof Resource)) return false;
    final Resource resource = (Resource)o;
    if(url != null ? !url.equals(resource.getUrl()) : resource
            .getUrl()!= null) return false;
    return true;
  }

  @Override
  public int hashCode() {
    return (url!= null ? url.hashCode() : 0);
  }
  
}
