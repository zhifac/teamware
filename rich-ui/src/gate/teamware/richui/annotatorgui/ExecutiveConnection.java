package gate.teamware.richui.annotatorgui;

import gate.Document;
import gate.teamware.richui.common.RichUIException;
import gleam.executive.proxy.ExecutiveProxy;
import gleam.executive.proxy.ExecutiveProxyException;
import gleam.executive.proxy.ExecutiveProxyFactory;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.SwingUtilities;

public class ExecutiveConnection extends Connection implements Constants {
  private String annotatorPoolUrlString;
  private String userId;
  private String userPassword;
  private ExecutiveProxy executiveProxy;
  private AnnotatorTask annotatorTask;
  private List listeners = new ArrayList();
  private boolean readyToAcceptNewTask = true;

  private ExecutiveConnection() {
  }

  public ExecutiveConnection(String annotatorPoolUrlString, String userId,
    String userPassword) throws RichUIException {
    this.annotatorPoolUrlString = annotatorPoolUrlString;
    this.userId = userId;
    this.userPassword = userPassword;
    try {
      URI uri = new URI(this.annotatorPoolUrlString);
      if(AnnotatorGUI.isDebug())
        System.out.println("DEBUG: Creating Executive Proxy Factory \""
          + AnnotatorGUI.getProperties().getProperty(
            EXECUTIVE_PROXY_FACTORY_PARAMETER_NAME) + "\"...");
      ExecutiveProxyFactory ef =
        (ExecutiveProxyFactory)Class.forName(
          AnnotatorGUI.getProperties().getProperty(
            EXECUTIVE_PROXY_FACTORY_PARAMETER_NAME)).newInstance();
      this.executiveProxy = ef.getExecutiveProxy(uri);
    } catch(Exception e) {
      throw new RichUIException(
        "An error occured while connecting to Executive Service at:\n"
          + this.annotatorPoolUrlString + "\n\n" + e.getMessage(), e);
    }
  }

  public ExecutiveProxy getExecutiveProxy() {
    return this.executiveProxy;
  }

  public String getExecutiveUrlString() {
    return this.annotatorPoolUrlString;
  }

  public String getUserId() {
    return this.userId;
  }

  public String getUserPassword() {
    return userPassword;
  }

  public AnnotatorTask getCurrentAnnotatorTask() {
    return this.annotatorTask;
  }

  public Document getDocument() {
    if(this.annotatorTask == null) return null;
    return this.annotatorTask.getDocument();
  }

  public boolean isDocumentAnnotationsModified() {
    return (annotatorTask == null) ? false : annotatorTask.isAsModified();
  }

  public String getConnectionStatus() {
    String serviceLocation = annotatorPoolUrlString;
    try {
      URL url = new URL(serviceLocation);
      serviceLocation = url.getHost() + url.getPath();
    } catch(MalformedURLException e) {
    }
    return "Connected to POOL mode: " + getUserId();
  }

  public AnnotatorTask getNextAnnotatorTask() throws RichUIException {
    try {
      gleam.executive.proxy.AnnotatorTask executiveTask =
        getExecutiveProxy().getNextTask(userId);
      if(executiveTask == null) return null;
      AnnotatorGUI.loadPlugins(AnnotatorGUI.BASE_PLUGIN_NAME + "," + 
    		  executiveTask.getPluginCSVList(), true);
      AnnotatorGUI.loadAnnotationSchemas(executiveTask
              .getAnnotationSchemasCSVURLs(), true);
      AnnotatorTask annTask = new AnnotatorTask(this, executiveTask);
      setReadyToAcceptNewTask(true);
      return annTask;
    } catch(ExecutiveProxyException e) {
      throw new RichUIException(
        "An error occured while getting annotator task at:\n"
          + getExecutiveUrlString() + "\n\n" + e.getMessage(), e);
    }
  }



