/*
 *  ResolveNextAvailableAnnotationSetNameTest.java
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
package gleam.executive.workflow;


import gleam.executive.workflow.util.AnnotationUtil;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;


public class ResolveNextAvailableAnnotationSetNameTest extends TestCase {


/*
 * This class should test the method from WorkflowUtil
 * public static String resolveNextAvailableAnnotationSetName(
			Collection<String> allAnnotationSetNames, String username)
 */

  public void testAnonymousWithExistingAnnotationSetNamesInSequence() throws Exception {
        String username = null;
        Collection<String> allAnnotationSetNames = new ArrayList<String>();
        allAnnotationSetNames.add("annotator1");
        allAnnotationSetNames.add("annotator2");
        String result = AnnotationUtil.resolveNextAvailableAnnotationSetName(allAnnotationSetNames, username);
        assertEquals("Check generated AS", result, "annotator3");
  }

  public void testAnonymousWithExistingAnnotationSetNamesWithoutSequence() throws Exception {
      String username = null;
      Collection<String> allAnnotationSetNames = new ArrayList<String>();
      allAnnotationSetNames.add("annotator1");
      allAnnotationSetNames.add("annotator3");
      String result = AnnotationUtil.resolveNextAvailableAnnotationSetName(allAnnotationSetNames, username);
      assertEquals("Check generated AS", result, "annotator2");
}


  public void testAnonymousWithNonExistingAnnotationSetNames() throws Exception {
      String username = null;
      Collection<String> allAnnotationSetNames = new ArrayList<String>();
      String result = AnnotationUtil.resolveNextAvailableAnnotationSetName(allAnnotationSetNames, username);
      assertEquals("Check generated AS", result, "annotator1");
  }

  public void testNonAnonymousWithExistingAnnotationSetNamesInSequence() throws Exception {
      String username = "agaton";
      Collection<String> allAnnotationSetNames = new ArrayList<String>();
      allAnnotationSetNames.add("agaton1");
      String result = AnnotationUtil.resolveNextAvailableAnnotationSetName(allAnnotationSetNames, username);
      assertEquals("Check generated AS", result, "agaton2");
  }

  public void testNonAnonymousWithExistingAnnotationSetNamesWithoutSequence() throws Exception {
      String username = "agaton";
      Collection<String> allAnnotationSetNames = new ArrayList<String>();
      allAnnotationSetNames.add("agaton2");
      allAnnotationSetNames.add("agaton4");
      String result = AnnotationUtil.resolveNextAvailableAnnotationSetName(allAnnotationSetNames, username);
      assertEquals("Check generated AS", result, "agaton1");
  }

  public void testNonAnonymousWithGenericNameWithExistingAnnotationSetNamesInSequence() throws Exception {
      String username = "annotator1";
      Collection<String> allAnnotationSetNames = new ArrayList<String>();
      allAnnotationSetNames.add("annotator1");
      String result = AnnotationUtil.resolveNextAvailableAnnotationSetName(allAnnotationSetNames, username);
      assertEquals("Check generated AS", result, "annotator2");
  }

  public void testNonAnonymousWithGenericNameWithExistingAnnotationSetNamesWithoutSequence() throws Exception {
      String username = "annotator1";
      Collection<String> allAnnotationSetNames = new ArrayList<String>();
      allAnnotationSetNames.add("annotator2");
      allAnnotationSetNames.add("annotator4");
      String result = AnnotationUtil.resolveNextAvailableAnnotationSetName(allAnnotationSetNames, username);
      assertEquals("Check generated AS", result, "annotator1");
  }

  public void testNonAnonymousWithGenericNameWithNonExistingAnnotationSetNames() throws Exception {
      String username = "annotator1";
      Collection<String> allAnnotationSetNames = new ArrayList<String>();
      String result = AnnotationUtil.resolveNextAvailableAnnotationSetName(allAnnotationSetNames, username);
      assertEquals("Check generated AS", result, "annotator");
  }


}


