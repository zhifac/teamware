/*
 *  DocServiceCorpusImpl.java
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

package gleam.docservice.gate;

import gate.Factory;
import gate.Gate;
import gate.Resource;
import gate.corpora.DocumentData;
import gate.corpora.SerialCorpusImpl;
import gate.persist.PersistenceException;
import gate.security.SecurityException;
import gate.util.Err;
import gate.util.Out;
import gleam.docservice.DocService;
import gleam.docservice.DocumentInfo;

import java.util.ArrayList;

public class DocServiceCorpusImpl extends SerialCorpusImpl {
	/** Debug flag */
	private static final boolean DEBUG = false;

	public DocServiceCorpusImpl() {
		Gate.getCreoleRegister().addCreoleListener(this);
	}

	public void init(DocService sds) {
		if (DEBUG) System.out.println("DEBUG: " + this.getClass().getName() + ".init()" + "'" + " sds=" + sds);
		this.setName(sds.getCorpusInfo((String) getLRPersistenceId()).getCorpusName());
		this.setFeatures(Factory.newFeatureMap());

		this.documents = new ArrayList();
		this.docDataList = new ArrayList();
		DocumentInfo[] docInfos = sds.listDocs((String) getLRPersistenceId());
		if(docInfos != null) {
  		if (DEBUG)
  			System.out.println("DEBUG: " + this.getClass().getName() + ".init()" + " num of documents =" + docInfos.length);
  		for (int i = 0; i < docInfos.length; i++) {
  			this.docDataList.add(new DocumentData(docInfos[i].getDocumentName(), docInfos[i].getDocumentID()));
  			this.documents.add(null);
  		}
		}
	}

	public Object get(int index) {
		if (index >= docDataList.size()) return null;
		Object res = documents.get(index);
		if (DEBUG) Out.prln("DEBUG: " + this.getClass().getName() + ".get(): index=" + index + " result: " + res);
		if (res == null) {
			try {
				Resource lr = this.dataStore.getLr(DocServiceDataStore.DOCUMENT_CLASS_NAME, (String)((DocumentData) docDataList
						.get(index)).getPersistentID());
				if (DEBUG)
					Out.prln("DEBUG: " + this.getClass().getName() + ".get(): index=" + index + " Loaded document :"
							+ lr.getName());
				res = lr;
				// finally replace the doc with the instantiated version
				this.documents.set(index, lr);
			} catch (PersistenceException e) {
				Err.prln("Error reading document inside a DocService corpus.");
				e.printStackTrace();
			} catch (SecurityException e) {
				Err.prln("Error reading document inside a DocService corpus.");
				e.printStackTrace();
			}
		}
		return res;
	}
}
