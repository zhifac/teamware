/*
 *  ConfusionMatrixRowForm.java
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
package gleam.executive.webapp.form;

import java.util.HashMap;
import java.util.Map;

public class ConfusionMatrixRowForm {
  private String keyLabel;
  private String specificAgreementPositive;
  private String specificAgreementNegative;
  private Map<String, Float> entries;

  public Map<String, Float> getEntries() {
    return entries;
  }

  public void setEntries(Map<String, Float> entries) {
    this.entries = entries;
  }
  
  public void setEntry(String label, Float entry) {
    if(entries == null) {
      entries = new HashMap<String, Float>();
    }
    entries.put(label, entry);
  }

  public String getKeyLabel() {
    return keyLabel;
  }

  public void setKeyLabel(String keyLabel) {
    this.keyLabel = keyLabel;
  }

  public String getSpecificAgreementNegative() {
    return specificAgreementNegative;
  }

  public void setSpecificAgreementNegative(String specificAgreementNegative) {
    this.specificAgreementNegative = specificAgreementNegative;
  }

  public String getSpecificAgreementPositive() {
    return specificAgreementPositive;
  }

  public void setSpecificAgreementPositive(String specificAgreementPositive) {
    this.specificAgreementPositive = specificAgreementPositive;
  }
  
}
