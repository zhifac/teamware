package gleam.executive.service.data;

import gleam.executive.model.Resource;
import gleam.executive.model.Role;

import java.util.Iterator;
import java.util.List;
import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.intercept.web.PathBasedFilterInvocationDefinitionMap;
import org.acegisecurity.SecurityConfig;
import org.acegisecurity.intercept.web.FilterInvocationDefinitionSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gleam.executive.service.ResourceManager;

public class ResourceDefinitionSource implements FilterInvocationDefinitionSource{

	protected final Log log = LogFactory.getLog(getClass());
  //define an ObjectDefinitionSource, which is actually the protected resources.
  private SAFEFilterInvocationDefinitionMap delegate =null;

  /**
   * Creates a proxy to a PathBasedFilterInvocationDefinitionMap to delegate calls.
   * Initializations of the delegate is done here.
   *
   * @param authManager DAO handling URL/auth mappings
   * @throws DAOException
   */

  public ResourceDefinitionSource(ResourceManager manager) {


      delegate = new SAFEFilterInvocationDefinitionMap();

      // all URLs should be converted to lowercase before mapping
      delegate.setConvertUrlToLowercaseBeforeComparison( true );

      // read from XML and populate ConfigAttributeDefinitions in delegate
      List resources = manager.getResources();
      String allURL="";
      String passwordHintURL="/passwordHint.html*";

      List rolesPasswordHintUrl = manager.getRolesWithResource(passwordHintURL);
      if( rolesPasswordHintUrl.size() != 0 ) {
        log.debug("add the roles for passwordHintURL "+passwordHintURL);
        ConfigAttributeDefinition defn = //Holds a group of ConfigAttributs that are associated with a given secure object target.
                      new ConfigAttributeDefinition();
        for( int j = 0; j < rolesPasswordHintUrl.size(); j++ ) {
          Role role =(Role)rolesPasswordHintUrl.get(j);
          defn.addConfigAttribute( new SecurityConfig(role.getName()));
          log.debug("Here is the "+(j+1)+" role("+role.getName()+ ") that is related to the resource :"+passwordHintURL);
        }
        delegate.addSecureUrl(passwordHintURL, defn );//map a secured URL with several roles
      }

      for( int i = 0; i < resources.size(); i++ ) {
        Resource resource=(Resource)resources.get(i);
        log.debug("The "+i+"th resource "+resource.getUrl());
        if(!resource.getUrl().equals("/**/*.html*")){
          if(!resource.getUrl().equals("/passwordHint.html*")){
            List roles = manager.getRolesWithResource(resource.getUrl());
            // if there are roles registered for this resource
            if( roles.size() != 0 ) {
              ConfigAttributeDefinition defn = //Holds a group of ConfigAttributs that are associated with a given secure object target.
                          new ConfigAttributeDefinition();
              for( int j = 0; j < roles.size(); j++ ) {
                Role role =(Role)roles.get(j);
                defn.addConfigAttribute( new SecurityConfig(role.getName()));
                log.debug("Here is the "+(j+1)+" role("+role.getName()+ ") that is related to the resource :"+resource.getUrl());
              }
              delegate.addSecureUrl(resource.getUrl(), defn );//map a secured URL with several roles
              log.debug("Successfully add a map of URL and related roles");
            }
          }else{
            passwordHintURL=resource.getUrl();
          }
        }else{
          allURL=resource.getUrl();
        }
      }

      List rolesAllUrl = manager.getRolesWithResource(allURL);
      if( rolesAllUrl.size() != 0 ) {
    	log.debug("add the roles for allURL "+allURL);
        ConfigAttributeDefinition defn = //Holds a group of ConfigAttributs that are associated with a given secure object target.
                      new ConfigAttributeDefinition();
        for( int j = 0; j < rolesAllUrl.size(); j++ ) {
          Role role =(Role)rolesAllUrl.get(j);
          defn.addConfigAttribute( new SecurityConfig(role.getName()));
       log.debug("Here is the "+(j+1)+" role("+role.getName()+ ") that is related to the resource :"+allURL);
        }
        delegate.addSecureUrl(allURL, defn );//map a secured URL with several roles
      }

  }

  /**
   * @return Returns the delegate.
   */
  public SAFEFilterInvocationDefinitionMap getDelegate() {
    return delegate;
  }


  /**
   * @param delegate The delegate to set.
   */
  public void setDelegate(SAFEFilterInvocationDefinitionMap delegate) {
    this.delegate = delegate;
  }

  public ConfigAttributeDefinition getAttributes(Object arg0) throws IllegalArgumentException {
    // TODO Auto-generated method stub
    return delegate.getAttributes(arg0);
  }

  public Iterator getConfigAttributeDefinitions() {
    // TODO Auto-generated method stub
    return delegate.getConfigAttributeDefinitions();
  }

  public boolean supports(Class arg0) {
    // TODO Auto-generated method stub
    return delegate.supports(arg0);
  }
}

