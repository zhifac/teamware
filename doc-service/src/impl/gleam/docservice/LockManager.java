/*
 *  LockManager.java
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

import java.util.Date;
import javax.servlet.ServletContext;

/**
 * Implementation of locking mechanism for serial doc service.
 * </p>
 * This class is singleton and supposed to exist as only one instance within
 * JVM.
 * </p>
 */
public abstract class LockManager {
	/** Debug flag */
	public static final boolean DEBUG = false;

	/** Heavy debug flag */
	public static final boolean DEBUG_DETAILS = false;

	private DeadLocksCleaner deadLockCleaner;

	/**
	 * Parameter name for location of serial datastore. Method
	 * {@link #init(ServletContext)} will look for a parameter with this name in
	 * the context.
	 */
	public static final String LOCK_MANAGER_PARAMETER_NAME = "lock-manager";

	public static final String DEFAULT_LOCK_MANAGER = "gleam.docservice.MapLockManager";

	public static final String DEAD_LOCK_TIMEOUT = "dead-locks-cleanup-timeout";
	
	//private static LockManager thisInstance;

	LockManager() {
		if (deadLockCleaner == null) deadLockCleaner = new DeadLocksCleaner();
	}
	
	public void init() {
	  //thisInstance = this;
		deadLockCleaner.start();
	}

	public long getDeadLockCleanupTimeout() {
		return deadLockCleaner.getTimeout();
	}

	public void setDeadLockCleanupTimeout(long timeout) {
		deadLockCleaner.setTimeout(timeout);
	}
	
	public void shutDown() throws Exception {
	  deadLockCleaner.interrupt();
	}

	protected void finalize() throws Throwable {
		shutDown();
	}

	/*
	 * abstract protected String createLock(AnnotationSet annSet) throws
	 * DocServiceException;
	 * 
	 * abstract public boolean containsLock(String taskID) throws Exception;
	 * 
	 * abstract public boolean containsLock(AnnotationSet annSet) throws
	 * Exception;
	 * 
	 * abstract public String getExistingLock(AnnotationSet annSet) throws
	 * Exception;
	 * 
	 * abstract public AnnotationSet getAnnotationSet(String taskID) throws
	 * Exception;
	 * 
	 * abstract protected boolean releaseLock(String taskID) throws Exception;
	 * 
	 * abstract protected boolean refreshLock(String taskID) throws Exception;
	 * 
	 * abstract protected void reset() throws Exception;
	 * 
	 * abstract protected Map getTaskIDTimes() throws Exception;
	 */
	// ==========
	abstract public Lock _createLock(String asName, String docId) throws Exception;

	abstract public Lock _getLock(int taskId) throws Exception;

	abstract public Lock _getLock(String asName, String docId) throws Exception;

	abstract public boolean _releaseLock(int taskId) throws Exception;

	abstract public Lock _refreshLock(int taskId) throws Exception;

	abstract public Lock[] _getAllLocks() throws Exception;

	/**
	 * This class relese locks older than {@link #timeout}.
	 */
	class DeadLocksCleaner extends Thread {
		/** Amont of time after which a lock considered as dead. */
		private long timeout = 60000L;

		public long getTimeout() {
			return timeout;
		}

		public void setTimeout(long timeout) {
			this.timeout = timeout;
		}

		public void run() {
			while (!interrupted()) {
				try {
					try {
					  sleep(10000);
					}
					catch(InterruptedException ie) {
					  break;
					}
					if (LockManager.this == null) continue;
					Lock[] ll = LockManager.this._getAllLocks();
					long currTime = System.currentTimeMillis();
					for (int i = 0; i < ll.length; i++) {
						if ((currTime - ll[i].getTime()) > timeout) {
							System.out.println("LockManager [" + new Date().toString() + "]: Timeout exceeded (" + timeout / 1000
									+ " sec) Clearing dead lock. " + ll[i].toString());
							LockManager.this._releaseLock(ll[i].getTaskId());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		/*
		 * while (!interrupted()) { try { sleep(10000); if (LockManager.thisInstance ==
		 * null) continue; Map m = LockManager.thisInstance.getTaskIDTimes();
		 * synchronized (m) { long currTime = System.currentTimeMillis(); Iterator
		 * itr = m.entrySet().iterator(); while (itr.hasNext()) { Map.Entry e =
		 * (Map.Entry) itr.next(); if ((currTime - ((Long)
		 * e.getValue()).longValue()) > timeout) { System.out.println("LockManager [" +
		 * new Date().toString() + "]: Clearing dead lock. TaskID='" + e.getKey());
		 * LockManager.thisInstance.releaseLock((String) e.getKey()); } } } } catch
		 * (Exception e) { e.printStackTrace(); } } }
		 */
	}
}
