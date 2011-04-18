package gleam.docservice.util;

public interface RemoteDocumentListener {
  void documentModified();
  void documentClosed();
  void documentSynchronized();
}
