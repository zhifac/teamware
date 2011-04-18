/*
 * This file is part of SAFE, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2007 The University of Sheffield
 *
 * $Id$
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
