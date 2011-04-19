/*
 *  MapUtil.java
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

public class MapUtil {
	 private final static Log log = LogFactory.getLog(MapUtil.class);
	 
	 public static Map<String, String> copyMatchingEntriesIntoMap(Map<String, String[]> variableMap, String csvKeys){
		 String[] keyArray = StringUtils.commaDelimitedListToStringArray(csvKeys);
		 Map<String, String> resultMap = new HashMap<String, String>();
		 Iterator<Map.Entry<String, String[]>> it = variableMap.entrySet().iterator();
		 while(it.hasNext()){
			 Map.Entry<String, String[]> entry = it.next();
			 for (int i = 0; i < keyArray.length; i++) {
				String str = keyArray[i];
				if(entry.getKey().equals(str)){
					resultMap.put(entry.getKey(), entry.getValue()[0]);
				}
			}
		 }
         log.debug("result map: "+resultMap);
		 return resultMap;
	 }

	 
}
