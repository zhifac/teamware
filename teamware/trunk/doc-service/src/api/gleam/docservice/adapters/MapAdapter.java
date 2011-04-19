/*
 *  MapAdapter.java
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
package gleam.docservice.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;;

/**
 * Adapter to convert a Map to XML-able form.
 */
public class MapAdapter<K, V> extends XmlAdapter<Map<K, V>, MapWrapper<K, V>> {

  @Override
  public Map<K, V> marshal(MapWrapper<K, V> wrapper) throws Exception {
    if(wrapper == null) {
      return null;
    }
    
    List<MapEntry<K, V>> entries = wrapper.getEntries();
    HashMap<K, V> target = new HashMap<K, V>(entries.size());
    for(MapEntry<K, V> entry : entries) {
      target.put(entry.getKey(), entry.getValue());
    }
    
    return target;
  }

  @Override
  public MapWrapper<K, V> unmarshal(Map<K, V> map) throws Exception {
    if(map == null) {
      return null;
    }
    MapWrapper<K, V> w = new MapWrapper<K, V>();
    List<MapEntry<K, V>> entries = new ArrayList<MapEntry<K, V>>(map.size());
    for(Map.Entry<K, V> entry : map.entrySet()) {
      MapEntry<K, V> me = new MapEntry<K, V>();
      me.setKey(entry.getKey());
      me.setValue(entry.getValue());
      entries.add(me);
    }
    
    w.setEntries(entries);
    return w;
  }

}
