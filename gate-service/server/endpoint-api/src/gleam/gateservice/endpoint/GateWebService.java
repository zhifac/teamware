/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.gateservice.endpoint;

import java.net.URI;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * Service endpoint interface for a GaS, to be exposed as a web service.
 */
@WebService(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/gate-service")
public interface GateWebService {
  /**
   * Get the list of required parameter names supported by this GaS.
   * 
   * @return an array of parameter names.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/gate-service")
  public List<String> getRequiredParameterNames();

  /**
   * Get the list of optional parameter names supported by this GaS.
   * 
   * @return an array of parameter names.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/gate-service")
  public List<String> getOptionalParameterNames();

  /**
   * Get the list of annotation set names required by this service as
   * input. Note that one of the entries in the array may be
   * <code>null</code> if the service takes input from the default
   * annotation set.
   * 
   * @return an array of annotation set names, one of which may be
   *         <code>null</code>.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/gate-service")
  public List<String> getInputAnnotationSetNames();

  /**
   * Get the list of annotation set names output by this service. Note
   * that one of the entries in the array may be <code>null</code> if
   * the service provides output to the default annotation set. Also
   * note that the same annotation set may be named as both an input and
   * an output set.
   * 
   * @return an array of annotation set names, one of which may be
   *           <code>null</code>.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/gate-service")
  public List<String> getOutputAnnotationSetNames();

  /**
   * Instruct the service to begin processing a document in a remote doc
   * service. Note that this operation returns immediately - the
   * processing takes place asynchronously and success or failure is
   * communicated to the GLEAM executive.
   * 
   * @param executiveLocation URL of the executive web service endpoint
   *          that is responsible for this task.
   * @param taskId the task ID assigned by the executive.
   * @param docServiceLocation URL of the document service in which the
   *          document to be processed resides.
   * @param docId ID of the document within the doc service.
   * @param annotationSets mappings defining which annotation sets on
   *          the document in the doc service should be mapped to which
   *          annotation sets on the document presented to the GATE
   *          application by the GaS. This allows the same GaS to
   *          process results from, for example, several different human
   *          annotators.
   * @param parameterValues values for the GaS parameters. Processing
   *          will fail if any required parameters are not specified
   *          here.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/gate-service")
  public void processRemoteDocument(
          @WebParam(name = "executiveLocation") URI executiveLocation,
          @WebParam(name = "taskId") String taskId,
          @WebParam(name = "docServiceLocation") URI docServiceLocation,
          @WebParam(name = "docId") String docId,
          @WebParam(name = "annotationSets")
          List<AnnotationSetMapping> annotationSets,
          @WebParam(name = "parameterValues")
          List<ParameterValue> parameterValues) throws GateWebServiceFault;
  
  /**
   * Instruct the service to begin processing a set of documents in a
   * remote doc service. Note that this operation returns immediately
   * - the processing takes place asynchronously and success or
   * failure is communicated to the GLEAM executive.
   * 
   * <p><b>NOTE</b> you probably don't want to use this method.
   * In most cases it is far better for both speed and memory
   * consumption reasons to call {@link #processRemoteDocument}
   * several times with one document at a time, rather than using this
   * method.  This <code>processRemoteDocuments</code> method uses a single
   * worker to load all the documents into a corpus, process the corpus
   * and then save the results. Multiple <code>processRemoteDocument</code>
   * (singular) calls can farm out the documents to multiple workers,
   * and only require each worker to hold one document in memory at a time.
   * The <code>processRemoteDocuments</code> (plural) method is <b>only</b>
   * suitable for cases where the set of documents must be processed as a
   * unit by a single worker, e.g. when learning an ML model.</p>
   * 
   * @param executiveLocation URL of the executive web service endpoint
   *          that is responsible for this task.
   * @param taskId the task ID assigned by the executive.
   * @param docServiceLocation URL of the document service in which the
   *          document to be processed resides.
   * @param tasks the set of annotation tasks to be performed. Each task
   *          encapsulates a document ID and a set of annotation set
   *          mappings, see {@link #processRemoteDocument} for details.
   * @param parameterValues values for the GaS parameters. Processing
   *          will fail if any required parameters are not specified
   *          here.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/gate-service")
  public void processRemoteDocuments(
          @WebParam(name = "executiveLocation") URI executiveLocation,
          @WebParam(name = "taskId") String taskId,
          @WebParam(name = "docServiceLocation") URI docServiceLocation,
          @WebParam(name = "tasks")
          List<AnnotationTask> tasks,
          @WebParam(name = "parameterValues")
          List<ParameterValue> parameterValues) throws GateWebServiceFault;

  /**
   * Process a single document, returning the modified annotation sets.
   * 
   * @param documentXml the document to process, in GATE XML format.
   * @param encoding the encoding used for the XML.
   * @param parameterValues values for the GaS parameters. A
   *          {@link GateWebServiceFault} will be thrown if not all
   *          required parameters are specified.
   * @return the modified annotation sets in GATE XML format.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/gate-service")
  public List<AnnotationSetData> processDocument(
          @WebParam(name = "documentXml") byte[] documentXml,
          @WebParam(name = "encoding") String encoding,
          @WebParam(name = "parameterValues")
          List<ParameterValue> parameterValues) throws GateWebServiceFault;
}
