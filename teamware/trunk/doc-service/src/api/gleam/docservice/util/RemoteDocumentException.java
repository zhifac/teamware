package gleam.docservice.util;

public class RemoteDocumentException extends Exception {
  public RemoteDocumentException() {
    super();
  }
  
  public RemoteDocumentException(String message) {
    super(message);
  }

  public RemoteDocumentException(Throwable cause) {
    super(cause);
  }

  public RemoteDocumentException(String message, Throwable cause) {
    super(message, cause);
  }

}
