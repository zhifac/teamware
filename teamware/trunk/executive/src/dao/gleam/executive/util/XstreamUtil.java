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
