package gleam.executive.dao;

import java.util.List;

import gleam.executive.model.AnnotationService;
import gleam.executive.model.AnnotationServiceType;


/**
 * Annotation Service Data Access Object (Dao) interface.
 *
 * <p>
 * <a href="AnnotationServiceDao.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 */
public interface AnnotationServiceDao extends Dao {
  /**
   * Gets AnnotationService information based on annotation Service id.
   *
   * @param annotationServiceId the annotationService id
   * @return populated AnnotationService object
   */
  public AnnotationService getAnnotationService(Long annotationServiceId);
  
  /**
   * Gets AnnotationService information based on annotation Service name.
   *
   * @param annotationServiceName the annotationService name
   * @return populated AnnotationService object
   */
  public AnnotationService getAnnotationServiceByName(String name);

  /**
   * Gets a list of Annotation Services based on parameters passed in.
   *
   * @return List populated list of Annotation Services
   */
  public List getAnnotationServices(AnnotationService annotationService);

  /**
   * 
   * @return a list of all the Annotation Services in DB.
   */
  public List getAnnotationServices();
  
  /**
   * 
   * @return a list of all the Annotation Service Types in DB.
   */
  public List getAnnotationServiceTypes();
  
  /**
   * Saves a Annotation Service
   *
   * @param annotationService the object to be saved
   */
  public void saveAnnotationService(AnnotationService annotationService);

  /**
   * Saves a Annotation Service Type
   *
   * @param annotationServiceType the object to be saved
   */
  public void saveAnnotationServiceType(AnnotationServiceType annotationServiceType);

  /**
   * Removes a Annotation Service from the database by id
   *
   * @param annotationServiceId the Annotation Service id
   */
  public void removeAnnotationService(Long annotationServiceId);

  
  /**
   * Removes a Annotation Service Type from the database by id
   *
   * @param annotationServiceTypeId the Annotation Service type id
   */
  public void removeAnnotationServiceType(Long annotationServiceTypeId);
 
  /**
   * Fetches the relevant Annotation Service Type from the database with specified Annotation Service Type Id
   *
   * @param annotationServiceTypeId
   *          the annotation Service Type Id
   * @return List of services types
   */
  public AnnotationServiceType getAnnotationServiceType(Long annotationServiceTypeId);
  
  /**
   * Fetches the relevant Annotation Service Type from the database with specified Annotation Service Type Id
   *
   * @param name
   *          the annotation Service Type name
   * @return List of services types
   */
  public AnnotationServiceType getAnnotationServiceTypeByName(String name);
  
  
  /**
   * Fetches the relevant Annotation Service Type from the database with specified Annotation Service
   *
   * @param resourceName
   *          the resource name
   * @return List of services
   */
  public AnnotationServiceType getAnnotationServiceTypeForAnnotationService(Long annotationServiceId);
  
  /**
   * Fetches the Annotation Services from the database with specified service type id
   *
   * @param annotationServiceTypeId
   * @return List of Annotation Services
   */
  public List getAnnotationServicesWithType(Long annotationServiceTypeId);

}