  public boolean checkForNextTask() throws RichUIException {
	  boolean flag = false;
	  try {
	        flag = getExecutiveProxy().checkForNextTask(userId);
	        setReadyToAcceptNewTask(!flag);

	    } catch(ExecutiveProxyException e) {
	      throw new RichUIException(
	        "An error occured while accepting annotator task at:\n"
	          + getExecutiveUrlString() + "\n\n" + e.getMessage(), e);
	    }
	    return flag;
	  }

  public void setAnnotatorTask(AnnotatorTask annotatorTask)
    throws RichUIException {
    if(this.annotatorTask != null) this.annotatorTask.releaseLock();
    this.annotatorTask = annotatorTask;
    fireTaskChanged();
  }

  public void saveDocument() throws RichUIException {
    if(this.annotatorTask == null) return;
    this.annotatorTask.saveAnnotationSet(true);
  }

  public boolean finishTask() throws RichUIException {
    if(annotatorTask == null) return false;
    return this.annotatorTask.finish();
  }

  public boolean cancelTask() throws RichUIException {
    if(annotatorTask == null) return false;
    return this.annotatorTask.cancel();
  }

  public void cleanup() throws RichUIException {
    if(annotatorTask != null) {
      this.annotatorTask.cleanup();
    }
    listeners.clear();
  }

  public String getTaskStatus() {
    if(this.annotatorTask != null) {
      String serviceLocation =
        this.annotatorTask.getTask().getDocServiceLocation().toString();
      try {
        URL url = new URL(serviceLocation);
        serviceLocation = url.getHost() + url.getPath();
      } catch(MalformedURLException e) {
      }
      return (annotatorTask.isAsModified() ? "* " : "") + "Task:  "
        + annotatorTask.getTask().getTaskID()
        // + " (pri: " + annotatorTask.getTask().getPriority() + ") "
        + "     Document: " + annotatorTask.getDocument().getName();
    } else {
      return "No task is loaded. ";
    }
  }

  /**
   * Returns HTML representation for task.
   *
   * @return HTML representation of the task
   */
  public String getTaskDetails() {
    StringBuffer sb = new StringBuffer();
    sb.append("<html><body>");
    if(annotatorTask != null) {
      sb.append("<b>Task ID: </b>" + annotatorTask.getTask().getTaskID()
        + "<br>");
      // sb.append("<b>Priority: </b>" + annotatorTask.getTask().getPriority() +
      // "<br>");
      sb.append("<b>Document ID: </b>"
        + annotatorTask.getTask().getDocumentID() + "<br>");
      sb.append("<b>Located at: </b>"
        + annotatorTask.getTask().getDocServiceLocation() + "<br>");
      sb.append("<b>Lock ID: </b>" + annotatorTask.getTaskId() + "<br>");
    } else {
      sb.append("No any tasks");
    }
    sb.append("</body></html>");
    return sb.toString();
  }

  private void fireTaskChanged() {
    if(listeners.size() == 0) return;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Object[] lsnrs = listeners.toArray();
        for(int i = 0; i < lsnrs.length; i++) {
          AnnotatorTaskListener l = (AnnotatorTaskListener)lsnrs[i];
          l.taskChanged(annotatorTask);
        }
      }
    });
  }

  /**
   * Adds given listener to the list of listeners if this listener doesn't
   * exists.
   *
   * @param lsnr
   *          listener to add
   */
  public boolean addConnectionListener(AnnotatorTaskListener lsnr) {
    if(listeners.contains(lsnr)) return false;
    listeners.add(lsnr);
    return true;
  }

  /**
   * Removes given listener from the list of listeners if this listener exists.
   *
   * @param lsnr
   *          listener to remove
   */
  public boolean removeConnectionListener(AnnotatorTaskListener lsnr) {
    if(listeners.contains(lsnr)) {
      listeners.remove(lsnr);
      return true;
    }
    return false;
  }

public boolean isReadyToAcceptNewTask() {
	return readyToAcceptNewTask;
}

public void setReadyToAcceptNewTask(boolean readyToAcceptNewTask) {
	this.readyToAcceptNewTask = readyToAcceptNewTask;
}
}
