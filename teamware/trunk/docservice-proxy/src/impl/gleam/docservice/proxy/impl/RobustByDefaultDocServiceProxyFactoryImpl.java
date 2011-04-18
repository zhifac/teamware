package gleam.docservice.proxy.impl;

/**
 * Simple subclass of {@link DocServiceProxyFactoryImpl} that enables
 * keepalives and sets an infinite timeout by default.
 */
public class RobustByDefaultDocServiceProxyFactoryImpl
                                                      extends
                                                        DocServiceProxyFactoryImpl {

  public RobustByDefaultDocServiceProxyFactoryImpl() {
    super();
    setKeepaliveInterval(30);
    setTimeout(0);
  }
  
}
