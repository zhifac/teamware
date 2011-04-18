package gleam.annic;

import gleam.docservice.DocService;
import gleam.util.cxf.CXFClientUtils;

import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Utility class, that provides various static methods.
 * @author niraj
 *
 */
public class AnnicGUIUtils {
  private static final boolean USE_CHUNKED_ENCODING = false;

  private static final boolean USE_COMPRESSION = true;

  /**
   * Get the proxy factory bean. Creates one if it has not been either
   * injected (advanced users only) or already created.
   * 
   * @return
   */
  private static JaxWsProxyFactoryBean getFactoryBean() {
    JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

    Map<String, Object> props = new HashMap<String, Object>();
    props.put("mtom-enabled", Boolean.TRUE);
    factory.setProperties(props);
    JAXBDataBinding db = new JAXBDataBinding();
    // add IAA classes to data binding
    db.setExtraClass(new Class[]{
      gleam.docservice.iaa.AllWaysFMeasureDetail.class,
      gleam.docservice.iaa.AllWaysKappaDetail.class,
      gleam.docservice.iaa.PairwiseFMeasureDetail.class,
      gleam.docservice.iaa.PairwiseKappaDetail.class});
    factory.setDataBinding(db);
    factory.setServiceClass(DocService.class);

    return factory;
  }

  /**
   * Get a properly-configured SerialDocService stub.
   * @param docserviceURL - url of the docservice.
   */
  public static synchronized DocService getDocServiceStub(URL docserviceURL)
          throws AnnicGUIExeption {
    JaxWsProxyFactoryBean factoryBean = getFactoryBean();

    factoryBean.setAddress(docserviceURL.toExternalForm());
    DocService sds = (DocService)factoryBean.create();

    if(USE_COMPRESSION) {
      CXFClientUtils.configureForCompression(sds);
    }
    if(!USE_CHUNKED_ENCODING) {
      CXFClientUtils.setAllowChunking(sds, false);
    }

    return sds;
  }

}
