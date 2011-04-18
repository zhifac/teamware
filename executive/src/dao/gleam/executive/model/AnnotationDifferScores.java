package gleam.executive.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * AnnotationDifferScores.java
 * 
 * Copyright (c) 1998-2007, The University of Sheffield.
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
public class AnnotationDifferScores extends BaseObject implements Comparable,
                                                      Serializable {
  private static final long serialVersionUID = 1493775401319383278L;

  protected String documentID;

  protected String documentName;

  protected String recall;

  protected String precision;

  protected String FMeasure;

  protected String correct;

  protected String partCorrect;

  protected String missing;

  protected String spurious;

  /**
   * @return Returns the correct.
   */
  public String getCorrect() {
    return correct;
  }

  /**
   * @param correct The correct to set.
   */
  public void setCorrect(String correct) {
    this.correct = correct;
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
   * @return Returns the fMeasure.
   */
  public String getFMeasure() {
    return FMeasure;
  }

  /**
   * @param measure The fMeasure to set.
   */
  public void setFMeasure(String measure) {
    FMeasure = measure;
  }

  /**
   * @return Returns the missing.
   */
  public String getMissing() {
    return missing;
  }

  /**
   * @param missing The missing to set.
   */
  public void setMissing(String missing) {
    this.missing = missing;
  }

  /**
   * @return Returns the partCorrect.
   */
  public String getPartCorrect() {
    return partCorrect;
  }

  /**
   * @param partCorrect The partCorrect to set.
   */
  public void setPartCorrect(String partCorrect) {
    this.partCorrect = partCorrect;
  }

  /**
   * @return Returns the precision.
   */
  public String getPrecision() {
    return precision;
  }

  /**
   * @param precision The precision to set.
   */
  public void setPrecision(String precision) {
    this.precision = precision;
  }

  /**
   * @return Returns the recall.
   */
  public String getRecall() {
    return recall;
  }

  /**
   * @param recall The recall to set.
   */
  public void setRecall(String recall) {
    this.recall = recall;
  }

  /**
   * @return Returns the spurious.
   */
  public String getSpurious() {
    return spurious;
  }

  /**
   * @param spurious The spurious to set.
   */
  public void setSpurious(String spurious) {
    this.spurious = spurious;
  }

  /**
   * @see java.lang.Comparable#compareTo(Object)
   */
  public int compareTo(Object object) {
    AnnotationDifferScores myClass = (AnnotationDifferScores)object;
    return new CompareToBuilder().append(this.FMeasure, myClass.FMeasure)
            .append(this.partCorrect, myClass.partCorrect).append(this.correct,
                    myClass.correct).append(this.recall, myClass.recall)
            .append(this.documentName, myClass.documentName).append(
                    this.missing, myClass.missing).append(this.spurious,
                    myClass.spurious).append(this.precision, myClass.precision)
            .append(this.documentID, myClass.documentID).toComparison();
  }

  /**
   * @see java.lang.Object#equals(Object)
   */
  public boolean equals(Object object) {
    if(!(object instanceof AnnotationDifferScores)) {
      return false;
    }
    AnnotationDifferScores rhs = (AnnotationDifferScores)object;
    return new EqualsBuilder().append(this.FMeasure, rhs.FMeasure).append(
            this.partCorrect, rhs.partCorrect)
            .append(this.correct, rhs.correct).append(this.recall, rhs.recall)
            .append(this.documentName, rhs.documentName).append(this.missing,
                    rhs.missing).append(this.spurious, rhs.spurious).append(
                    this.precision, rhs.precision).append(this.documentID,
                    rhs.documentID).isEquals();
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return new HashCodeBuilder(2013483065, -1603984223).append(this.FMeasure)
            .append(this.partCorrect).append(this.correct).append(this.recall)
            .append(this.documentName).append(this.missing).append(
                    this.spurious).append(this.precision).append(
                    this.documentID).toHashCode();
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return new ToStringBuilder(this).append("recall", this.recall).append(
            "correct", this.correct).append("missing", this.missing).append(
            "FMeasure", this.FMeasure).append("spurious", this.spurious)
            .append("documentName", this.documentName).append("precision",
                    this.precision).append("documentID", this.documentID)
            .append("partCorrect", this.partCorrect).toString();
  }
}
