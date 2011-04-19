/*
 *  Project.java
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
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Implementation of Project model class
 * </p>
 *
 * <p>
 * <a href="Project.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 */


/**
 * @hibernate.class table="project"
 */
public class Project extends BaseObject implements Serializable {

	    private Long id;
	    private String name;
	    private String description;
	    private Date lastUpdate;
		private boolean enabled = false;
		private Long userId;
	    private User user;
	    private Integer version;

	   

		private byte [] data;
	    
	    /**
	     * @hibernate.property length="255"
	     */
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		
		  /**
		   * @hibernate.id column="id" generator-class="native"
		   *               unsaved-value="null"
		   */
		public Long getId() {
			return id;
		}
		
	    public void setId(Long id) {
		    this.id = id;
		}
		  
		 /**
	     * @hibernate.property length="50" not-null="true"
	     */
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		/** 
		 * @hibernate.property
		 * column="data"
		 * type = "org.springframework.orm.hibernate3.support.BlobByteArrayType" 
		 */
		public byte[] getData() {
			return data;
		}
		public void setData(byte[] data) {
			this.data = data;
		}
		
		/** 
		 * @hibernate.property
		 */
		public Date getLastUpdate() {
			return lastUpdate;
		}
		public void setLastUpdate(Date lastUpdate) {
			this.lastUpdate = lastUpdate;
		}
		

	    /**
	     * @hibernate.many-to-one insert="false" update="false" cascade="merge,persist"
	     *  column="user_id" outer-join="true"
	     */
	    public User getUser() {
	        return user;
	    }

	    public void setUser(User user) {
	        this.user = user;
	    }

	    /**
	     * @hibernate.property column="user_id" not-null="true" 
	     */
	    public Long getUserId() {
	        return userId;
	    }

	    public void setUserId(Long userId) {
	        this.userId = userId;
	    }
		
	    /**
	     * @hibernate.property column="enabled" type="yes_no"
	     */
	    public boolean isEnabled() {
	      return enabled;
	    }

	    public void setEnabled(boolean enabled) {
	      this.enabled = enabled;
	    }
	    
	    /**
	     * @hibernate.property column="version"
	     */
        public Integer getVersion() {
			return version;
		}
		public void setVersion(Integer version) {
			this.version = version;
		}


		
		 public boolean equals(Object o) {
			    if(this == o) return true;
			    if(!(o instanceof Project)) return false;
			    final Project project = (Project)o;
			    return !(name != null ? !name.equals(project.name) : project.name != null);
			  }

			  public int hashCode() {
			    return (name != null ? name.hashCode() : 0);
			  }

			  public String toString() {
			    return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(
			            this.name).toString();
			  }

			
}
