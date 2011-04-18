package gleam.executive.service.dwr.impl;

import java.util.List;
import gleam.executive.service.DocServiceManager;
import gleam.executive.service.SafeManagerException;
import gleam.executive.service.dwr.DocServiceDetailManager;

public class DocServiceDetailManagerImpl implements DocServiceDetailManager {
  private DocServiceManager docManager;

  public void setDocServiceManager(DocServiceManager docManager) {
    this.docManager = docManager;
  }

  public List listAnnotationSetNames(String docID) throws SafeManagerException {
    return getDocManager().listAnnotationSetNames(docID);
  }

  public List listSharedAnnotationTypes(String docID, String...asNames) throws SafeManagerException {
    return getDocManager().listSharedAnnotationTypes(docID, asNames);
  }
  
  public List listCorpora() throws SafeManagerException {
	    return getDocManager().listCorpora();
	  }

  /**
   * @return Returns the docManager.
   */
  public DocServiceManager getDocManager() {
    return docManager;
  }

  /**
   * @param docManager The docManager to set.
   */
  public void setDocManager(DocServiceManager docManager) {
    this.docManager = docManager;
  }
}
