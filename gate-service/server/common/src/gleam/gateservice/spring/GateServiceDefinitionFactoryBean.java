/*
 *  GateServiceDefinitionFactoryBean.java
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
package gleam.gateservice.spring;

import gleam.gateservice.definition.GateServiceDefinition;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;

/**
 * Spring factory bean to create a GateServiceDefinition from an XML
 * file.
 */
public class GateServiceDefinitionFactoryBean implements FactoryBean {

  /**
   * Spring resource pointing to the XML file.
   */
  private Resource xmlFile;

  public void setXmlFile(Resource file) {
    this.xmlFile = file;
  }

  public Resource getXmlFile() {
    return xmlFile;
  }

  /**
   * Constructs a {@link GateServiceDefinition} from the configured XML
   * file.
   */
  public Object getObject() throws Exception {
    if(xmlFile == null) {
      throw new IllegalArgumentException("No xmlFile specified");
    }

    SAXBuilder builder = new SAXBuilder();
    Document doc = builder.build(xmlFile.getURL());

    GateServiceDefinition def = new GateServiceDefinition();
    def.fromXml(doc.getRootElement());

    return def;
  }

  public Class getObjectType() {
    return GateServiceDefinition.class;
  }

  public boolean isSingleton() {
    return true;
  }

}
