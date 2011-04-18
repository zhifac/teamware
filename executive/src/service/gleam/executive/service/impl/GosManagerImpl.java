package gleam.executive.service.impl;

/*
 * GosManagerImpl.java
 *
 * Copyright (c) 1998-2007, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 *
 * <p>
 * <a href="GosManagerImpl.java.html"><i>View Source</i></a>
 * </p>
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 * @author <a href="H.Sun@dcs.shef.ac.uk">Haotian Sun</a>
 *
 */
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
