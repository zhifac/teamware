/*
 *  AnnotationServiceManagerImpl.java
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
 * Milan Agatonovic
 *
 *  $Id$
 */
package gleam.executive.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gleam.executive.dao.AnnotationServiceDao;
import gleam.executive.model.AnnotationSchema;
import gleam.executive.model.AnnotationService;
import gleam.executive.model.AnnotationServiceType;
import gleam.executive.service.AnnotationServiceManager;
import gleam.executive.service.SafeManagerException;
import gleam.executive.util.FileUtil;

public class AnnotationServiceManagerImpl extends BaseManager implements
		AnnotationServiceManager {
	private String schemasDirPath;
	private AnnotationServiceDao annotationServiceDao;

	public AnnotationServiceManagerImpl() {
	}

	public AnnotationServiceManagerImpl(String schemasDirPath) {
		this.schemasDirPath = schemasDirPath;
	}

	/**
	 * @see gleam.executive.service.AnnotationServiceManager#publishSchema(java.io.InputStream,
	 *      String)
	 */
	public void publishSchema(InputStream inputStream, String path)
			throws SafeManagerException {
		try {
			FileUtil.redirectInputStream(inputStream, path);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}

	/**
	 * @see gleam.executive.service.AnnotationServiceManager#listSchemas()
	 */
	public List<AnnotationSchema> listSchemas() throws SafeManagerException {

		List<AnnotationSchema> files = new ArrayList<AnnotationSchema>();
		try {
			List fileNames = FileUtil.listFiles(getSchemasDirPath());
			Iterator it = fileNames.iterator();
			while (it.hasNext()) {
				AnnotationSchema asf = new AnnotationSchema();
				String name = (String) it.next();
				asf.setName(name);
				files.add(asf);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
		return files;
	}

	/**
	 * @see gleam.executive.service.AnnotationServiceManager#deleteSchema(String)
	 */
	public void deleteSchema(String sourcePath) throws SafeManagerException {
		FileUtil.delete(sourcePath);
	}

	/**
	 * @see gleam.executive.service.AnnotationServiceManager#getAnnotationService(Long)
	 */
	public AnnotationService getAnnotationService(Long annotationServiceId)
			throws SafeManagerException {
		try {
			return annotationServiceDao.getAnnotationService(annotationServiceId);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}
	
	/**
	 * @see gleam.executive.service.AnnotationServiceManager#getAnnotationServiceByName(String)
	 */
	public AnnotationService getAnnotationServiceByName(String name)
			throws SafeManagerException {
		try {
			return annotationServiceDao.getAnnotationServiceByName(name);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}

	/**
	 * @see gleam.executive.service.AnnotationServiceManager#getAnnotationServiceType(Long)
	 */
	public AnnotationServiceType getAnnotationServiceType(
			Long annotationServiceTypeId) throws SafeManagerException {
		try {
			return annotationServiceDao.getAnnotationServiceType(annotationServiceTypeId);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}
	
	/**
	 * @see gleam.executive.service.AnnotationServiceManager#getAnnotationServiceTypeByName(String)
	 */
	public AnnotationServiceType getAnnotationServiceTypeByName(
			String name) throws SafeManagerException {
		try {
			return annotationServiceDao.getAnnotationServiceTypeByName(name);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}
	
	/**
	 * @see gleam.executive.service.AnnotationServiceManager#getAnnotationServices(gleam.executive.model.AnnotationService)
	 */
	public List getAnnotationServices(AnnotationService annotationService)
			throws SafeManagerException {
		try {
			return annotationServiceDao.getAnnotationServices(annotationService);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}

	/**
	 * @see gleam.executive.service.AnnotationServiceManager#getAnnotationServices()
	 */
	public List getAnnotationServices() throws SafeManagerException {
		try {
			return annotationServiceDao.getAnnotationServices();

		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}

	/**
	 * @see gleam.executive.service.AnnotationServiceManager#getAnnotationServiceTypes()
	 */
	public List getAnnotationServiceTypes() throws SafeManagerException {
		try {
			return annotationServiceDao.getAnnotationServiceTypes();

		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}

	/**
	 * @see gleam.executive.service.AnnotationServiceManager#saveAnnotationService(gleam.executive.model.AnnotationService)
	 */
	public void saveAnnotationService(AnnotationService annotationService)
			throws SafeManagerException {
		try {
			annotationServiceDao.saveAnnotationService(annotationService);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}

	/**
	 * @see gleam.executive.service.AnnotationServiceManager#saveAnnotationServiceType(gleam.executive.model.AnnotationService)
	 */
	public void saveAnnotationServiceType(
			AnnotationServiceType annotationServiceType)
			throws SafeManagerException {
		try {
			annotationServiceDao.saveAnnotationServiceType(annotationServiceType);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}

	/**
	 * @see gleam.executive.service.AnnotationServiceManager#removeAnnotationService(Long)
	 */
	public void removeAnnotationService(Long annotationServiceId)
			throws SafeManagerException {
		try {
			annotationServiceDao.removeAnnotationService(annotationServiceId);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}

	/**
	 * @see gleam.executive.service.AnnotationServiceManager#removeAnnotationService(Long)
	 */
	public void removeAnnotationServiceType(Long annotationServiceTypeId)
			throws SafeManagerException {
		try {
			annotationServiceDao.removeAnnotationServiceType(annotationServiceTypeId);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}

	/**
	 * @see gleam.executive.service.AnnotationServiceManager#getAnnotationServiceTypeForAnnotationService(Long)
	 */
	public AnnotationServiceType getAnnotationServiceTypeForAnnotationService(
			Long annotationServiceId) throws SafeManagerException {
		try {
			return annotationServiceDao
					.getAnnotationServiceTypeForAnnotationService(annotationServiceId);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}

	/**
	 * @see gleam.executive.service.AnnotationServiceManager#getAnnotationServicesWithType(Long)
	 */
	public List getAnnotationServicesWithType(Long annotationServiceTypeId)
			throws SafeManagerException {
		try {
			return annotationServiceDao.getAnnotationServicesWithType(annotationServiceTypeId);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}

	public String getSchemasDirPath() {
		return schemasDirPath;
	}

	public void setSchemasDirPath(String schemasDirPath) {
		this.schemasDirPath = schemasDirPath;
	}

	public AnnotationServiceDao getAnnotationServiceDao() {
		return annotationServiceDao;
	}

	public void setAnnotationServiceDao(AnnotationServiceDao dao) {
		this.annotationServiceDao = dao;
	}

}
