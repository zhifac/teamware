/*
 *  WebAppBean.java
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
package gleam.executive.model;

public class WebAppBean {
	private String name;

	private String annoDiffURL;

	private String urlBase;
	
	private String privateUrlBase;

	private String instanceName;

	private String pluginCSVList;

	private String title;
	
	private String instanceDir;

	public String getInstanceDir() {
		return instanceDir;
	}

	public void setInstanceDir(String instanceDir) {
		this.instanceDir = instanceDir;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPluginCSVList() {
		return pluginCSVList;
	}

	public void setPluginCSVList(String pluginCSVList) {
		this.pluginCSVList = pluginCSVList;
	}

	public WebAppBean(String urlBase, String webappName,
  String annoDifferURL, String instanceName, String pluginCSVList,
  String title, String instanceDir) {
    this(urlBase, urlBase, webappName, annoDifferURL, instanceName,
            pluginCSVList, title, instanceDir);
  }

  public WebAppBean(String urlBase, String privateUrlBase,
			String webappName, String annoDifferURL, String instanceName,
			String pluginCSVList, String title, String instanceDir) {
		this.urlBase = urlBase;
		this.privateUrlBase = privateUrlBase;
		this.name = webappName;
		this.annoDiffURL = annoDifferURL;
		this.instanceName = instanceName;
		this.pluginCSVList = pluginCSVList;
		this.title = title;
		this.instanceDir = instanceDir;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getAnnoDiffURL() {
		return annoDiffURL;
	}

	public void setAnnoDiffURL(String annoDiffURL) {
		this.annoDiffURL = annoDiffURL;
	}

	public String getUrlBase() {
		return urlBase;
	}

	public void setUrlBase(String urlBase) {
		this.urlBase = urlBase;
	}

	public String getPrivateUrlBase() {
    return privateUrlBase;
  }

  public void setPrivateUrlBase(String privateUrlBase) {
    this.privateUrlBase = privateUrlBase;
  }

  public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getBaseSchemaURL() {
		StringBuilder sb = new StringBuilder().append(
				getUrlBase()).append("/").append(getInstanceName()).append(
				"/schemas/");
		return sb.toString();
	}

}
