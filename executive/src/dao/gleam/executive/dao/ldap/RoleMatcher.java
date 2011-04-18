package gleam.executive.dao.ldap;

import java.util.Set;

public interface RoleMatcher {

	public Set<String> getMatchingRoles(String username, Set<String> adRoles);
}
