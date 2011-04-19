/*
 *  DocServiceInternalDataStore.java
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

import gate.persist.LuceneDataStoreImpl;
import gate.persist.SerialDataStore;

/**
 * DataStore class used internally by the doc service. This class is a
 * trivial subclass of LuceneDataStoreImpl, which exists simply to
 * override {@link SerialDataStore#constructPersistenceId} to have
 * public visibility.
 * 
 * Normally, the persistence ID for a newly adopted LR is only generated
 * during the call to <code>sync</code>. This means that it is not
 * possible for the doc service implementation to acquire the write lock
 * for a new LR until after it has been sync'ed. This means that another
 * thread could potentially get in between the time the file is created
 * on disk and the time the file's contents have been fully written,
 * which would cause an exception. With a public
 * <code>constructPersistenceId</code> method we can generate a
 * persistence ID first, then acquire the lock for that ID before
 * calling <code>sync</code> to prevent this race condition.
 */
public class DocServiceInternalDataStore extends LuceneDataStoreImpl {

  public DocServiceInternalDataStore() {
    super();
  }

  @Override
  public String constructPersistenceId(String lrName) {
    return super.constructPersistenceId(lrName);
  }
}
