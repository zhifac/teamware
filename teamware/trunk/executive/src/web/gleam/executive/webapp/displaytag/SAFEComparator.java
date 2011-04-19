/*
 *  SAFEComparator.java
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
