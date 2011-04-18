package gleam.docservice;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.client.WebClient;

/**
 * Servlet filter that extracts the security token from each call
 * and verified it with the executive, returning a 404 status
 * code if the token is not valid.  This filter is designed to be
 * configured by Spring and registered as a DelegatingFilterProxy,
 * it should not be registered directly in web.xml under its
 * own class name.
 */
public class SecurityTokenFilter implements Filter {
  private static final Log log = LogFactory.getLog(SecurityTokenFilter.class);
  
  private static final int MAX_CACHE_SIZE = 10;
  private WebClient executiveVerifierClient;
  
  /**
   * Map from token IDs to their last access time.
   */
  private Map<String, Long> recentTokens =
    new LinkedHashMap<String, Long>(16, 0.75f, true) {
    @Override
    protected boolean removeEldestEntry(Entry<String, Long> eldest) {
      // TODO Auto-generated method stub
      return size() > MAX_CACHE_SIZE;
    }
  
  };
  
  public void setVerifierClient(WebClient client) {
    this.executiveVerifierClient = client;
  }
  
  public void init(FilterConfig conf) throws ServletException {
    // nothing to do
  }
  
  protected boolean tokenValid(String tokenId) {
    if(log.isDebugEnabled()) {
      log.debug("Checking validity of token " + tokenId);
    }
    synchronized(recentTokens) {
      Long lastTime = recentTokens.get(tokenId);
      if(lastTime != null) {
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastTime < 30000) {
          if(log.isDebugEnabled()) {
            log.debug("Token " + tokenId + " was checked within the last " +
            		"30 seconds, assuming still valid");
          }
          return true;
        } else {
          if(log.isDebugEnabled()) {
            log.debug("Token " + tokenId + " seen before, but not checked " +
            		"within the last 30 seconds");
          }
          recentTokens.remove(tokenId);
        }
      }
    }
    // cache miss - check with the executive
    Response verifierResponse = WebClient.fromClient(
            executiveVerifierClient, true).path(tokenId).get();
    if(verifierResponse.getStatus() == HttpServletResponse.SC_OK) {
      synchronized(recentTokens) {
        recentTokens.put(tokenId, System.currentTimeMillis());
      }
      if(log.isDebugEnabled()) {
        log.debug("Executive says token " + tokenId + " is valid");
      }
      return true;
    }
    else {
      log.warn("Connection attempted with invalid token " + tokenId);
      return false;
    }
  }

  public void doFilter(ServletRequest req, ServletResponse resp,
          FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpReq = (HttpServletRequest)req;
    HttpServletResponse httpResp = (HttpServletResponse)resp;
    String theToken = httpReq.getPathInfo();
    theToken = theToken.substring(theToken.lastIndexOf('/') + 1);
    if(tokenValid(theToken)) {
      chain.doFilter(req, resp);
    }
    else {
      httpResp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid security token " + theToken);
    }
  }
  
  public void destroy() {
    // TODO Auto-generated method stub
  }

}
