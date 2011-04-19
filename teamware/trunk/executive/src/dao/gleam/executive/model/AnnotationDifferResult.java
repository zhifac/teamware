/*
 *  AnnotationDifferResult.java
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
 * Haotian Sun 18-Dec-2006
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
public class AnnotationDifferResult extends BaseObject implements Comparable,
                                                      Serializable {
  private static final long serialVersionUID = 1493775401319383219L;

  protected String documentID;

  protected String documentName;

  protected String keyAnnoSetName;

  protected String resAnnoSetName;

  protected String annoType;

  protected Long startKey;

  protected Long endKey;

  protected String keyString;

  protected String keyFeature;

  protected String markString;

  protected Long startRes;

  protected Long endRes;

  protected String resString;

  protected String resFeature;

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
   * @struts.validator type="required"
   * @return Returns the annoType.
   */
  public String getAnnoType() {
    return annoType;
  }

  /**
   * @param annoType The annoType to set.
   */
  public void setAnnoType(String annoType) {
    this.annoType = annoType;
  }

  /**
   * @return Returns the endKey.
   */
  public Long getEndKey() {
    return endKey;
  }

  /**
   * @param endKey The endKey to set.
   */
  public void setEndKey(Long endKey) {
    this.endKey = endKey;
  }

  /**
   * @return Returns the endRes.
   */
  public Long getEndRes() {
    return endRes;
  }

  /**
   * @param endRes The endRes to set.
   */
  public void setEndRes(Long endRes) {
    this.endRes = endRes;
  }

  /**
   * @return Returns the keyAnnoSetName.
   */
  public String getKeyAnnoSetName() {
    return keyAnnoSetName;
  }

  /**
   * @param keyAnnoSetName The keyAnnoSetName to set.
   */
  public void setKeyAnnoSetName(String keyAnnoSetName) {
    this.keyAnnoSetName = keyAnnoSetName;
  }

  /**
   * @return Returns the keyFeature.
   */
  public String getKeyFeature() {
    return keyFeature;
  }

  /**
   * @param keyFeature The keyFeature to set.
   */
  public void setKeyFeature(String keyFeature) {
    this.keyFeature = keyFeature;
  }

  /**
   * @return Returns the keyString.
   */
  public String getKeyString() {
    return keyString;
  }

  /**
   * @param keyString The keyString to set.
   */
  public void setKeyString(String keyString) {
    this.keyString = keyString;
  }

  /**
   * @return Returns the markString.
   */
  public String getMarkString() {
    return markString;
  }

  /**
   * @param markString The markString to set.
   */
  public void setMarkString(String markString) {
    this.markString = markString;
  }

  /**
   * @return Returns the resAnnoSetName.
   */
  public String getResAnnoSetName() {
    return resAnnoSetName;
  }

  /**
   * @param resAnnoSetName The resAnnoSetName to set.
   */
  public void setResAnnoSetName(String resAnnoSetName) {
    this.resAnnoSetName = resAnnoSetName;
  }

  /**
   * @return Returns the resFeature.
   */
  public String getResFeature() {
    return resFeature;
  }

  /**
   * @param resFeature The resFeature to set.
   */
  public void setResFeature(String resFeature) {
    this.resFeature = resFeature;
  }

  /**
   * @return Returns the resString.
   */
  public String getResString() {
    return resString;
  }

  /**
   * @param resString The resString to set.
   */
  public void setResString(String resString) {
    this.resString = resString;
  }

  /**
   * @return Returns the startKey.
   */
  public Long getStartKey() {
    return startKey;
  }

  /**
   * @param startKey The startKey to set.
   */
  public void setStartKey(Long startKey) {
    this.startKey = startKey;
  }

  /**
   * @return Returns the startRes.
   */
  public Long getStartRes() {
    return startRes;
  }

  /**
   * @param startRes The startRes to set.
   */
  public void setStartRes(Long startRes) {
    this.startRes = startRes;
  }

  /**
   * @see java.lang.Comparable#compareTo(Object)
   */
  public int compareTo(Object object) {
    AnnotationDifferResult myClass = (AnnotationDifferResult)object;
    return new CompareToBuilder().append(this.resAnnoSetName,
            myClass.resAnnoSetName).append(this.resFeature, myClass.resFeature)
            .append(this.startRes, myClass.startRes).append(this.keyFeature,
                    myClass.keyFeature).append(this.endRes, myClass.endRes)
            .append(this.markString, myClass.markString).append(this.endKey,
                    myClass.endKey).append(this.resString, myClass.resString)
            .append(this.documentID, myClass.documentID).append(this.annoType,
                    myClass.annoType).append(this.documentName,
                    myClass.documentName).append(this.startKey,
                    myClass.startKey).append(this.keyString, myClass.keyString)
            .append(this.keyAnnoSetName, myClass.keyAnnoSetName).toComparison();
  }

  /**
   * @see java.lang.Object#equals(Object)
   */
  public boolean equals(Object object) {
    if(!(object instanceof AnnotationDifferResult)) {
      return false;
    }
    AnnotationDifferResult rhs = (AnnotationDifferResult)object;
    return new EqualsBuilder().append(this.resAnnoSetName, rhs.resAnnoSetName)
            .append(this.resFeature, rhs.resFeature).append(this.startRes,
                    rhs.startRes).append(this.keyFeature, rhs.keyFeature)
            .append(this.endRes, rhs.endRes).append(this.markString,
                    rhs.markString).append(this.endKey, rhs.endKey).append(
                    this.resString, rhs.resString).append(this.documentID,
                    rhs.documentID).append(this.annoType, rhs.annoType).append(
                    this.documentName, rhs.documentName).append(this.startKey,
                    rhs.startKey).append(this.keyString, rhs.keyString).append(
                    this.keyAnnoSetName, rhs.keyAnnoSetName).isEquals();
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return new HashCodeBuilder(-776122507, -1409805149).append(
            this.resAnnoSetName).append(this.resFeature).append(this.startRes)
            .append(this.keyFeature).append(this.endRes)
            .append(this.markString).append(this.endKey).append(this.resString)
            .append(this.documentID).append(this.annoType).append(
                    this.documentName).append(this.startKey).append(
                    this.keyString).append(this.keyAnnoSetName).toHashCode();
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return new ToStringBuilder(this).append("resFeature", this.resFeature)
            .append("startKey", this.startKey).append("endKey", this.endKey)
            .append("keyFeature", this.keyFeature).append("startRes",
                    this.startRes).append("annoType", this.annoType).append(
                    "markString", this.markString).append("documentName",
                    this.documentName).append("keyString", this.keyString)
            .append("keyAnnoSetName", this.keyAnnoSetName).append(
                    "resAnnoSetName", this.resAnnoSetName).append("documentID",
                    this.documentID).append("endRes", this.endRes).append(
                    "resString", this.resString).toString();
  }
}
