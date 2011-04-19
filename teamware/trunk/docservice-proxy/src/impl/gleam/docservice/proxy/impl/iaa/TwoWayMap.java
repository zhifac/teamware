/*
 *  TwoWayMap.java
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
package gleam.docservice.proxy.impl.iaa;

import java.util.Map;
import java.util.HashMap;

/**
 * A class representing a symmetric two-dimensional map, (x, y) -> v,
 * where the key pair (x, y) maps to the same value as the pair (y, x).
 * Each pair is only actually stored in the map one way round, the
 * methods getFirstKey and getSecondKey allow you to determine which
 * way.
 */
public class TwoWayMap<K, V> {

  private Map<K, Map<K, V>> backingMap = new HashMap<K, Map<K, V>>();

  public V put(K key1, K key2, V value) {
    if(containsKeys(key1, key2)) {
      Map<K, V> m = backingMap.get(getFirstKey(key1, key2));
      return m.put(getSecondKey(key1, key2), value);
    }
    else {
      Map<K, V> m = backingMap.get(key1);
      if(m == null) {
        m = new HashMap<K, V>();
        backingMap.put(key1, m);
      }
      return m.put(key2, value);
    }
  }

  public V get(Object key1, Object key2) {
    if(containsKeys(key1, key2)) {
      return backingMap.get(getFirstKey(key1, key2)).get(
              getSecondKey(key1, key2));
    }
    else {
      return null;
    }
  }

  public boolean containsKeys(Object key1, Object key2) {
    if(backingMap.containsKey(key1)) {
      Map<K, V> m = backingMap.get(key1);
      if(m != null && m.containsKey(key2)) {
        return true;
      }
    }

    if(backingMap.containsKey(key2)) {
      Map<K, V> m = backingMap.get(key2);
      if(m != null && m.containsKey(key1)) {
        return true;
      }
    }

    return false;
  }

  /**
   * If this map contains the given pair of keys, this method returns
   * the key which is first in the order in which the keys were
   * originally added. The results are undefined if this key pair is not
   * contained in this map.
   */
  public <T> T getFirstKey(T key1, T key2) {
    if(backingMap.containsKey(key1) && backingMap.get(key1).containsKey(key2)) {
      return key1;
    }
    else {
      return key2;
    }
  }

  /**
   * If this map contains the given pair of keys, this method returns
   * the key which is second in the order in which the keys were
   * originally added. The results are undefined if this key pair is not
   * contained in this map.
   */
  public <T> T getSecondKey(T key1, T key2) {
    if(backingMap.containsKey(key2) && backingMap.get(key2).containsKey(key1)) {
      return key1;
    }
    else {
      return key2;
    }
  }
}
