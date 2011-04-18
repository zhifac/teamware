package gleam.executive.workflow.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;



public class CSVUtil {



	/**
	   * Consumes last token in csvString.
	   *
	   * @param csvString
	   * @return 2 elements in array; 1st is token, 2nd is the remaining csv string
	   */
	  public static String[] fetchLastToken(String csvString) {
	    String[] result = new String[2];
	    int comma = csvString.lastIndexOf(",");
	    // token element
	    if(comma!=-1){
		    result[0] = csvString.substring(comma+1);
		    // remained string
		    result[1] = csvString.substring(0, comma);
		    }
	    else{
	        result[0]=csvString;
	        result[1]=null;
	    }
	    return result;
	  }

	  /**
	   * Creates delimited string from elements
	   * @param elements
	   * @return delimited String
	   */
	  public static String createTokenFromElements(String[] elements, String separator) {
            return StringUtils.arrayToDelimitedString(elements, separator);
       }



	  /**
	   * Updates CSV List by applying specified operation
	   * @param elements
	   * @return delimited String
	   */
	  public static String execute(String item, String itemCSVList, String operation) {
          String result = "";
		  if("+".equals(operation)){
        	  result = appendTokenToCSVString(item, itemCSVList);
          }
		  else if("-".equals(operation)){
			  result = removeTokenFromCSVString(item, itemCSVList);
		  }
		  return result;
       }




	  /**
	   * Adds token to an existing CSV string no matter of token existed previously
	   * @param token
	   * @param csvString
	   * @return CSV String with added token
	   */
	  public static String appendTokenToCSVString(String token, String csvString) {

       StringBuilder builder = new StringBuilder("");
    	if(csvString!=null && !"".equals(csvString)){
    		builder.append(csvString);
    		builder.append(",");

    	}
    	builder.append(token);
    	return builder.toString();
    }

	  /**
	   * removes token from an existing CSV string if exists
	   * @param token
	   * @param csvString
	   * @return CSV String after token removal
	   */
	  public static String removeTokenFromCSVString(String token, String csvString) {

	    	Set<String> set = StringUtils.commaDelimitedListToSet(csvString);
	        boolean found = false;
			Iterator<String> it = set.iterator();
			while(it.hasNext() && !found){
				if(token.equals(it.next())){
					found = true;
					it.remove();
				}
			}
			return StringUtils.collectionToCommaDelimitedString(set);
	    }

	  public static int getNumberOfTokens(String csvString){
		  int result = 0;
		  if(csvString!=null){
		     String[] array = StringUtils.commaDelimitedListToStringArray(csvString);
		     result = array.length;
		  }
		  return result;

	  }





}