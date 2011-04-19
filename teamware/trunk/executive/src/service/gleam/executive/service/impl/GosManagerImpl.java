/*
 *  GosManagerImpl.java
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
 * Milan Agatonovic and Haotian Sun
 *
 *  $Id$
 */
package gleam.executive.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import gleam.executive.service.GosManager;
import gleam.executive.service.SafeManagerException;
//import gleam.gos.proxy.*;


public class GosManagerImpl extends BaseManager implements GosManager {
	private String gosURL;
	//private GOSProxyFactory gosProxyFactory;
/*
	public GosManagerImpl(String gosURL, GOSProxyFactory gosProxyFactory) {
		this.gosURL = gosURL;
		log.debug("gosURL: "+ gosURL);
		this.gosProxyFactory = gosProxyFactory;
	}

	public String getGosURL() {
		return gosURL;
	}

	public void setGosURL(String gosURL) {
		this.gosURL = gosURL;
	}


	public boolean addRepository(String repositoryName, String ontologyURL)
			throws SafeManagerException {
		System.out.println("calling addRepository()..........");
		try {
			return getGosProxyFactory().getGOSProxy(new URI(gosURL))
					.addRepository(repositoryName, ontologyURL);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());

		} catch (GOSProxyException gpe) {
			gpe.printStackTrace();
			throw new SafeManagerException(
					"GOSProxy couldn't add new repository with repositoryName :"
							+ repositoryName + " because " + gpe.getMessage());
		}
	}

	public boolean clearRepository(String repositoryName)
			throws SafeManagerException {
		System.out.println("calling clearRepository()..........");
		try {
			return getGosProxyFactory().getGOSProxy(new URI(gosURL))
					.clearRepository(repositoryName);

		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());

		} catch (GOSProxyException gpe) {
			throw new SafeManagerException(
					"GOSProxy couldn't clear the repository :" + repositoryName
							+ " because " + gpe.getMessage());
		}
	}


	public boolean deleteRepository(String repositoryName)
			throws SafeManagerException {
		System.out.println("calling deleteRepository()..........");
		try {
			return getGosProxyFactory().getGOSProxy(new URI(gosURL))
					.deleteRepository(repositoryName);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());

		} catch (GOSProxyException gpe) {
			throw new SafeManagerException(
					"GOSProxy couldn't delete the repository :"
							+ repositoryName + " because " + gpe.getMessage());
		}
	}

	
	public List listRepositories() throws SafeManagerException {
		System.out.println("calling listRepositories()..........");
		try {
			return Arrays.asList(getGosProxyFactory().getGOSProxy(
					new URI(gosURL)).listRepositories());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());

		} catch (GOSProxyException gpe) {
			gpe.printStackTrace();
			throw new SafeManagerException(
					"GOSProxy couldn't list the repository because "
							+ gpe.getMessage());
		}
	}

	
	public String getOntologyData(String repositoryName)
			throws SafeManagerException {
		System.out.println("calling getOntologyData().........");
		try {
			return getGosProxyFactory().getGOSProxy(new URI(gosURL))
					.getOntologyData(repositoryName);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());

		} catch (GOSProxyException gpe) {
			gpe.printStackTrace();
			throw new SafeManagerException(
					"GOSProxy couldn't get ontology data from the repository"
							+ " because " + gpe.getMessage());
		}

	}

	public GOSProxyFactory getGosProxyFactory() {
		return gosProxyFactory;
	}
*/	
	
}
