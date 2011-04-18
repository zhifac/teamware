/*
 * This file is part of SAFE, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.docservice.proxy;

/**
 * Information about a single language resource (document or corpus) in
 * the doc service.
 */
public interface LRInfo {
  /**
   * Get the ID of this document.
   */
  public String getID();

  /**
   * Get the name of this document.
   */
  public String getName();
  
  /**
   * Get the size of this document.
   */
  public int getSize();
}
