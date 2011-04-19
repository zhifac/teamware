/*
 *  AnnicGUIUtils.java
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
