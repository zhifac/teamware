/*
 *  RichUIUtils.java
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
package gate.teamware.richui.common;

import gleam.docservice.proxy.DSProxyException;
import gleam.docservice.proxy.DocServiceProxy;
import gleam.docservice.proxy.DocServiceProxyFactory;

import java.net.URI;
public class RichUIUtils {
  private static String docServiceProxyFactoryClassname =
    "gleam.docservice.proxy.factory.impl.RobustByDefaultDocServiceProxyFactoryImpl";
  private static DocServiceProxyFactory factory;
  
  public static void setDocServiceProxyFactoryClassname(String classname) {
    docServiceProxyFactoryClassname = classname;
  }
  
  private static DocServiceProxyFactory getFactory() throws RichUIException {
    if(factory == null) {
      try {
        factory =
          (DocServiceProxyFactory)Class.forName(
            docServiceProxyFactoryClassname).newInstance();
      }
      catch(Exception e) {
        throw new RichUIException("Exception creating DocServiceProxyFactory", e);
      }
    }
    return factory;
  }

  public static synchronized DocServiceProxy getDocServiceProxy(URI docserviceURI) throws RichUIException {
    try {
      return getFactory().getDocServiceProxy(docserviceURI);
    }
    catch(DSProxyException e) {
      throw new RichUIException("Exception connecting to doc service at "
              + docserviceURI, e);
    }
  }
}
