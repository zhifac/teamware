/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2007 The University of Sheffield
 * 
 * $Id$
 */
package gleam.util.cxf;

import javax.xml.ws.BindingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.transport.http.gzip.*;

/**
 * This class contains utility methods to work with CXF client stub
 * objects.
 */
public class CXFClientUtils {

  private static final Log log = LogFactory.getLog(CXFClientUtils.class);

  /**
   * Set whether or not this stub is allowed to use chunked HTTP. Does
   * nothing if the object passed is not actually a CXF stub (e.g. if
   * we're talking directly to an implementation object), or if it is
   * but does not use HTTP transport.
   */
  public static void setAllowChunking(Object cxfStub, boolean value) {
    HTTPClientPolicy policy = getClientPolicy(cxfStub);

    if(policy != null) {
      policy.setAllowChunking(value);
    }
  }

  /**
   * Set the HTTP connection and receive timeouts for this stub. Does
   * nothing if the object passed is not actually a CXF stub (e.g. if
   * we're talking directly to an implementation object), or if it is
   * but does not use HTTP transport.
   */
  public static void setTimeout(Object cxfStub, long timeout) {
    HTTPClientPolicy policy = getClientPolicy(cxfStub);

    if(policy != null) {
      policy.setReceiveTimeout(timeout);
      policy.setConnectionTimeout(timeout);
    }
  }

  /**
   * Set whether or not this stub should accept and use HTTP session
   * cookies from the server to maintain an HTTP session across multiple
   * requests. This should only be done in cases where the service
   * requires it, e.g. the ontology service. Does nothing if the object
   * passed is not actually a CXF stub (e.g. if we're talking directly
   * to an implementation object).
   */
  public static void setMaintainSession(Object cxfStub, boolean maintain) {
    if(cxfStub instanceof BindingProvider) {
      ((BindingProvider)cxfStub).getRequestContext().put(
              BindingProvider.SESSION_MAINTAIN_PROPERTY,
              Boolean.valueOf(maintain));
    }
  }

  /**
   * Configure the given stub to compress its HTTP requests and to be
   * able to handle compressed responses. Does nothing if the object
   * passed is not actually a CXF stub (e.g. if we're talking directly
   * to an implementation object), or if it is but does not use HTTP
   * transport.
   */
  public static void configureForCompression(Object cxfStub) {
    HTTPClientPolicy policy = getClientPolicy(cxfStub);

    if(policy != null) {
      policy.setAcceptEncoding("gzip");
      GZIPOutInterceptor outInterceptor = new GZIPOutInterceptor();
      GZIPInInterceptor inInterceptor = new GZIPInInterceptor();
      Client client = ClientProxy.getClient(cxfStub);
      client.getInInterceptors().add(inInterceptor);
      client.getInFaultInterceptors().add(inInterceptor);
      client.getOutInterceptors().add(outInterceptor);
    }
  }

  /**
   * Get the CXF client policy for the given stub object. Returns
   * <code>null</code> if the object passed is not actually a CXF stub
   * (e.g. if we're talking directly to an implementation object), or if
   * it is but does not use HTTP transport.
   */
  public static HTTPClientPolicy getClientPolicy(Object cxfStub) {
    try {
      Client client = ClientProxy.getClient(cxfStub);
      Conduit conduit = client.getConduit();
      if(conduit instanceof HTTPConduit) {
        HTTPClientPolicy policy = ((HTTPConduit)conduit).getClient();
        if(policy == null) {
          log.debug("HTTP conduit has no client policy, creating one");
          policy = new HTTPClientPolicy();
          ((HTTPConduit)conduit).setClient(policy);
        }
        return policy;
      }
      else {
        log.debug("Stub is not an HTTP-based service, ignoring");

      }
    }
    catch(RuntimeException rx) {
      log.debug("Exception getting client policy, "
              + "maybe the object is not a CXF stub?");
    }

    return null;
  }

}
