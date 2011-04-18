package gleam.executive.service.callback;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * Web service interface used for callbacks to the executive by a remote
 * processor, e.g. a GaS, to signal completion of tasks.
 */
@WebService(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/executive-callback-service")
public interface ExecutiveCallbackService {
  /**
   * Callback method used to signal successful completion of a system task.
   *
   * @param taskID the task ID that identifies the completed task.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/executive-callback-service")
  public void taskFinished(@WebParam(name = "taskID") String taskID) throws ExecutiveServiceCallbackException;

  /**
   * Callback method used to signal successful completion of a user task.
   *
   * @param taskID the task ID that identifies the completed task.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/executive-callback-service")
  public void taskCompleted(@WebParam(name = "taskID") String taskID) throws ExecutiveServiceCallbackException;
  
  /**
   * Callback method used to signal saving of a user task.
   *
   * @param taskID the task ID that identifies the saved task.
 * @param lastOpened 
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/executive-callback-service")
  public void taskSaved(@WebParam(name = "taskID") String taskID) throws ExecutiveServiceCallbackException;
  
  /**
   * Callback method used to signal cancellation of a task.
   *
   * @param taskID the task ID that identifies the canceled task.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/executive-callback-service")
  public void taskCanceled(@WebParam(name = "taskID") String taskID) throws ExecutiveServiceCallbackException;


  /**
   * Callback method used to signal failure of a task.
   *
   * @param taskID the task ID that identifies the failed task.
   * @param reason human-readable string giving the reason for failure.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/executive-callback-service")
  public void taskFailed(@WebParam(name = "taskID") String taskID,
          @WebParam(name = "reason") String reason) throws ExecutiveServiceCallbackException;


  /**
   * Callback method used to get next task.
   *
   * @param actorId the performer username
   * @return  AnnotationTask with variety of properties set
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/executive-callback-service")
  public AnnotationTask getNextTask(@WebParam(name = "actorId") String actorId) throws ExecutiveServiceCallbackException;
  
  
  
  /**
   * Callback method used to explicitly accept next task.
   *
   * @param actorId the performer username
   * @return  true if there is a new task for user, otherwise false
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/executive-callback-service")
  public boolean checkForNextTask(@WebParam(name = "actorId") String actorId) throws ExecutiveServiceCallbackException;

}
