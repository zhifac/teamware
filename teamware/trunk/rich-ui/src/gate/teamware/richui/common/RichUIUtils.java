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
