package gleam.executive.model;

import java.io.Serializable;
import java.net.URL;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * OntologyRepository.java
 * 
 * Copyright (c) 1998-2007, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June 1991 (in the distribution as file licence.html, and
 * also available at http://gate.ac.uk/gate/licence.html).
 * 
 * Haotian Sun 20-Aug-2007
 * 
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
