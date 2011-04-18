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
