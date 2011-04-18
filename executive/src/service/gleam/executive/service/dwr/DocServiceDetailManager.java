package gleam.executive.service.dwr;

import gleam.executive.service.SafeManagerException;
import gleam.executive.service.DocServiceManager;
import java.util.List;

public interface DocServiceDetailManager {
  public void setDocServiceManager(DocServiceManager docManager);

  public List listAnnotationSetNames(String docID) throws SafeManagerException;

  public List listSharedAnnotationTypes(String docID, String...asNames) throws SafeManagerException;

  public List listCorpora() throws SafeManagerException;
}
