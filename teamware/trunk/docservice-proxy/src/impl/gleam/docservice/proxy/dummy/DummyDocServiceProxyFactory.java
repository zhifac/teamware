/*
 *  DummyDocServiceProxyFactory.java
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
package gleam.docservice.proxy.dummy;

import java.net.URI;

import gleam.docservice.proxy.DocServiceProxy;
import gleam.docservice.proxy.DocServiceProxyFactory;
import gleam.util.logging.TraceWrapper;

/**
 * Dummy proxy factory that always returns the same proxy. If no proxy
 * has been configured via {@link #setProxy} then a dummy proxy is
 * returned.
 */
public class DummyDocServiceProxyFactory implements DocServiceProxyFactory {

  private DocServiceProxy proxy;

  private DocServiceProxy realProxy;

  private boolean traceCalls = false;

  private boolean traceMethodParams = false;

  private boolean traceExceptions = false;
  

  /**
   * HTTP timeout for web service calls.
   */
  private long timeout = -1;

  public long getTimeout() {
    return timeout;
  }

  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  public void setTraceCalls(boolean trace) {
    this.traceCalls = trace;
  }

  public void setTraceMethodParameters(boolean trace) {
    this.traceMethodParams = trace;
  }

  public void setTraceExceptions(boolean trace) {
    this.traceExceptions = trace;
  }

  public void setProxy(DocServiceProxy realProxy) {
    this.realProxy = realProxy;
    this.proxy = null;
  }

  /**
   * Always returns the same dummy DocServiceProxy.
   */
  public DocServiceProxy getDocServiceProxy(URI docServiceLocation) {
    if(proxy == null) {
      if(realProxy == null) {
        realProxy = new DummyDocServiceProxy();
      }
      if(traceCalls) {
        proxy = TraceWrapper.getTraceWrapper(realProxy, DocServiceProxy.class,
                traceMethodParams, traceExceptions);
      }
      else {
        proxy = realProxy;
      }
    }
    return proxy;
  }

}
