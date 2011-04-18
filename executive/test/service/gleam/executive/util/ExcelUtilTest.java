package gleam.executive.util;

import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import gleam.executive.model.User;

public class ExcelUtilTest extends TestCase {
	  // ~ Instance fields
	  // ========================================================
	  private final Log log = LogFactory.getLog(ExcelUtilTest.class);
	  private static final String testXLSFilePath = "test/service/gleam/executive/service/resource/test.xls";
	  // ~ Constructors
	  // ===========================================================
	  public ExcelUtilTest(String name) {
	    super(name);
	  }
	  
	  public void testPopulateUser(){ 
		  try{
		  List<User> users = ExcelUtil.populateUser(testXLSFilePath);
		  Iterator<User> it = users.iterator();
		  while(it.hasNext()){
			  User user = it.next();
			  log.debug("---------------------------------------------");
			  log.debug("username: "+user.getUsername());
			  assertEquals("agaton", user.getUsername());
			  log.debug("password: "+user.getPassword());
			  assertEquals("tomcat", user.getPassword());
			  log.debug("firstName: "+user.getFirstName());
			  assertEquals("Milan", user.getFirstName());
			  log.debug("lastName: "+user.getLastName());
			  assertEquals("Agatonovic", user.getLastName());
			  log.debug("email: "+user.getEmail());
			  assertEquals("agaton@dcs.shef.ac.uk", user.getEmail());
			  String rolesCSVString = StringUtils.collectionToCommaDelimitedString(user.getRoleNames());
			  log.debug("rolesCSVString: "+rolesCSVString);
			  assertEquals("annotator", rolesCSVString);
		  }
		  } catch(Exception e){
			  e.printStackTrace();
		  }
		
	  }
}
