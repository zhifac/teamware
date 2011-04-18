package gleam.executive.webapp.form;

public class LabelValueDetailForm {
  private String labelValue;
  private String strictValues;
  private String lenientValues;
  private String otherValues;
  public String getLabelValue() {
    return labelValue;
  }
  public void setLabelValue(String labelValue) {
    this.labelValue = labelValue;
  }
  public String getLenientValues() {
    return lenientValues;
  }
  public void setLenientValues(String lenientValues) {
    this.lenientValues = lenientValues;
  }
  public String getOtherValues() {
    return otherValues;
  }
  public void setOtherValues(String otherValues) {
    this.otherValues = otherValues;
  }
  public String getStrictValues() {
    return strictValues;
  }
  public void setStrictValues(String strictValues) {
    this.strictValues = strictValues;
  }
}
