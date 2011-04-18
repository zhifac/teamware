package gleam.executive.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * This class is used to generate the Struts Validator Form as well as
 * the This class is used to generate Spring Validation rules as well as
 * the Hibernate mapping file.
 *
 * <p>
 * <a href="User.java.html"><i>View Source</i></a>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 *         Updated by Dan Kibler (dan@getrolling.com) Extended to
 *         implement Acegi UserDetails interface by David Carter
 *         david@carter.net
 *
 * @struts.form include-all="true" extends="BaseForm"
 * @hibernate.class table="app_user"
 */
public class User extends BaseObject implements Serializable, UserDetails {
  private static final long serialVersionUID = 3832626162173359411L;

  protected Long id;

  protected String username; // required

  protected String password; // required
  
  protected String salt;
  
  protected int iterations;

  protected String confirmPassword;

  protected String passwordHint;

  protected String firstName; // required

  protected String lastName; // required

  protected String email; // required; unique

  protected String phoneNumber;

  protected String website;

  protected Address address = new Address();

  protected Integer version;

  protected Set<Role> roles = new HashSet<Role>();
  
  protected Set<String> roleNames = new HashSet<String>();

  protected boolean enabled;

  protected boolean accountExpired;

  protected boolean accountLocked;

  protected boolean credentialsExpired;

  public User() {
  }

  public User(String username) {
    this.username = username;
  }

  /**
   * @hibernate.id column="id" generator-class="native"
   *               unsaved-value="null"
   */
  public Long getId() {
    return id;
  }

  /**
   * @struts.validator type="required"
   * @hibernate.property length="50" not-null="true" unique="true"
   */
  public String getUsername() {
    return username;
  }

  /**
   * @struts.validator type="required"
   * @struts.validator type="twofields" msgkey="errors.twofields"
   * @struts.validator-args arg1resource="userForm.password"
   * @struts.validator-args arg1resource="userForm.confirmPassword"
   * @struts.validator-var name="secondProperty" value="confirmPassword"
   * @hibernate.property column="password" not-null="true"
   */
  public String getPassword() {
    return password;
  }
  
  /**
   * @hibernate.property column="salt"
   */
  public String getSalt() {
    return salt;
  }
  
  /**
   * @hibernate.property column="iterations"
   */
  public int getIterations() {
    return iterations;
  }

  /**
   * @struts.validator type="required"
   */
  public String getConfirmPassword() {
    return confirmPassword;
  }

  /**
   * @struts.validator type="required"
   * @hibernate.property column="password_hint" not-null="false"
   */
  public String getPasswordHint() {
    return passwordHint;
  }

  /**
   * @struts.validator type="required"
   * @hibernate.property column="first_name" not-null="true" length="50"
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * @struts.validator type="required"
   * @hibernate.property column="last_name" not-null="true" length="50"
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * @struts.validator type="required"
   * @hibernate.property name="email" not-null="true"
   */
  public String getEmail() {
    return email;
  }

  /**
   * @hibernate.property column="phone_number" not-null="false"
   */
  public String getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * @hibernate.property column="website" not-null="false"
   */
  public String getWebsite() {
    return website;
  }

  /**
   * Returns the full name.
   */
  public String getFullName() {
    return firstName + ' ' + lastName;
  }

  /**
   * @hibernate.component
   */
  public Address getAddress() {
    return address;
  }

  /**
   * @hibernate.set table="user_role" cascade="none" lazy="false"
   * @hibernate.collection-key column="user_id"
   * @hibernate.collection-many-to-many class="gleam.executive.model.Role"
   *                                    column="role_id"
   */
  public Set<Role> getRoles() {
    return roles;
  }

  /**
   * Adds a role for the user
   *
   * @param role
   */
  public void addRole(Role role) {
    getRoles().add(role);
  }

  /**
   * @see org.acegisecurity.userdetails.UserDetails#getAuthorities()
   */
  public GrantedAuthority[] getAuthorities() {
		GrantedAuthority[] authorities = new GrantedAuthority[roles.size()];
		int index = 0;
		for (Iterator<Role> it = roles.iterator(); it.hasNext(); ) {
			authorities[index] = (GrantedAuthority) it.next();
			index++;
		}
		return authorities; 
	}
  /**
   * @hibernate.version
   */
  public Integer getVersion() {
    return version;
  }

  /**
   * @hibernate.property column="account_enabled" type="yes_no"
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * @hibernate.property column="account_expired" not-null="true"
   *                     type="yes_no"
   */
  public boolean isAccountExpired() {
    return accountExpired;
  }

  /**
   * @see org.acegisecurity.userdetails.UserDetails#isAccountNonExpired()
   */
  public boolean isAccountNonExpired() {
    return !isAccountExpired();
  }

  /**
   * @hibernate.property column="account_locked" not-null="true"
   *                     type="yes_no"
   */
  public boolean isAccountLocked() {
    return accountLocked;
  }

  /**
   * @see org.acegisecurity.userdetails.UserDetails#isAccountNonLocked()
   */
  public boolean isAccountNonLocked() {
    return !isAccountLocked();
  }

  /**
   * @hibernate.property column="credentials_expired" not-null="true"
   *                     type="yes_no"
   */
  public boolean isCredentialsExpired() {
    return credentialsExpired;
  }

  /**
   * @see org.acegisecurity.userdetails.UserDetails#isCredentialsNonExpired()
   */
  public boolean isCredentialsNonExpired() {
    return !credentialsExpired;
  }
  
  public Set<String> getRoleNames() {
		return roleNames;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }
  
  public void setSalt(String salt) {
    this.salt = salt;
  }
  
  public void setIterations(int iterations) {
    this.iterations = iterations;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }

  public void setPasswordHint(String passwordHint) {
    this.passwordHint = passwordHint;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setAccountExpired(boolean accountExpired) {
    this.accountExpired = accountExpired;
  }

  public void setAccountLocked(boolean accountLocked) {
    this.accountLocked = accountLocked;
  }

  public void setCredentialsExpired(boolean credentialsExpired) {
    this.credentialsExpired = credentialsExpired;
  }

  public void setRoleNames(Set<String> roleNames) {
	this.roleNames = roleNames;
  }

  public boolean equals(Object o) {
    if(this == o) return true;
    if(!(o instanceof User)) return false;
    final User user = (User)o;
    if(username != null ? !username.equals(user.getUsername()) : user
            .getUsername() != null) return false;
    return true;
  }
  
  
  public int hashCode() {
    return (username != null ? username.hashCode() : 0);
  }

  public String toString() {
    return this.username;
  }
}
