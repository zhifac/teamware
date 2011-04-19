/*
 *  Document.java
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
 * Haotian Sun 12-Dec-2006
 *
 *  $Id$
 */
package gleam.executive.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @struts.form include-all="true"
 *              extends="gleam.executive.webapp.form.BaseForm"
 */
public class Document extends BaseObject implements Comparable, Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -7173365862837375365L;

  protected String documentID;

  protected String documentName;


  public Document() {
  }

  /**
   * @return Returns the documentName.
   */
  public String getDocumentName() {
    return documentName;
  }

  /**
   * @param documentName The documentName to set.
   */
  public void setDocumentName(String documentName) {
    this.documentName = documentName;
  }

  /**
   * @return Returns the id.
   * @hibernate.id column="id" generator-class="increment"
   *               unsaved-value="null"
   */
  public String getDocumentID() {
    return documentID;
  }

  /**
   * @param id The id to set.
   */
  public void setDocumentID(String documentID) {
    this.documentID = documentID;
  }

  


/**
   * @see java.lang.Comparable#compareTo(Object)
   */
  public int compareTo(Object object) {
    Document myClass = (Document)object;
    return new CompareToBuilder().append(this.documentName,
            myClass.documentName).append(this.documentID, myClass.documentID)
            .toComparison();
  }

  /**
   * @see java.lang.Object#equals(Object)
   */
  public boolean equals(Object object) {
    if(!(object instanceof Document)) {
      return false;
    }
    Document rhs = (Document)object;
    return new EqualsBuilder().append(this.documentName, rhs.documentName)
            .append(this.documentID, rhs.documentID).isEquals();
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return new HashCodeBuilder(507894221, 2144988001).append(this.documentName)
            .append(this.documentID).toHashCode();
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return new ToStringBuilder(this).append("documentID", this.documentID)
            .append("documentName", this.documentName).toString();
  }
}
