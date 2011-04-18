package gate.teamware.richui.annic;

import gate.teamware.richui.common.RichUIException;
import gate.teamware.richui.common.RichUIUtils;
import gleam.docservice.proxy.DocServiceProxy;

import java.net.URI;
import java.net.URISyntaxException;

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
   * @throws RichUIException
   */
  public DocserviceConnection(String docserviceUrlString, final String corpusId)
    throws RichUIException {
    this.docserviceUrlString = docserviceUrlString;
    this.corpusId = corpusId;

    try {
      // lets convert the docserviceURLString to a url
      // this may cause an exception if the URL is not properly formed
      URI uri = new URI(this.docserviceUrlString);
      
      this.proxy = RichUIUtils.getDocServiceProxy(uri);
    } catch(URISyntaxException e) {
      throw new RichUIException(
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

  public DocServiceProxy getDocServiceProxy() {
	  return this.proxy;
  }
  
  public String getConnectionStatus() {
    return "Connected for : " + corpusId;
  }

  public void cleanup() throws RichUIException {
	  // nothing to do
  }
}
