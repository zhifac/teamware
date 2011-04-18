/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
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
