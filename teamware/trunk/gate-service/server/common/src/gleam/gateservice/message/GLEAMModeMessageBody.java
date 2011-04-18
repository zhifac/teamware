/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.gateservice.message;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Request message body for a GLEAM-mode call.
 */
public class GLEAMModeMessageBody extends RequestMessageBody {
  static final long serialVersionUID = -5547585043764294322L;

  /**
   * URL to the executive web service endpoint which is to receive
   * notification of the success or failure of this call.
   */
  protected URI executiveLocation;

  /**
   * URL to the doc service WS endpoint in which the document to process
   * resides.
   */
  protected URI docServiceLocation;

  /**
   * Task ID to pass to the executive.
   */
  protected String taskID;

  /**
   * Annotation tasks to perform.
   */
  protected List<Task> tasks = new ArrayList<Task>();

  /**
   * Class representing a single annotation task.
   */
  public static class Task implements Serializable {
    static final long serialVersionUID = 4140932148199253907L;

    /**
     * ID of the document to process.
     */
    protected String docID;

    /**
     * Mappings from GaS input annotation set names to annotation sets
     * in the doc service. The map may contain <code>null</code> as
     * one of its keys and/or values, indicating the unnamed annotation
     * set.
     */
    protected Map<String, String> annotationSetMappings =
            new HashMap<String, String>();

    public Task(String docID) {
      this.docID = docID;
    }

    public Map<String, String> getAnnotationSetMappings() {
      return annotationSetMappings;
    }

    public void setAnnotationSetMappings(
            Map<String, String> annotationSetMappings) {
      this.annotationSetMappings = annotationSetMappings;
    }

    public void addAnnotationSetMapping(String gateServiceASName,
            String docServiceASName) {
      annotationSetMappings.put(gateServiceASName, docServiceASName);
    }

    public String getDocID() {
      return docID;
    }

    public void setDocID(String docID) {
      this.docID = docID;
    }

    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("docId = ");
      sb.append(docID);
      sb.append(", annotationSetMappings = ");
      sb.append(annotationSetMappings);
      return sb.toString();
    }
  }

  public GLEAMModeMessageBody(URI executiveLocation, String taskID,
          URI docServiceLocation) {
    super();
    this.executiveLocation = executiveLocation;
    this.docServiceLocation = docServiceLocation;
    this.taskID = taskID;
  }

  public URI getDocServiceLocation() {
    return docServiceLocation;
  }

  public void setDocServiceLocation(URI docServiceLocation) {
    this.docServiceLocation = docServiceLocation;
  }

  public URI getExecutiveLocation() {
    return executiveLocation;
  }

  public void setExecutiveLocation(URI executiveLocation) {
    this.executiveLocation = executiveLocation;
  }

  public String getTaskID() {
    return taskID;
  }

  public void setTaskID(String taskID) {
    this.taskID = taskID;
  }

  /**
   * Create and return a new annotation task, adding it to our internal
   * list of tasks.
   * 
   * @param docID the document ID for the new task
   */
  public Task createTask(String docID) {
    Task newTask = new Task(docID);
    this.tasks.add(newTask);
    return newTask;
  }
  
  public List<Task> getTasks() {
    return this.tasks;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder(super.toString());
    sb.append(", executiveLocation = ");
    sb.append(executiveLocation);
    sb.append(", taskID = ");
    sb.append(taskID);
    sb.append(", docServiceLocation = ");
    sb.append(docServiceLocation);
    sb.append(", tasks = ");
    sb.append(tasks);

    return sb.toString();
  }

}
