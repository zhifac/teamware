package gleam.executive.util;

import gate.Document;
import gate.DocumentFormat;
import gate.Factory;
import gate.FeatureMap;
import gate.GateConstants;
import gate.corpora.DocumentStaxUtils;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import gate.creole.annic.lucene.QueryParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GATEUtil {

	private static Log log = LogFactory.getLog(GATEUtil.class);
	private static XMLOutputFactory outputFactory = XMLOutputFactory
			.newInstance();

	public static long MAX_ALLOWED_SIZE = 30 * 1024 * 1024;// 25Mb

	public static String DEFAULT_ENCODING = "UTF-8";

	public static String DEFAULT_MARKUP_AWARE = "true";

	public static Set<String> supportedFormats = DocumentFormat
			.getSupportedFileSuffixes();

	public static byte[] getDocumentXml(URL entryURL, String encoding,
			String markupAware) throws Exception {
		byte[] docXml = null;
		Document doc = null;
		try {
			FeatureMap docParams = Factory.newFeatureMap();
			docParams.put(Document.DOCUMENT_URL_PARAMETER_NAME, entryURL);
			docParams.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME, encoding);
			if (markupAware.equals("true")) {
				docParams.put(Document.DOCUMENT_MARKUP_AWARE_PARAMETER_NAME,
						Boolean.TRUE);
			} else {
				docParams.put(Document.DOCUMENT_MARKUP_AWARE_PARAMETER_NAME,
						Boolean.FALSE);
			}
			FeatureMap docFeatures = Factory.newFeatureMap();
			docFeatures.put(GateConstants.THROWEX_FORMAT_PROPERTY_NAME,
					Boolean.TRUE);
			doc = (Document) Factory.createResource(
					"gate.corpora.DocumentImpl", docParams, docFeatures);

			log.debug("created GATE doc ");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			XMLStreamWriter xsw = outputFactory.createXMLStreamWriter(baos,
					encoding);
			xsw.writeStartDocument();
			DocumentStaxUtils.writeDocument(doc, xsw, "");
			xsw.close();
			docXml = baos.toByteArray();
		} finally {
			// be sure to free the document to avoid a
			// memory leak
			if (doc != null) {
				Factory.deleteResource(doc);
			}
		}
		return docXml;
	}

	public static boolean isValidQuery(String query) {
		return QueryParser.isValidQuery(query);
	}
/*
	public static void startPoint(String benchmarkID) {
		Benchmark.startPoint(benchmarkID);
	}

	public static void checkPoint(String benchmarkID,
			Object objectInvokingThisCheckPoint, Map benchmarkingFeatures) {
		Benchmark.checkPoint(benchmarkID, objectInvokingThisCheckPoint,
				benchmarkingFeatures);
	}
*/	
	 public static String extractNameOfGATEEntity(String itemId){
		  int index = itemId.indexOf("___");
		  String itemName = "";
		  if(index!=-1){
		   itemName = itemId.substring(0, index);
		  }
		  else {
			  itemName = itemId;
		  }
		 
		  return itemName;
     }

}
