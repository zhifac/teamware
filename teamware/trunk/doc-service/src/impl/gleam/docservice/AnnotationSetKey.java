/*
 *  AnnotationSetKey.java
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

import gate.AnnotationSet;

import java.security.InvalidParameterException;

/**
 * Utility class to wrap annotation set and allow it to be stored as a key in
 * maps.<br>
 */
class AnnotationSetKey {
	private static final boolean DEBUG = false;

	private static final boolean DEBUG_DETAILS = false;

	private AnnotationSet annotationSet;

	private AnnotationSetKey() {
	}

	public AnnotationSetKey(AnnotationSet as) {
		if (as == null) throw new InvalidParameterException("Annotation set must not be null.");
		this.annotationSet = as;
	}

	public AnnotationSet getAnnotationSet() {
		return annotationSet;
	}

	public void setAnnotationSet(AnnotationSet annotationSet) {
		this.annotationSet = annotationSet;
	}

	public boolean equals(Object o) {
		if (o instanceof AnnotationSetKey) {
			AnnotationSetKey ask = (AnnotationSetKey) o;
			if (this.annotationSet == ask.getAnnotationSet()) {
				if (DEBUG_DETAILS) {
					System.out.println("DEBUG: AnnotationSetKey.equals[return true]: this == Obj");
				}
				return true;
			}
			if (!this.annotationSet.getDocument().equals(ask.getAnnotationSet().getDocument())) {
				if (DEBUG_DETAILS) {
					System.out.println("DEBUG: AnnotationSetKey.equals[return false]: this.doc NEQ Obj.doc");
				}
				return false;
			}
			if (!this.annotationSet.getName().equals(ask.getAnnotationSet().getName())) {
				if (DEBUG_DETAILS) {
					System.out.println("DEBUG: AnnotationSetKey.equals[return false]: this.asName NEQ Obj.asName. ["
							+ this.annotationSet.getName() + "," + ask.getAnnotationSet().getName() + "]");
				}
				return false;
			}
			if (DEBUG_DETAILS) {
				System.out.println("DEBUG: AnnotationSetKey.equals[return true]");
			}
			return true;
		} else {
			if (DEBUG_DETAILS) {
				System.out.println("DEBUG: AnnotationSetKey.equals[return false]: not instance of 'AnnotationSetKey'");
			}
			return false;
		}
	}

	public int hashCode() {
		int h = this.annotationSet.getDocument().hashCode();
		if (this.annotationSet.getName() != null) h = h ^ this.annotationSet.getName().hashCode();
		if (DEBUG_DETAILS) {
			System.out.println("DEBUG: AnnotationSetKey.hashCode=" + h);
		}
		return h;
	}

	public String toString() {
		return "AnnotationSetKey: hash='" + hashCode() + "' asName='" + this.annotationSet.getName() + "' doc-hash="
				+ this.annotationSet.getDocument().hashCode();
	}
}
