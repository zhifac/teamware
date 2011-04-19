/*
 *  Corpus.java
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
public class Corpus extends BaseObject implements Comparable, Serializable {
	/**
   * 
   */
	private static final long serialVersionUID = -7173365862837375365L;

	protected String corpusID;

	protected String corpusName;

	protected int numberOfDocuments;
	
	protected String uploader;

	public Corpus() {
	}

	/**
	 * @struts.validator type="required"
	 * @struts.validator type="mask" msgkey="corpusForm.invalidName"
	 * @struts.validator-var name="mask" value="^([a-zA-Z_0-9]*-*_*!*(\s)*)+$"
	 * @return Returns the corpusName.
	 */
	public String getCorpusName() {
		return corpusName;
	}

	/**
	 * @param corpusName
	 *            The corpusName to set.
	 */
	public void setCorpusName(String corpusName) {
		this.corpusName = corpusName;
	}

	/**
	 * @return Returns the id.
	 * @hibernate.id column="id" generator-class="increment"
	 *               unsaved-value="null"
	 */
	public String getCorpusID() {
		return corpusID;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setCorpusID(String corpusID) {
		this.corpusID = corpusID;
	}

	public int getNumberOfDocuments() {
		return numberOfDocuments;
	}

	public void setNumberOfDocuments(int numberOfDocuments) {
		this.numberOfDocuments = numberOfDocuments;
	}

	public String getUploader() {
		return uploader;
	}

	public void setUploader(String uploader) {
		this.uploader = uploader;
	}

	/**
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(Object object) {
		Corpus myClass = (Corpus) object;
		return new CompareToBuilder().append(this.corpusName.toLowerCase(),
				myClass.corpusName.toLowerCase()).append(
				this.corpusID.toLowerCase(), myClass.corpusID.toLowerCase())
				.toComparison();
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof Corpus)) {
			return false;
		}
		Corpus rhs = (Corpus) object;
		return new EqualsBuilder().append(this.corpusName, rhs.corpusName)
				.append(this.corpusID, rhs.corpusID).isEquals();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder(507894221, 2144988001).append(
				this.corpusName).append(this.corpusID).toHashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this).append("corpusID", this.corpusID)
				.append("corpusName", this.corpusName).toString();
	}
}
