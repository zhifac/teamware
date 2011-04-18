/*
 *  MapLockManager.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 31/May/2006
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
