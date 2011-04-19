/*
 *  Department.java
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
package gleam.executive.organization.model;

import java.util.Date;

import gleam.executive.model.BaseObject;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @hibernate.class table="department"
 * @struts.form include-all="true" extends="BaseForm"
 */
public class Department extends BaseObject {
    private Long departmentId;
    private String departmentName;
    private String missionStatement;
    private Date createdDate;

    /**
     * @return Returns the id.
     * @hibernate.id column="department_id" generator-class="native" unsaved-value="null"
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * @struts.validator type="required"
     * @hibernate.property column="department_name" length="50" not-null="true"
     */
    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    /**
     * @struts.validator type="required"
     * @hibernate.property column="mission_statement" length="50" not-null="true"
     */
    public String getMissionStatement() {
        return missionStatement;
    }

    public void setMissionStatement(String missionStatement) {
        this.missionStatement = missionStatement;
    }
    
    /**
     * @struts.validator type="required"
     * @hibernate.property column="created_date" length="20" not-null="true"
     */
    public Date getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    public boolean equals(Object object) {
        if (!(object instanceof Department)) {
            return false;
        }
        Department rhs = (Department) object;
        return new EqualsBuilder().append(this.departmentName, rhs.departmentName)
                .append(this.missionStatement, rhs.missionStatement)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(1923026325, -1034774675)
                .append(this.departmentName).append(this.missionStatement)
                .toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("departmentId", this.departmentId)
                .append("missionStatement", this.missionStatement)
                .append("departmentName", this.departmentName).toString();
    }
}
