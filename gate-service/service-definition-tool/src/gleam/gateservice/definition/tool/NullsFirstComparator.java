/*
 *  NullsFirstComparator.java
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
package gleam.gateservice.definition.tool;

import java.util.Comparator;

/**
 * A comparator that compares mutually comparable objects, but always
 * treats <code>null</code> as being less than any other value.
 * 
 * @param <T> the type of objects to compare. This type must be
 *          comparable to itself.
 */
public class NullsFirstComparator<T extends Comparable<T>> implements
                                                           Comparator<T> {

  public int compare(T a, T b) {
    if(a == null) {
      if(b == null) {
        return 0;
      }
      else {
        return -1;
      }
    }
    else if(b == null) {
      return 1;
    }
    else {
      return a.compareTo(b);
    }
  }

}
