package gleam.executive.dao;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gleam.executive.model.AnnotationService;
import gleam.executive.model.AnnotationServiceType;
import gleam.executive.util.MapUtil;
import gleam.executive.util.XstreamUtil;

import org.springframework.util.StringUtils;


public class AnnotationServiceDaoTest extends BaseDaoTestCase {
	private AnnotationServiceDao dao = null;

	public void setAnnotationServiceDao(AnnotationServiceDao dao) {
		this.dao = dao;
	}
	
	
	public void testAdd() throws Exception {
		log.debug("----------------Testing add Annotation Service ----------");
		AnnotationServiceType annotationServiceType = new AnnotationServiceType();
		// annotationServiceType.setId(new Long(1));
		annotationServiceType.setName("someType");
		String[] keyArray = { "asKey", "asValue", "asExtraMappings",
				"parameterKey", "parameterValue" };
		String csvKeys = StringUtils.arrayToCommaDelimitedString(keyArray);
		log.debug(csvKeys);
		annotationServiceType.setData(csvKeys);
		
		dao.saveAnnotationServiceType(annotationServiceType);
		log.debug("saved annotation service type");
		setComplete(); // change behavior from rollback to commit
		endTransaction();
		startNewTransaction();
		AnnotationServiceType annotationServiceType1 = dao
				.getAnnotationServiceTypeByName("someType");
		assertEquals("check name", "someType", annotationServiceType1.getName());
		log.debug(annotationServiceType1.getName());
		log.debug(annotationServiceType1.getId());
		AnnotationService annotationService = new AnnotationService();
		annotationService.setAnnotationServiceTypeId(annotationServiceType1.getId());
		//annotationService.setAnnotationServiceType(annotationServiceType1);
		// annotationService.setId(new Long(1));
		annotationService.setName("someService");

		annotationService.setDescription("someService description");
		annotationService.setUrl("http://some-service");
		assertNotNull("check annotationServiceType", annotationService.getAnnotationServiceTypeId());
		Map<String, String[]> variableMap = new HashMap<String, String[]>();
		variableMap.put("asKey", new String[]{"some asKey"});
		variableMap.put("asValue", new String[]{"some asValue"});
		variableMap.put("asExtraMappings", new String[]{"some asExtraMappings"});
		variableMap.put("parameterKey", new String[]{"some parameterKey"});
		variableMap.put("parameterValue", new String[]{"some parameterValue"});
		Map<String, String> parameterMap = MapUtil.copyMatchingEntriesIntoMap(
				variableMap, csvKeys);
		String data = XstreamUtil.fromMapToString(parameterMap);
		annotationService.setParameters(data);
		dao.saveAnnotationService(annotationService);
		log.debug("saved annotation service");
		setComplete(); // change behavior from rollback to commit
		endTransaction();
		startNewTransaction();
		AnnotationService annotationService1 = dao
				.getAnnotationServiceByName("someService");
		assertEquals("check description", "someService description", annotationService1
				.getDescription());
		assertNotNull("check annotationServiceType", annotationService.getAnnotationServiceTypeId());
		
		String parameterData = annotationService1.getParameters();
		Map<String, String> parameterMap1 = XstreamUtil.fromStringToMap(parameterData);

		String asKey = (String) parameterMap1.get("asKey");
		String asValue = (String) parameterMap1.get("asValue");
		String asExtraMappings = (String) parameterMap1.get("asExtraMappings");
		String parameterKey = (String) parameterMap1.get("parameterKey");
		String parameterValue = (String) parameterMap1.get("parameterValue");
		assertEquals("check asKey", "some asKey", asKey);
		assertEquals("check asValue", "some asValue", asValue);
		assertEquals("check asExtraMappings", "some asExtraMappings",
				asExtraMappings);
		assertEquals("check parameterKey", "some parameterKey", parameterKey);
		assertEquals("check parameterValue", "some parameterValue",
				parameterValue);
		setComplete(); // change behavior from rollback to commit
		endTransaction();
      }
	
	public void testRemove() throws Exception {
		AnnotationService annotationService = dao.getAnnotationServiceByName("someService");
	    log.debug("try to delete annotationService: " + annotationService.getId());
	    dao.removeAnnotationService(annotationService.getId());
	    log.debug("deleted annotationService " + annotationService.getId());
	    AnnotationServiceType annotationServiceType = dao.getAnnotationServiceTypeByName("someType");
	    log.debug("try to delete annotationServiceType: " + annotationServiceType.getId());
	    dao.removeAnnotationServiceType(annotationServiceType.getId());
	    log.debug("deleted annotationServiceType " + annotationServiceType.getId());
        setComplete(); // change behavior from rollback to commit
		endTransaction();
	}

}
