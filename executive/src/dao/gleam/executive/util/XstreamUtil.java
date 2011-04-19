/*
 *  XstreamUtil.java
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
package gleam.executive.util;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XstreamUtil {
	
	private final static Log log = LogFactory.getLog(XstreamUtil.class);
	 

	public static Map fromByteArrayToMap(byte[] data) throws Exception {
		String xml = new String(data, "utf-8");
		XStream xstream = new XStream(new DomDriver());
		return (Map) xstream
				.fromXML(xml);
	}


	public static byte[] fromMapToByteArray(Map map) throws Exception {
	       XStream xstream = new XStream(new DomDriver());
			String xml = xstream.toXML(map);
			log.debug(xml);
			return xml.getBytes("utf-8");
	}
	
	public static Map<String, String> fromStringToMap(String xml) throws Exception {
		XStream xstream = new XStream(new DomDriver());
		return (Map<String, String>) xstream
				.fromXML(xml);
	}
	
	public static String fromMapToString(Map<String, String> map) throws Exception {
	       XStream xstream = new XStream(new DomDriver());
			String xml = xstream.toXML(map);
			log.debug(xml);
			return xml;
	}

}
