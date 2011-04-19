/*
 *  PersonPlus.java
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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @hibernate.class table="person"
 * @struts.form include-all="true" extends="BaseForm"
 */
public class PersonPlus extends BaseObject {
    private Long personId;
    private String firstName;
    private String lastName;

    /**
     * @return Returns the id.
     * @hibernate.id column="person_id"
     *  generator-class="increment" unsaved-value="null"
     */
    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    /**
     * @struts.validator type="required"
     * @hibernate.property column="first_name" length="50" not-null="true"
     */
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @struts.validator type="required"
     * @hibernate.property column="last_name" length="50" not-null="true"
     */
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean equals(Object object) {
        if (!(object instanceof PersonPlus)) {
            return false;
        }
        PersonPlus rhs = (PersonPlus) object;
        return new EqualsBuilder().append(this.firstName, rhs.firstName)
                .append(this.lastName, rhs.lastName)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(1923026325, -1034774675)
                .append(this.firstName).append(this.lastName)
                .toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("personId", this.personId)
                .append("lastName", this.lastName)
                .append("firstName", this.firstName).toString();
    }
}
