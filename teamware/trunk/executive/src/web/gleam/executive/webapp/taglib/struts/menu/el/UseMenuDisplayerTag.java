package gleam.executive.webapp.taglib.struts.menu.el;

import net.sf.navigator.displayer.MenuDisplayer;
import org.apache.taglibs.standard.tag.common.fmt.BundleSupport;

import gleam.executive.model.WebAppBean;
import gleam.executive.service.RoleManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import javax.servlet.jsp.tagext.Tag;
import net.sf.navigator.displayer.MenuDisplayerMapping;
import net.sf.navigator.displayer.MessageResourcesMenuDisplayer;
import net.sf.navigator.menu.MenuRepository;
import net.sf.navigator.menu.PermissionsAdapter;
import net.sf.navigator.menu.RolesPermissionsAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This tag acts the same as net.sf.navigator.taglib.UseMenuDisplayerTag, except
 * that it allows JSTL Expressions in all it's attributes.
 *
 * @author Matt Raible Modified by Haotian Sun
 * @version $Revision: 1.5 $ $Date: 2006/03/02 08:04:33 $
 * 
 * @jsp.tag name="useMenuDisplayer" bodycontent="JSP"
 */
public class UseMenuDisplayerTag  extends TagSupport {

    private static final long serialVersionUID = 1L;
    private String name;
    private String bundle;
    private String config = MenuDisplayer.DEFAULT_CONFIG;
    private String locale;
    private String permissions;
    private String repository;
    
    private static Log log = LogFactory.getLog(UseMenuDisplayerTag.class);
    public static final String PRIVATE_REPOSITORY = "net.sf.navigator.repositoryKey";
    public static final String DISPLAYER_KEY = "net.sf.navigator.taglib.DISPLAYER";
    public static final String ROLES_ADAPTER = "rolesAdapter";
    public static final String MENUS_ADAPTER = "menusAdapter";
    public static final String MENU_ID = "net.sf.navigator.MENU_ID";
    
    protected static ResourceBundle messages =
            ResourceBundle.getBundle("gleam.executive.webapp.taglib.struts.menu.el.LocalStrings");

    protected MenuDisplayer menuDisplayer;
    protected String localeKey;
    protected String bundleKey;
    protected String id;
    protected ResourceBundle rb; // used to allow setting of ResourceBundle 
                                 // from JSTL in EL tag
    /**
     * @jsp.attribute required="false" rtexprvalue="true"
     */
    public void setId(String id){
      this.id=id;
    }
    /**
     * @jsp.attribute required="true" rtexprvalue="true"
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * 
     * @param bundle
     * @jsp.attribute required="false" rtexprvalue="true"
     */
    public void setBundle(String bundle) {
        this.bundle = bundle;
    }
    
    /**
     * 
     * @param config
     * @jsp.attribute required="false" rtexprvalue="true"
     */
    public void setConfig(String config) {
        this.config = config;
    }

    /**
     * 
     * @param locale
     * @jsp.attribute required="false" rtexprvalue="true"
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    /**
     * 
     * @param permissions
     * @jsp.attribute required="false" rtexprvalue="true"
     */
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
    
    /**
     * 
     * @param key
     * @jsp.attribute required="false" rtexprvalue="true"
     */
    public void setRepository(String key) {
        this.repository = key;
    }

    public UseMenuDisplayerTag() {
        super();
        init();
    }

    private void init() {
        name = null;
        bundle = null;
        config = null;
        locale = null;
        permissions = null;
        repository = null;
    }

    private void evaluateExpressions() throws JspException {
        ExpressionEvaluator eval = new ExpressionEvaluator(this, pageContext);

        if (name != null) {
            setName(eval.evalString("name", name));
        }

        if (bundle != null) {
            setBundle(eval.evalString("bundle", bundle));
        }

        if (config != null) {
            setConfig(eval.evalString("config", config));
        }

        if (locale != null) {
            setLocale(eval.evalString("locale", locale));
        }

        if (permissions != null) {
            setPermissions(eval.evalString("permissions", permissions));
        }

        if (repository != null) {
            setRepository(eval.evalString("repository", repository));
        }
    }
    
    public String getBundle() {
      return bundleKey;
    }

    public String getConfig() {
      return config;
    }


    public String getLocale() {
      return localeKey;
    }

    public String getName() {
      return name;
    }

    public String getRepository() {
      return repository;
    }

    /** Getter for property permissions.
     * @return Value of property permissions.
     */
    public String getPermissions() {
      return this.permissions;
    }

