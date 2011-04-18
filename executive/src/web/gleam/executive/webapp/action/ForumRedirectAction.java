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
 * Copyright (c) 1998-2006, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * <p>
 * <a href="ForumRedirectAction.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
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
