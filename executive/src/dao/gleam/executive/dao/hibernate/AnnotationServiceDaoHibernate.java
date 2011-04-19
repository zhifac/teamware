/*
 *  AnnotationServiceDaoHibernate.java
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
 *  $Id$
 */
package gleam.executive.dao.hibernate;

import java.util.List;

import gleam.executive.dao.AnnotationServiceDao;
import gleam.executive.model.AnnotationService;
import gleam.executive.model.AnnotationServiceType;

import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.hibernate.ObjectNotFoundException;
import org.springframework.orm.ObjectRetrievalFailureException;

/**
 * This class interacts with Spring's HibernateTemplate to save/delete and
 * retrieve AnnotationService objects.
 * 
 * <p>
 * <a href="AnnotationServiceDaoHibernate.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 */
public class AnnotationServiceDaoHibernate extends BaseDaoHibernate implements
		AnnotationServiceDao {
	/**
	 * @see gleam.executive.dao.AnnotationServiceDao#getAnnotationService(Long)
	 */
	public AnnotationService getAnnotationService(Long annotationServiceId) {
		AnnotationService annotationService = (AnnotationService) getHibernateTemplate()
				.get(AnnotationService.class, annotationServiceId);
		if (annotationService == null) {
			log.warn("uh oh, annotationService '" + annotationServiceId
					+ "' not found...");
			throw new ObjectRetrievalFailureException(AnnotationService.class,
					annotationServiceId);
		}
		return annotationService;
	}

	/**
	 * @see gleam.executive.dao.AnnotationServiceDao#getAnnotationServiceByName(String)
	 */
	public AnnotationService getAnnotationServiceByName(String name) {
		List<AnnotationService> annotationServiceList = getHibernateTemplate()
				.find("from AnnotationService where name=?", name);
		if (annotationServiceList == null
				|| annotationServiceList.isEmpty()) {
			throw new ObjectRetrievalFailureException(
					AnnotationService.class, name);

		} else {
			return annotationServiceList.get(0);
		}
	}
	
	/**
	 * @see gleam.executive.dao.AnnotationServiceDao#getAnnotationServices(gleam.executive.model.AnnotationService)
	 */
	public List getAnnotationServices(AnnotationService annotationService) {
		return getHibernateTemplate().find(
				"from AnnotationService sr order by upper(sr.url)");
	}

	/**
	 * @see gleam.executive.dao.AnnotationServiceDao#getAnnotationServices()
	 */
	public List getAnnotationServices() {
		return getHibernateTemplate().find(
				"from AnnotationService sr order by upper(sr.url)");
	}

	/**
	 * @see gleam.executive.dao.AnnotationServiceDao#getAnnotationServiceTypes()
	 */
	public List getAnnotationServiceTypes() {
		return getHibernateTemplate().find(
				"from AnnotationServiceType sr order by upper(sr.name)");
	}

	/**
	 * @see gleam.executive.dao.AnnotationServiceDao#saveAnnotationService(gleam.executive.model.AnnotationService)
	 */
	public void saveAnnotationService(AnnotationService annotationService) {
		if (log.isDebugEnabled()) {
			log.debug("AnnotationService's id: " + annotationService.getId());
		}
		getHibernateTemplate().merge(annotationService);
		getHibernateTemplate().flush();
	}

	/**
	 * @see gleam.executive.dao.AnnotationServiceDao#saveAnnotationServiceType(gleam.executive.model.AnnotationServiceType)
	 */
	public void saveAnnotationServiceType(
			AnnotationServiceType annotationServiceType) {
		if (log.isDebugEnabled()) {
			log.debug("AnnotationServiceType's id: "
					+ annotationServiceType.getId());
		}
		getHibernateTemplate().merge(annotationServiceType);
		getHibernateTemplate().flush();
	}

	/**
	 * @see gleam.executive.dao.AnnotationServiceDao#removeAnnotationService(Long)
	 */
	public void removeAnnotationService(Long annotationServiceId) {
		getHibernateTemplate()
				.delete(getAnnotationService(annotationServiceId));
	}

	/**
	 * @see gleam.executive.dao.AnnotationServiceDao#removeAnnotationServiceType(Long)
	 */
	public void removeAnnotationServiceType(Long annotationServiceTypeId) {
		getHibernateTemplate().delete(
				getAnnotationServiceType(annotationServiceTypeId));
	}

	/**
	 * @see gleam.executive.dao.AnnotationServiceDao#getAnnotationServiceType(Long)
	 */
	public AnnotationServiceType getAnnotationServiceType(
			Long annotationServiceTypeId) {

		AnnotationServiceType annotationServiceType = (AnnotationServiceType) getHibernateTemplate()
				.get(AnnotationServiceType.class, annotationServiceTypeId);
		if (annotationServiceType == null) {
			log.warn("uh oh, annotationServiceType '" + annotationServiceTypeId
					+ "' not found!");
			throw new ObjectRetrievalFailureException(
					AnnotationServiceType.class, annotationServiceTypeId);
		}
		return annotationServiceType;
	}

	/**
	 * @see gleam.executive.dao.AnnotationServiceDao#getAnnotationServiceTypeByName(String)
	 */
	public AnnotationServiceType getAnnotationServiceTypeByName(String name) {
		List<AnnotationServiceType> annotationServiceTypeList = getHibernateTemplate()
				.find("from AnnotationServiceType where name=?", name);
		if (annotationServiceTypeList == null
				|| annotationServiceTypeList.isEmpty()) {
			throw new ObjectRetrievalFailureException(
					AnnotationServiceType.class, name);

		} else {
			return annotationServiceTypeList.get(0);
		}
	}

	/**
	 * @see gleam.executive.dao.AnnotationServiceDao#getAnnotationServiceType(Long)
	 */
	public AnnotationServiceType getAnnotationServiceTypeForAnnotationService(
			Long annotationServiceId) {
		AnnotationService annotationService = (AnnotationService) getHibernateTemplate()
				.get(AnnotationService.class, annotationServiceId);
		if (annotationService == null) {
			log.warn("uh oh, AnnotationService ID '" + annotationServiceId
					+ "' not found in getAnnotationServiceType()...");
			throw new ObjectRetrievalFailureException(AnnotationService.class,
					annotationServiceId);
		}
		AnnotationServiceType annotationServiceType = (AnnotationServiceType) getHibernateTemplate()
				.get(AnnotationServiceType.class,
						annotationService.getAnnotationServiceTypeId());
		if (annotationServiceType == null) {
			log.warn("uh oh, annotationServiceType '"
					+ annotationService.getAnnotationServiceTypeId()
					+ "' not found...");
			throw new ObjectRetrievalFailureException(
					AnnotationServiceType.class, annotationService
							.getAnnotationServiceTypeId());
		}
		return annotationServiceType;
	}

	/**
	 * @see gleam.executive.dao.AnnotationServiceDao#getAnnotationServicesWithType(Long)
	 */
	public List getAnnotationServicesWithType(Long annotationServiceTypeId) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT annotation_service from ").append(
				AnnotationService.class.getName() + " annotation_service ")
				.append(
						" WHERE annotation_service_type_id = '"
								+ annotationServiceTypeId + "' ");
		return getHibernateTemplate().find(sb.toString());
	}
}
