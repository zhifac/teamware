/*
 *  OntologyRepository.java
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
 * Haotian Sun 20-Aug-2007
 *
 *  $Id$
 */
package gleam.executive.model;

import java.io.Serializable;
import java.net.URL;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @struts.form include-all="true"
 *              extends="gleam.executive.webapp.form.BaseForm"
 *             
 */

public class OntologyRepository extends BaseObject implements Comparable, Serializable{
  
  private static final long serialVersionUID = -7173365862837375367L;
  
  //Ontology Name
  protected String name;
  
  //Ontology URL
  protected URL ontologyURL;
  
  /**
   * empty constructor
   */
  public OntologyRepository(){
    
  }

  /**
   * @struts.validator type="required"
   * @struts.validator type="mask" msgkey="ontologyRepositoryForm.invalidName"
   * @struts.validator-var name="mask" value="^([a-zA-Z_0-9]*-*_*(\s)*)+$"
   * @return Returns the name.
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * 
   * @return Returns the url.
   */
  public URL getOntologyURL() {
    return ontologyURL;
  }

  public void setOntologyURL(URL url) {
    this.ontologyURL = url;
  }

  /**
   * @see java.lang.Object#equals(Object)
   */
  public boolean equals(Object object) {
    if(!(object instanceof OntologyRepository)) {
      return false;
    }
    OntologyRepository rhs = (OntologyRepository)object;
    return new EqualsBuilder().append(this.name, rhs.name).append(
            this.ontologyURL, rhs.ontologyURL).isEquals();
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return new HashCodeBuilder(507894221, 2144988001).append(this.name)
    .append(this.name).toHashCode();
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return new ToStringBuilder(this).append("name", this.name).append(
      "ontologyURL", this.ontologyURL).toString();
  }

  /**
   * @see java.lang.Comparable#compareTo(Object)
   */
  public int compareTo(Object o) {
    OntologyRepository myClass = (OntologyRepository)o;
    return new CompareToBuilder().append(this.name, myClass.name)
            .append(this.ontologyURL, myClass.ontologyURL).toComparison();
  }
  
}
