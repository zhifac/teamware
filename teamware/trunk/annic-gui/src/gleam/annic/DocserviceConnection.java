/*
 *  DocserviceConnection.java
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
import gleam.docservice.proxy.DocServiceProxy;
import gleam.docservice.proxy.impl.DocServiceProxyImpl;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A Class that provides methods to get connected with the doc service.
 * @author niraj
 *
 */
public class DocserviceConnection extends Connection implements Constants {
  
  /**
   * Doc-service URL
   */
  private String docserviceUrlString;
  
  /**
   * Instance of the Doc Service stub.
   */
  private DocService serialDocService;
  
  /**
   * Corpus ID
   */
  private String corpusId;
  
  /**
   * Object of the DocServiceProxy
   */
  private DocServiceProxy proxy;
  
  /**
   * Constructor
   * @param docserviceUrlString
   * @param corpusId
   * @throws AnnicGUIExeption
   */
  public DocserviceConnection(String docserviceUrlString, final String corpusId)
    throws AnnicGUIExeption {
    this.docserviceUrlString = docserviceUrlString;
    this.corpusId = corpusId;

    try {
      // lets convert the docserviceURLString to a url
      // this may cause an exception if the URL is not properly formed
      URL url = new URL(this.docserviceUrlString);
      
      // so the URL is alright, lets get an object of the serail doc service
      this.serialDocService = AnnicGUIUtils.getDocServiceStub(url);
      
      // and we obtain a docServiceProxy which internally uses the serialDocService 
      this.proxy = new DocServiceProxyImpl(this.serialDocService);
    } catch(MalformedURLException e) {
      throw new AnnicGUIExeption(
        "An error occured while connecting to Document Service at:\n"
          + this.docserviceUrlString + "\n\n" + e.getMessage(), e);
    }
  }

  public String getDocserviceUrlString() {
    return docserviceUrlString;
  }

  public String getCorpusId() {
    return corpusId;
  }

  public DocService getSerialDocService() {
    return serialDocService;
  }

  public DocServiceProxy getDocServiceProxy() {
	  return this.proxy;
  }
  
  public String getConnectionStatus() {
    return "Connected for : " + corpusId;
  }

  public void cleanup() throws AnnicGUIExeption {
	  // nothing to do
  }
}
