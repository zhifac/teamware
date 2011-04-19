/*
 *  FMeasure.java
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
package gleam.docservice.proxy.iaa;

/**
 * Interface representing a single F-measure statistic.
 */
public interface FMeasure {
  public float correct();
  public float partiallyCorrect();
  public float spurious();
  public float missing();
  
  public float precision();
  public float recall();
  public float f1();
  public float precisionLenient();
  public float recallLenient();
  public float f1Lenient();
}
