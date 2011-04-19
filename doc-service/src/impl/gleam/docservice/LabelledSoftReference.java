/*
 *  LabelledSoftReference.java
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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * A SoftReference that holds a label as well as a soft referent. Useful
 * when using soft references in a Map (where the label would be the key
 * that the reference is stored under in the map).
 * 
 * @author ian
 * 
 * @param <L> the type of the label
 * @param <T> the type of the referent of the soft reference
 */
public class LabelledSoftReference<L, T> extends SoftReference<T> {

  private L label;

  public LabelledSoftReference(L label, T referent) {
    super(referent);
    this.label = label;
  }

  public LabelledSoftReference(L label, T referent, ReferenceQueue<? super T> q) {
    super(referent, q);
    this.label = label;
  }

  public L getLabel() {
    return label;
  }
}
