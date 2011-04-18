public String[] getResourceRoles() {
        gleam.executive.model.Role role;
        String[] resourceRoles = new String[roles.size()];
        int i = 0;
        for (java.util.Iterator iter = roles.iterator(); iter.hasNext();) {
          role = (gleam.executive.model.Role) iter.next();
          resourceRoles[i] = role.getName();
          i++;
      }
      return resourceRoles;
}

    /**
     * Note that this is not used - it's just needed by Struts.  If you look
     * in UserAction - you'll see that request.getParameterValues("resourceRoles")
     * is used instead.
     * 
     * @param roles
     */
    public void setResourceRoles(String[] roles) {}
    
