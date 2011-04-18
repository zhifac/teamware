/*
 * This file is part of SAFE, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2007 The University of Sheffield
 *
 * $Id$
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
