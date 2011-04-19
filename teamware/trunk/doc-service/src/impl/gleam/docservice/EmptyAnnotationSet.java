/*
 *  EmptyAnnotationSet.java
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
package gleam.docservice;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;
import gate.Node;
import gate.event.AnnotationSetListener;
import gate.event.GateListener;
import gate.util.InvalidOffsetException;

/**
 * Always-empty implementation of AnnotationSet. This is used as the
 * response to a request for a non-existent annotation set to avoid
 * actually creating the set on the document. Because of the caching
 * implemented in the doc service, if we were just to blindly do
 * doc.getAnnotations("nonExistentSet"), this would create an empty
 * annotation set called "nonExistentSet" on the cached copy of the
 * document in memory. Subsequent calls to listAnnotationSets would
 * include this nonExistentSet in the list of available set names. If
 * the same document receives a setAnnotationSet call before the
 * document has been evicted from the cache then the nonExistentSet will
 * be written to the datastore, but if it is evicted before it receives
 * any setAnnotationSet calls then the "phantom" set will disappear.
 * 
 * To avoid this inconsistency, when such a set is requested we return
 * an empty-set response to the caller but do not create the set on the
 * cached document. Only sets that have been previously saved will be
 * listed as existing by listAnnotationSets.
 */
class EmptyAnnotationSet implements AnnotationSet {
  
  private static final EmptyAnnotationSet instance = new EmptyAnnotationSet();
  
  static EmptyAnnotationSet getInstance() {
    return instance;
  }
  
  private EmptyAnnotationSet() {
    super();
  }
  
  public void add(Integer id, Long start, Long end, String type,
          FeatureMap features) throws InvalidOffsetException {
    throw new UnsupportedOperationException();
  }

  public void addAnnotationSetListener(AnnotationSetListener l) {
    throw new UnsupportedOperationException();
  }

  public void addGateListener(GateListener l) {
    throw new UnsupportedOperationException();
  }

  public Node firstNode() {
    throw new UnsupportedOperationException();
  }

  public AnnotationSet get(Long offset) {
    return this;
  }

  public AnnotationSet get(String type, FeatureMap constraints) {
    return this;
  }

  public AnnotationSet get(String type, Set featureNames) {
    return this;
  }

  public AnnotationSet get(Long startOffset, Long endOffset) {
    return this;
  }

  public AnnotationSet get(String type, FeatureMap constraints, Long offset) {
    return this;
  }

  public AnnotationSet get(String type, Long startOffset, Long endOffset) {
    return this;
  }

  public AnnotationSet getContained(Long startOffset, Long endOffset) {
    return this;
  }

  public AnnotationSet getCovering(String type, Long startOffset, Long endOffset) {
    return this;
  }

  public Node lastNode() {
    throw new UnsupportedOperationException();
  }

  public Node nextNode(Node node) {
    throw new UnsupportedOperationException();
  }

  public void removeAnnotationSetListener(AnnotationSetListener l) {
    throw new UnsupportedOperationException();
  }

  public void removeGateListener(GateListener l) {
    throw new UnsupportedOperationException();
  }

  public boolean add(Annotation a) {
    throw new UnsupportedOperationException();
  }

  public Integer add(Node start, Node end, String type, FeatureMap features) {
    throw new UnsupportedOperationException();
  }

  public Integer add(Long start, Long end, String type, FeatureMap features)
          throws InvalidOffsetException {
    throw new UnsupportedOperationException();
  }

  public AnnotationSet get() {
    return this;
  }

  public Annotation get(Integer id) {
    return null;
  }

  public AnnotationSet get(String type) {
    return this;
  }

  public AnnotationSet get(Set<String> types) {
    return this;
  }

  public Set<String> getAllTypes() {
    return Collections.emptySet();
  }

  public Document getDocument() {
    return null;
  }

  public String getName() {
    return null;
  }

  public Iterator<Annotation> iterator() {
    return Collections.<Annotation> emptyList().iterator();
  }

  public boolean remove(Object o) {
    return false;
  }

  public int size() {
    return 0;
  }

  public boolean addAll(Collection<? extends Annotation> c) {
    return false;
  }

  public void clear() {
    throw new UnsupportedOperationException();
  }

  public boolean contains(Object o) {
    return false;
  }

  public boolean containsAll(Collection<?> c) {
    return false;
  }

  public boolean isEmpty() {
    return true;
  }

  public boolean removeAll(Collection<?> c) {
    return false;
  }

  public boolean retainAll(Collection<?> c) {
    return false;
  }

  public Object[] toArray() {
    return new Annotation[0];
  }

  public <T> T[] toArray(T[] a) {
    return Collections.emptyList().toArray(a);
  }
}
