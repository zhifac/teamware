/*
 *  DocServiceDetailManagerImpl.java
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
