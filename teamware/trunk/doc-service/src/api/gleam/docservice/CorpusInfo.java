package gleam.docservice;

public class CorpusInfo {
	private String corpusID, corpusName;

	private int numberOfDocuments;
  public CorpusInfo() {
  }

	public CorpusInfo(String corpusID, String corpusName) {
		this.corpusID = corpusID;
		this.corpusName = corpusName;
	}
	
	public CorpusInfo(String corpusID, String corpusName, int numberOfDocuments) {
		this.corpusID = corpusID;
		this.corpusName = corpusName;
		this.numberOfDocuments = numberOfDocuments;
	}
	

	public String getCorpusID() {
		return corpusID;
	}

	public void setCorpusID(String corpusID) {
    this.corpusID = corpusID;
  }

  public String getCorpusName() {
		return corpusName;
	}
  
   

  public void setCorpusName(String corpusName) {
    this.corpusName = corpusName;
  }
  
	public int getNumberOfDocuments() {
		return numberOfDocuments;
	}

	public void setNumberOfDocuments(int numberOfDocuments) {
		this.numberOfDocuments = numberOfDocuments;
	}

	@Override
	public String toString() {
		return "Docservice corpus info. ID=" + corpusID + " name=" + corpusName;
	}
}
