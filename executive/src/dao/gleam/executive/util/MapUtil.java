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