    public int doStartTag() throws JspException {
      evaluateExpressions();

      // Default to JSTL Bundle to use (for EL Tag)
      Tag tag = findAncestorWithClass(this, BundleSupport.class);

      if (tag != null) {
        BundleSupport parent = (BundleSupport) tag;
        rb = parent.getLocalizationContext().getResourceBundle();
      } else {
        // check for the localizationContext in applicationScope, set in web.xml
        LocalizationContext localization =
            BundleSupport.getLocalizationContext(pageContext);

        if (localization != null) {
            rb = localization.getResourceBundle();
        }
      }
      if (repository == null) {
          repository = MenuRepository.MENU_REPOSITORY_KEY;
      }


      // get the menu repository
      MenuRepository rep =
          (MenuRepository) pageContext.findAttribute(this.repository);

      if (rep == null) {
          throw new JspException(messages.getString("menurepository.not.found"));
      } else {
          // set repository as a pageContext variable so that DisplayMenuTag
          // can grab it.
          pageContext.setAttribute(PRIVATE_REPOSITORY, rep);
      }

      // get the displayer mapping
      MenuDisplayerMapping displayerMapping =
              rep.getMenuDisplayerMapping(this.name);

      if (displayerMapping == null) {
          throw new JspException(messages.getString("displayer.mapping.not.found"));
      }

      PermissionsAdapter permissions = getPermissionsAdapter();

      //get an instance of the menu displayer
      MenuDisplayer displayerInstance;

      try {
          displayerInstance =
                  (MenuDisplayer) Class.forName(displayerMapping.getType())
                  .newInstance();
          menuDisplayer = displayerInstance;
          // default to use the config on the mapping
          if (displayerMapping.getConfig() != null) {
              // this value (config) is set on the displayer below
              setConfig(displayerMapping.getConfig());
          }
      } catch (Exception e) {
          throw new JspException(e.getMessage());
      }
      
      if (bundleKey == null) {
          this.bundleKey = "org.apache.struts.action.MESSAGE";
      }
      
      // setup the displayerInstance
      // if the displayer is a MessageResourceMenuDisplayer
      // and a bundle is specified, then pass it the bundle (message resources) and
      // the locale
      if ((bundleKey != null && !"".equals(bundleKey)) &&
              (displayerInstance instanceof MessageResourcesMenuDisplayer)) {
          MessageResourcesMenuDisplayer mrDisplayerInstance =
                  (MessageResourcesMenuDisplayer) displayerInstance;
          Locale locale;

          if (localeKey == null) {
              // default to Struts locale
              locale = 
                  (Locale) pageContext.findAttribute("org.apache.struts.action.LOCALE");
              if (locale == null) {
                  locale = pageContext.getRequest().getLocale();
              }
          } else {
              locale = (Locale) pageContext.findAttribute(localeKey);
          }
          mrDisplayerInstance.setLocale(locale);
          
          if (rb != null) {
              mrDisplayerInstance.setMessageResources(rb);
          } else {
              Object resources = pageContext.findAttribute(bundleKey);
              
              if (resources == null) {
                  // try a simple ResourceBundle
                  try {
                      rb = ResourceBundle.getBundle(bundleKey, locale);
                      mrDisplayerInstance.setMessageResources(rb);
                  } catch (MissingResourceException mre) {
                      log.error(mre.getMessage());
                  }
              } else {
                   mrDisplayerInstance.setMessageResources(resources);   
              }
          }
      }

      displayerInstance.setConfig(config);
      if (id != null) {
          pageContext.setAttribute("menuId", id);
      }

      displayerInstance.init(pageContext, displayerMapping);
      displayerInstance.setPermissionsAdapter(permissions);

      pageContext.setAttribute(DISPLAYER_KEY, displayerInstance);

      return (EVAL_BODY_INCLUDE);
    }

    protected PermissionsAdapter getPermissionsAdapter()
          throws JspException {
      PermissionsAdapter adapter = null;

      if (permissions != null) {
          // If set to "rolesAdapter", then create automatically
          if (permissions.equalsIgnoreCase(ROLES_ADAPTER)) {
              adapter =
                  new RolesPermissionsAdapter((HttpServletRequest) pageContext.getRequest());
          }else if(permissions.equalsIgnoreCase(MENUS_ADAPTER)) {
            ApplicationContext ctx = WebApplicationContextUtils
                                .getRequiredWebApplicationContext(pageContext.getServletContext());
            RoleManager roleManager=(RoleManager)ctx.getBean("roleManager");
            WebAppBean webapp = (WebAppBean)ctx.getBean("webAppBean");
              adapter =
                  new ExecutiveMenuAdapter((HttpServletRequest) pageContext.getRequest(),roleManager,webapp.getName());
          }else {
              adapter =
                  (PermissionsAdapter) pageContext.findAttribute(permissions);

              if (adapter == null) {
                  throw new JspException(messages.getString("permissions.not.found"));
              }
          }
      }

      return adapter;
    }

    public int doEndTag() throws JspException {
      menuDisplayer.end(pageContext);
      pageContext.removeAttribute(DISPLAYER_KEY);
      pageContext.removeAttribute(PRIVATE_REPOSITORY);
      return (EVAL_PAGE);
    }

    public void release() {
      if (log.isDebugEnabled()) {
          log.debug("release() called");
      }

      this.menuDisplayer = null;
      this.bundleKey = null;
      this.config = MenuDisplayer.DEFAULT_CONFIG;
      this.localeKey = null;
      this.name = null;
      this.menuDisplayer = null;
      this.repository = null;
      this.permissions = null;
      this.rb = null;
      init();
    }
}
