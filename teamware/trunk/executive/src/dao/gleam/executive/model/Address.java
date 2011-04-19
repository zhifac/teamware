/*
 *  Address.java
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
 * This class is used to represent an address.
 * </p>
 * 
 * <p>
 * <a href="Address.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 * 
 * @struts.form include-all="true" extends="BaseForm"
 */
public class Address extends BaseObject implements Serializable {
  private static final long serialVersionUID = 3617859655330969141L;

  protected String address;

  protected String city;

  protected String province;

  protected String country;

  protected String postalCode;

  /**
   * @hibernate.property column="address" not-null="false" length="150"
   */
  public String getAddress() {
    return address;
  }

  /**
   * @hibernate.property column="city" not-null="false" length="50"
   */
  public String getCity() {
    return city;
  }

  /**
   * @hibernate.property column="province" length="100"
   */
  public String getProvince() {
    return province;
  }

  /**
   * @hibernate.property column="country" length="100"
   */
  public String getCountry() {
    return country;
  }

  /**
   * @hibernate.property column="postal_code" not-null="false"
   *                     length="15"
   */
  public String getPostalCode() {
    return postalCode;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public boolean equals(Object o) {
    if(this == o) return true;
    if(!(o instanceof Address)) return false;
    final Address address1 = (Address)o;
    if(address != null
            ? !address.equals(address1.address)
            : address1.address != null) return false;
    if(city != null ? !city.equals(address1.city) : address1.city != null)
      return false;
    if(country != null
            ? !country.equals(address1.country)
            : address1.country != null) return false;
    if(postalCode != null
            ? !postalCode.equals(address1.postalCode)
            : address1.postalCode != null) return false;
    if(province != null
            ? !province.equals(address1.province)
            : address1.province != null) return false;
    return true;
  }

  public int hashCode() {
    int result;
    result = (address != null ? address.hashCode() : 0);
    result = 29 * result + (city != null ? city.hashCode() : 0);
    result = 29 * result + (province != null ? province.hashCode() : 0);
    result = 29 * result + (country != null ? country.hashCode() : 0);
    result = 29 * result + (postalCode != null ? postalCode.hashCode() : 0);
    return result;
  }

  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append(
            "country", this.country).append("address", this.address).append(
            "province", this.province).append("postalCode", this.postalCode)
            .append("city", this.city).toString();
  }
}
