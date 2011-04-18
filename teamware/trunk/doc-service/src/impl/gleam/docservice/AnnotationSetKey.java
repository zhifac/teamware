/*
 *  AnnotationSetKey.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 23/May/2006
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
