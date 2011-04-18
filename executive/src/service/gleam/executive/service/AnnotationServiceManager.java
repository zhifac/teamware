package gleam.executive.service;

import gleam.executive.model.AnnotationSchema;
import gleam.executive.model.AnnotationService;
import gleam.executive.model.AnnotationServiceType;

import java.io.InputStream;
import java.util.List;

/*
 *  AnnotationServiceManager.java
 *
 *  Copyright (c) 1998-2008, The University of Sheffield.
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 * <p>
 * <a href="AnnotationServiceManager.java.html"><i>View Source</i></a>
 * </p>
 *
 *  @author <a href="agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 */
public interface AnnotationServiceManager {

	  /**
	   * The method to publish schema in schemas dir, e.g (${user.home}/safe/...)
	   * @param inputStream of uploaded file
	   * @param destPath absolute path on FS
	   * @throws SafeManagerException
	   */
	  public void publishSchema(InputStream inputStream, String destinationPath) throws SafeManagerException;


	  /**
	   * The method to list all schema in schemas dir
	   * @return List of absoulute file paths on FS
	   * @throws SafeManagerException
	   */
	  public List<AnnotationSchema> listSchemas() throws SafeManagerException;


	  /**
	   * The method to delete schema
	   * @param sourcePath absolute path on FS FS where schemas are stored (${user.home}/safe/...
	   * @throws SafeManagerException
	   */
	  public void deleteSchema(String sourcePath) throws SafeManagerException;


	  /**
	   * The method to obtain location of schemas dir on FS
	   * @returns sourcePath absolute path on FS FS where schemas are stored (${user.home}/safe/...
	   * @throws SafeManagerException
	   */
	  public String getSchemasDirPath();
	  
	  
	  /**
	   * Gets AnnotationService information based on annotation Service id.
	   *
	   * @param annotationServiceId the annotationService id
	   * @return populated AnnotationService object
	   */
	  public AnnotationService getAnnotationService(Long annotationServiceId) throws SafeManagerException;

	  
	  /**
	   * Gets AnnotationService information based on annotation Service name.
	   *
	   * @param annotationServiceName the annotationService name
	   * @return populated AnnotationService object
	   */
	  public AnnotationService getAnnotationServiceByName(String name) throws SafeManagerException;
	  
	  /**
	   * Gets a list of Annotation Services based on parameters passed in.
	   *
	   * @return List populated list of Annotation Services
	   */
	  public List getAnnotationServices(AnnotationService annotationService) throws SafeManagerException;

	  /**
	   * 
	   * @return a list of all the Annotation Services in DB.
	   */
	  public List getAnnotationServices() throws SafeManagerException;
	  
	  /**
	   * 
	   * @return a list of all the Annotation Service Types in DB.
	   */
	  public List getAnnotationServiceTypes() throws SafeManagerException;
	  
	  /**
	   * Saves a Annotation Service
	   *
	   * @param annotationService the object to be saved
	   */
	  public void saveAnnotationService(AnnotationService annotationService) throws SafeManagerException;

	  /**
	   * Saves a Annotation Service Type
	   *
	   * @param annotationServiceType the object to be saved
	   */
	  public void saveAnnotationServiceType(AnnotationServiceType annotationServiceType) throws SafeManagerException;

	  /**
	   * Removes a Annotation Service from the database by id
	   *
	   * @param annotationServiceId the Annotation Service id
	   */
	  public void removeAnnotationService(Long annotationServiceId) throws SafeManagerException;

	  
	  /**
	   * Removes a Annotation Service Type from the database by id
	   *
	   * @param annotationServiceTypeId the Annotation Service type id
	   */
	  public void removeAnnotationServiceType(Long annotationServiceTypeId) throws SafeManagerException;
	 
	  /**
	   * Fetches the relevant Annotation Service Type from the database with specified Annotation Service Type Id
	   *
	   * @param resourceName
	   *          the resource name
	   * @return List of services
	   */
	  public AnnotationServiceType getAnnotationServiceType(Long annotationServiceTypeId) throws SafeManagerException;
	  
	  /**
	   * Fetches the relevant Annotation Service Type from the database with specified Annotation Service Type Id
	   *
	   * @param name
	   *          the annotation Service Type name
	   * @return List of services types
	   */
	  public AnnotationServiceType getAnnotationServiceTypeByName(String name) throws SafeManagerException ;
	  
	  /**
	   * Fetches the relevant Annotation Service Type from the database with specified Annotation Service
	   *
	   * @param resourceName
	   *          the resource name
	   * @return List of services
	   */
	  public AnnotationServiceType getAnnotationServiceTypeForAnnotationService(Long annotationServiceId) throws SafeManagerException;
	  
	  /**
	   * Fetches the Annotation Services from the database with specified service type id
	   *
	   * @param annotationServiceTypeId
	   * @return List of Annotation Services
	   */
	  public List getAnnotationServicesWithType(Long annotationServiceTypeId) throws SafeManagerException;

}

