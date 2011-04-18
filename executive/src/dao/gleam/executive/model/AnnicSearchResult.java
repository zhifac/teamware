package gleam.executive.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * AnnicSearchResult.java
 * 
 * Copyright (c) 1998-2006, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June 1991 (in the distribution as file licence.html, and
 * also available at http://gate.ac.uk/gate/licence.html).
 * 
 * Haotian Sun 18-Dec-2006
 * 
 * @struts.form include-all="true"
 *              extends="gleam.executive.webapp.form.BaseForm"
 */
public class AnnicSearchResult extends BaseObject implements Comparable,
                                                 Serializable {
  private static final long serialVersionUID = 1493775401319383217L;

  protected String query;

  protected String contextWindow;

  protected String documentID;

  protected String documentName;

  protected String leftContext;

  protected String pattern;

  protected String rightContext;

  protected String detailsID;

  protected String detailsTable;

  /**
   * @struts.validator type="required"
   * @return Returns the query.
   */
  public String getQuery() {
    return query;
  }

  /**
   * @param query The query to set.
   */
  public void setQuery(String query) {
    this.query = query;
  }

  /**
   * @struts.validator type="required"
   * @return Returns the contextWindow.
   */
  public String getContextWindow() {
    return contextWindow;
  }

  /**
   * @param contextWindow The contextWindow to set.
   */
  public void setContextWindow(String contextWindow) {
    this.contextWindow = contextWindow;
  }

  /**
   * @return Returns the documentID.
   * @hibernate.id column="id" generator-class="increment"
   *               unsaved-value="null"
   */
  public String getDocumentID() {
    return documentID;
  }

  /**
   * @param documentID The documentID to set.
   */
  public void setDocumentID(String documentID) {
    this.documentID = documentID;
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
   * @return Returns the leftContext.
   */
  public String getLeftContext() {
    return leftContext;
  }

  /**
   * @param leftContext The leftContext to set.
   */
  public void setLeftContext(String leftContext) {
    this.leftContext = leftContext;
  }

  /**
   * @return Returns the pattern.
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * @param pattern The pattern to set.
   */
  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  /**
   * @return Returns the rightContext.
   */
  public String getRightContext() {
    return rightContext;
  }

  /**
   * @param rightContext The rightContext to set.
   */
  public void setRightContext(String rightContext) {
    this.rightContext = rightContext;
  }

  /**
   * @return Returns the detailsID.
   */
  public String getDetailsID() {
    return detailsID;
  }

  /**
   * @param detailsID The detailsID to set.
   */
  public void setDetailsID(String detailsID) {
    this.detailsID = detailsID;
  }

  /**
   * @return Returns the detailsTable.
   */
  public String getDetailsTable() {
    return detailsTable;
  }

  /**
   * @param detailsTable The detailsTable to set.
   */
  public void setDetailsTable(String detailsTable) {
    this.detailsTable = detailsTable;
  }

  /**
   * @see java.lang.Comparable#compareTo(Object)
   */
  public int compareTo(Object object) {
    AnnicSearchResult myClass = (AnnicSearchResult)object;
    return new CompareToBuilder().append(this.contextWindow,
            myClass.contextWindow).append(this.query, myClass.query).append(
            this.documentName, myClass.documentName).append(this.detailsID,
            myClass.detailsID).append(this.leftContext, myClass.leftContext)
            .append(this.pattern, myClass.pattern).append(this.rightContext,
                    myClass.rightContext).append(this.documentID,
                    myClass.documentID).toComparison();
  }

  /**
   * @see java.lang.Object#equals(Object)
   */
  public boolean equals(Object object) {
    if(!(object instanceof AnnicSearchResult)) {
      return false;
    }
    AnnicSearchResult rhs = (AnnicSearchResult)object;
    return new EqualsBuilder().append(this.contextWindow, rhs.contextWindow)
            .append(this.query, rhs.query).append(this.documentName,
                    rhs.documentName).append(this.detailsID, rhs.detailsID)
            .append(this.leftContext, rhs.leftContext).append(this.pattern,
                    rhs.pattern).append(this.rightContext, rhs.rightContext)
            .append(this.documentID, rhs.documentID).isEquals();
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return new HashCodeBuilder(-374330847, 404817589)
            .append(this.contextWindow).append(this.query).append(
                    this.documentName).append(this.detailsID).append(
                    this.leftContext).append(this.pattern).append(
                    this.rightContext).append(this.documentID).toHashCode();
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return new ToStringBuilder(this).append("query", this.query).append(
            "rightContext", this.rightContext).append("documentName",
            this.documentName).append("detailsID", this.detailsID).append(
            "documentID", this.documentID).append("contextWindow",
            this.contextWindow).append("pattern", this.pattern).append(
            "leftContext", this.leftContext).toString();
  }
}
