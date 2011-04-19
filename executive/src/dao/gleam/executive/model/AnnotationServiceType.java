/*
 *  AnnotationServiceType.java
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
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * This class is used to generate the Hibernate mapping file.
 * 
 * <p>
 * <a href="AnnotationServiceType.java.html"><i>View Source</i></a>
 * 
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 * 
 * @struts.form include-all="true" extends="BaseForm"
 * @hibernate.class table="annotation_service_type"
 */
public class AnnotationServiceType extends BaseObject implements Serializable {

	protected Long id;

	protected String name;

	protected String data;

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
	 * @hibernate.property column="name" length="100" not-null="true"
	 *                     unique="true"
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @hibernate.property column="data" type = "text"                    
	 */

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof AnnotationServiceType))
			return false;
		final AnnotationServiceType annotationServiceType = (AnnotationServiceType) o;
		if (name != null ? !name.equals(annotationServiceType.getName())
				: annotationServiceType.getName() != null)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return (name != null ? name.hashCode() : 0);
	}

	@Override
	public String toString() {
		ToStringBuilder sb = new ToStringBuilder(this,
				ToStringStyle.DEFAULT_STYLE).append("name", this.name);
		return sb.toString();
	}

}
