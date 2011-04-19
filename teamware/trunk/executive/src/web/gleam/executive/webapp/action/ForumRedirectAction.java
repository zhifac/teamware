/*
 *  ForumRedirectAction.java
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
package gleam.executive.webapp.action;

import gleam.executive.model.WebAppBean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


/**
 * @struts.action path="/forum" validate="false"
 * @struts.action-forward name="forumInstall"
 *                        path="/WEB-INF/pages/forumInstall.jsp"
 * @struts.action-forward name="forumList"
 *                        path="/forumList.html" redirect="true"
 */
public class ForumRedirectAction extends BaseAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		// first check if DB exists
		BasicDataSource dataSource = (BasicDataSource)getBean("dataSource");
		String databaseURL = dataSource.getUrl();
		WebAppBean webAppBean = (WebAppBean)getBean("webAppBean");
		log.debug("databaseURL: " + databaseURL);
		String databaseDriver = dataSource.getDriverClassName();
		log.debug("databaseDriver: " + databaseDriver);
		String user = dataSource.getUsername();
		log.debug("user: " + user);
		String password = dataSource.getPassword();
		log.debug("password: " + password);
		if(!tableExists(databaseURL, databaseDriver, user, password, "jforum_categories")){
			log.debug("forum is not installed: ");
			return mapping.findForward("forumInstall");
		}
		else {
			log.debug("forum is installed");
			return mapping.findForward("forumList");
		}

	}

	public boolean tableExists(String databaseURL, String databaseDriver, String user, String password,
			String tableName) throws SQLException {

		Connection con = null;
        boolean flag = false;
		PreparedStatement stmt = null;
		ResultSet results = null;
		try {
			Class.forName(databaseDriver);
			con = DriverManager.getConnection(databaseURL, user, password);
			stmt = con.prepareStatement("SELECT COUNT(*) FROM "
					+ "jforum_categories" + " WHERE 1 = 2");
			results = stmt.executeQuery();
			flag = true; // if table does exist, no rows will ever be
							// returned
		} catch (SQLException e) {
			e.printStackTrace();
							// thrown
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (results != null) {
				results.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
        return flag;
	}

}
