package gleam.executive.webapp.displaytag;

   
   import java.text.Collator;
import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
   
   
   /**
    * Default comparator. Was previously part of RowSorter.
    * @author fguist
    * @author rapruitt
    * @version $Revision: 1085 $ ($Author: rapruitt $)
    */
   public class SAFEComparator implements Comparator {
   
	   private static Log log = LogFactory.getLog(SAFEComparator.class);
       /**
        * Use this collator.
        */
       private Collator collator;
   
       /**
        * Instantiate a default comparator with no collator specified.
        */
       public SAFEComparator()
       {
           this(Collator.getInstance());
       }
   
       /**
        * Instantiate a default comparator with a specified collator.
        * @param collatorToUse collator instance
        */
       public SAFEComparator(Collator collatorToUse)
       {
           this.collator = collatorToUse;
           //collator.setStrength(Collator.PRIMARY); // ignore case and accents
           collator.setStrength(Collator.TERTIARY); 
       }
   
       /**
        * Compares two given objects. Not comparable objects are compared using their string representation. String
        * comparisons are done using a Collator.
        * @param object1 first parameter
        * @param object2 second parameter
        * @return the value
        */
       public int compare(Object object1, Object object2)
       {
           int returnValue;
           if (object1 instanceof String && object2 instanceof String)
           {
        	   
               returnValue = collator.compare(object1, object2);
           }
           else if (object1 instanceof Comparable && object2 instanceof Comparable)
           {
               returnValue = ((Comparable) object1).compareTo(object2);
           }
           else
           {
               // if object are not null and don't implement comparable, compare using string values
               returnValue = collator.compare(object1.toString(), object2.toString());
           }
           return returnValue;
       }
   }
