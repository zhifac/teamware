/*
 *  AnnotationSetsListModel.java
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

import gleam.gateservice.definition.GateServiceDefinition;
import gleam.gateservice.definition.ServiceDefinitionEvent;
import gleam.gateservice.definition.ServiceDefinitionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractListModel;

/**
 * List model for the input and output annotation set lists.
 */
public class AnnotationSetsListModel extends AbstractListModel implements
                                                              ServiceDefinitionListener {

  /**
   * Comparator to keep the set names in alphabetical order, with the
   * default set always listed first.
   */
  private static final Comparator<String> COMPARATOR = new NullsFirstComparator<String>();

  /**
   * The displayed list of annotation set names. This is kept up to date
   * by events fired from the service definition.
   */
  private List<String> annotationSetNames;

  /**
   * Which events are we interested in (input or output annotation set)?
   */
  private ServiceDefinitionEvent.Direction direction;

  public AnnotationSetsListModel(GateServiceDefinition def,
          Set<String> setNames, ServiceDefinitionEvent.Direction direction) {
    annotationSetNames = new ArrayList<String>(setNames);
    Collections.sort(annotationSetNames, COMPARATOR);
    this.direction = direction;
    def.addServiceDefinitionListener(this);
  }

  public Object getElementAt(int index) {
    return annotationSetNames.get(index);
  }

  public int getSize() {
    return annotationSetNames.size();
  }

  public boolean containsAnnotationSet(String name) {
    return annotationSetNames.contains(name);
  }

  /**
   * Update our list of sets when one is added to the definition.
   */
  public void annotationSetAdded(ServiceDefinitionEvent e) {
    if(e.getDirection() == direction) {
      String asName = e.getAnnotationSetName();
      int index = Collections.binarySearch(annotationSetNames, asName,
              COMPARATOR);
      if(index < 0) {
        index = -index - 1;
        annotationSetNames.add(index, asName);
        fireIntervalAdded(this, index, index);
      }
    }
  }

  /**
   * Update our list of sets when one is removed from the definition.
   */
  public void annotationSetRemoved(ServiceDefinitionEvent e) {
    if(e.getDirection() == direction) {
      String asName = e.getAnnotationSetName();
      int index = Collections.binarySearch(annotationSetNames, asName,
              COMPARATOR);
      if(index >= 0) {
        annotationSetNames.remove(index);
        fireIntervalRemoved(this, index, index);
      }
    }
  }

  // remaining ServiceDefinitionListener methods are unused

  public void featureMappingAdded(ServiceDefinitionEvent arg0) {
  }

  public void featureMappingRemoved(ServiceDefinitionEvent arg0) {
  }

  public void parameterAdded(ServiceDefinitionEvent arg0) {
  }

  public void parameterChanged(ServiceDefinitionEvent arg0) {
  }

  public void parameterMappingAdded(ServiceDefinitionEvent arg0) {
  }

  public void parameterMappingRemoved(ServiceDefinitionEvent arg0) {
  }

  public void parameterRemoved(ServiceDefinitionEvent arg0) {
  }
}
