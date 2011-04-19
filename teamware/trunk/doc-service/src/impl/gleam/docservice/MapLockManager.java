/*
 *  MapLockManager.java
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapLockManager extends LockManager {
	protected Map locks;

	public MapLockManager() {
		this.locks = new HashMap();
	}

	public Lock _createLock(String annSetName, String docId) throws Exception {
		Lock l = (Lock) locks.get(new Lock(annSetName, docId));
		if (l != null) {
			throw new DocServiceException("Can't lock "
					+ ((annSetName == null) ? "'default annotation set'" : ("annotation set '" + annSetName + "'"))
					+ " already locked.");
		}
		l = new Lock(annSetName, docId);
		locks.put(l, l);
		return l;
	}

	public Lock[] _getAllLocks() throws Exception {
		Lock[] ll = new Lock[locks.size()];
		locks.values().toArray(ll);
		return ll;
	}

	public Lock _getLock(String asName, String docId) throws Exception {
		return (Lock) locks.get(new Lock(asName, docId));
	}

	public Lock _getLock(int taskId) throws Exception {
		Iterator itr = this.locks.values().iterator();
		while (itr.hasNext()) {
			Lock l = (Lock) itr.next();
			if (l.getTaskId() == taskId) return l;
		}
		return null;
	}

	public Lock _refreshLock(int taskId) throws Exception {
		Iterator itr = this.locks.values().iterator();
		while (itr.hasNext()) {
			Lock l = (Lock) itr.next();
			if (l.getTaskId() == taskId) {
				l = new Lock(taskId, l.getAnnSetName(), l.getDocId(), System.currentTimeMillis());
				locks.put(l, l);
				return l;
			}
		}
		return null;
	}

	public boolean _releaseLock(int taskId) throws Exception {
		Iterator itr = this.locks.values().iterator();
		while (itr.hasNext()) {
			Lock l = (Lock) itr.next();
			if (l.getTaskId() == taskId) {
				itr.remove();
				return true;
			}
		}
		return false;
	}
}
