/*
 *  CorpusInfo.java
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
