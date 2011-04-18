package gleam.executive.service.data;

import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.intercept.web.AbstractFilterInvocationDefinitionSource;
import org.acegisecurity.intercept.web.FilterInvocationDefinition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;


/**
* Maintains a <code>List</code> of <code>ConfigAttributeDefinition</code>s associated with different HTTP request
* URL Apache Ant path-based patterns.<p>Apache Ant path expressions are used to match a HTTP request URL against a
* <code>ConfigAttributeDefinition</code>.</p>
* <p>The order of registering the Ant paths using the {@link #addSecureUrl(String,ConfigAttributeDefinition)} is
* very important. The system will identify the <b>first</b>  matching path for a given HTTP URL. It will not proceed
* to evaluate later paths if a match has already been found. Accordingly, the most specific paths should be
* registered first, with the most general paths registered last.</p>
* <p>If no registered paths match the HTTP URL, <code>null</code> is returned.</p>
*
* @author Ben Alex
* @version $Id$
*/
public class SAFEFilterInvocationDefinitionMap extends AbstractFilterInvocationDefinitionSource
       implements FilterInvocationDefinition {
   //~ Static fields/initializers =====================================================================================

   private static final Log logger = LogFactory.getLog(SAFEFilterInvocationDefinitionMap.class);

   //~ Instance fields ================================================================================================

   private List requestMap = new Vector();
   private PathMatcher pathMatcher = new AntPathMatcher();
   private boolean convertUrlToLowercaseBeforeComparison = false;

   //~ Methods ========================================================================================================

   public void addSecureUrl(String antPath, ConfigAttributeDefinition attr) {
       // SEC-501: If using lower case comparison, we should convert the paths to lower case
       // as any upper case characters included by mistake will prevent the URL from ever being matched.
       if (convertUrlToLowercaseBeforeComparison) {
           antPath = antPath.toLowerCase();
       }

       requestMap.add(new EntryHolder(antPath, attr));

       if (logger.isDebugEnabled() && antPath.contains("downloadCorpus")) {
           logger.debug("Added Ant path: " + antPath + "; attributes: " + attr);
       }

   }

   public Iterator getConfigAttributeDefinitions() {
       Set set = new HashSet();
       Iterator iter = requestMap.iterator();

       while (iter.hasNext()) {
           EntryHolder entryHolder = (EntryHolder) iter.next();
           set.add(entryHolder.getConfigAttributeDefinition());
       }

       return set.iterator();
   }

   public int getMapSize() {
       return this.requestMap.size();
   }

   public boolean isConvertUrlToLowercaseBeforeComparison() {
       return convertUrlToLowercaseBeforeComparison;
   }

   public ConfigAttributeDefinition lookupAttributes(String url) {
       /*
	   // Strip anything after a question mark symbol, as per SEC-161. See also SEC-321
       int firstQuestionMarkIndex = url.indexOf("?");

       if (firstQuestionMarkIndex != -1) {
           url = url.substring(0, firstQuestionMarkIndex);
       }
       */
       if (isConvertUrlToLowercaseBeforeComparison()) {
           url = url.toLowerCase();
/*
           if (logger.isDebugEnabled()) {
               logger.debug("Converted URL to lowercase, from: '" + url + "'; to: '" + url + "'");
           }
           */
       }

       Iterator iter = requestMap.iterator();

       while (iter.hasNext()) {
           EntryHolder entryHolder = (EntryHolder) iter.next();

           boolean matched = pathMatcher.match(entryHolder.getAntPath(), url);

           if (logger.isDebugEnabled() && url.contains("downloadCorpus")) {
               logger.debug("Candidate is: '" + url + "'; pattern is " + entryHolder.getAntPath() + "; matched="
                       + matched);
           }

           if (matched) {
               return entryHolder.getConfigAttributeDefinition();
           }
       }

       return null;
   }

   public void setConvertUrlToLowercaseBeforeComparison(boolean convertUrlToLowercaseBeforeComparison) {
       this.convertUrlToLowercaseBeforeComparison = convertUrlToLowercaseBeforeComparison;
   }

   //~ Inner Classes ==================================================================================================

   protected class EntryHolder {
       private ConfigAttributeDefinition configAttributeDefinition;
       private String antPath;

       public EntryHolder(String antPath, ConfigAttributeDefinition attr) {
           this.antPath = antPath;
           this.configAttributeDefinition = attr;
       }

       protected EntryHolder() {
           throw new IllegalArgumentException("Cannot use default constructor");
       }

       public String getAntPath() {
           return antPath;
       }

       public ConfigAttributeDefinition getConfigAttributeDefinition() {
           return configAttributeDefinition;
       }
   }
}
