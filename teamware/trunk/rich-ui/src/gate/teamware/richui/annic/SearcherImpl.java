/*
 *  SearcherImpl.java
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
package gate.teamware.richui.annic;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gate.creole.annic.Constants;
import gate.creole.annic.Hit;
import gate.creole.annic.SearchException;
import gate.creole.annic.Searcher;
import gate.creole.annic.lucene.StatsCalculator;
import gleam.docservice.proxy.DSProxyException;
import gleam.docservice.proxy.DocServiceProxy;

/**
 * SearcherImpl is an another implementation of the Searcher interface from the
 * GATE. This is the heart of the GUI. The searcher objects talks to the
 * doc-service and provides various methods which are used by the searchGUI to
 * populated data into it. we provide the nexessary parameters, e.g. a proxy
 * object obtained from a docservice and the corpus ID in which we want to
 * search patterns
 * 
 * @author niraj
 * 
 */
public class SearcherImpl implements Searcher {

	/**
	 * doc-service proxy - that helps us obtaining necessary information from
	 * the index
	 */
	private DocServiceProxy proxy;

	/**
	 * Id of the searcher. It is possible that multiple searchers are querying
	 * doc-service for different pattern search. Therefore doc-service creates a
	 * new searcher id for individual searchers, which it returns when the
	 * search is invoked. User can use this searcherID to obtain results for
	 * which it issued a query.
	 * 
	 */
	private String searcherId;

	/**
	 * The latest query is stored i nthis object.
	 */
	private String query = "";

	/**
	 * Necessary paramters needed for issuing a query are stored in here.
	 */
	private Map<String, Object> parameters;

	/**
	 * The latest results are stored in this array. This is a sort of caching.
	 * If user needs to know the latest returned results, we can simply return
	 * them from this cache.
	 */
	private Hit[] latestResults;

	/**
	 * Corpus ID for which we issue our query.
	 */
	private String corpusId;

	/**
	 * Constructor
	 * 
	 * @param proxy
	 * @param corpusId
	 */
	public SearcherImpl(DocServiceProxy proxy, String corpusId) {
		this.proxy = proxy;
		this.corpusId = corpusId;
	}

	/**
	 * This method issues the search query to the doc-service, obtains results.
	 * If no results found it returns false. If there were results found, it
	 * returns true. User can then proceed to obtaining results using the next
	 * method. There must be atleast two parameters specified for the
	 * "parameters" object of Map. 1. context_window - that tells how many
	 * tokens should be displayed in the left and right of the found pattern. 2.
	 * annotation_set_id - that tells in which annotation set we want to search
	 * in.
	 */
	public boolean search(String query, Map<String, Object> parameters) throws SearchException {
		Integer contextWindow = (Integer) parameters
				.get(gate.creole.annic.Constants.CONTEXT_WINDOW);
		String annotationSetToSearchIn = (String) parameters
				.get(Constants.ANNOTATION_SET_ID);

		this.query = query;
		int cw = 4;
		if (contextWindow != null) {
			cw = contextWindow.intValue();
		}

		// we use document proxy to issue a query
		try {
			searcherId = this.proxy.startSearch(query, corpusId,
					annotationSetToSearchIn, cw);
			this.parameters = parameters;
			if (searcherId == null) {
				System.out.println("No result found for the query :"
						+ searcherId + " on corpus " + corpusId);
				this.latestResults = null;
				return false;
			}
		} catch (DSProxyException dpe) {
			throw new SearchException(dpe);
		}
		return true;
	}

	/**
	 * Returns the latest issued query.
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Obtains the next numberOfHits patterns for the latest search. It is
	 * gurantteed that for this next call, it will return maximum the
	 * numberOfHits hits. It may show less than that if no further patterns are
	 * found.
	 */
	public Hit[] next(int numberOfHits) throws SearchException {
		try {
			latestResults = this.proxy.getNextResults(this.searcherId,
					numberOfHits);
			return getHits();
		} catch (DSProxyException dpe) {
			throw new SearchException(dpe);
		}
	}

	/**
	 * This method returns a set of annotation set names that are indexed. Each
	 * entry is consist of corpusID;annotationSetName.
	 * 
	 * @indexLocation
	 * @return
	 */
	public String[] getIndexedAnnotationSetNames()
			throws SearchException {
		try {
			Set<String> toReturn = new HashSet<String>();
			String[] results = this.proxy.getIndexedAnnotationSetNames();

			// we are only interested in the annotation set names that belong to
			// the specified corpus ID
			if (results != null) {
				for (String s : results) {
					if (s.startsWith(corpusId + ";")) {
						toReturn.add(s);
					}
				}
			}
			return toReturn.toArray(new String[0]);
		} catch (DSProxyException dpe) {
			throw new SearchException(dpe);
		}
	}

	/**
	 * This method returns every possible annotationType.Features that are
	 * indexed in the datastore for the corpus with id set to corpusID as
	 * specified in the constructor. The key is :
	 * corpusID;annotationSetID;annotationType and the value is a list of
	 * featurenames for this annotation type.
	 */
	public Map<String, List<String>> getAnnotationTypesMap() {
		try {
			Map<String, List<String>> map = this.proxy.getAnnotationTypesForAnnic();
			Map<String, List<String>> toReturn = new HashMap<String, List<String>>();
			if (map == null) {
				return toReturn;
			}

			for (String key : map.keySet()) {
				if (key.startsWith(corpusId + ";")) {
					List<String> vals = map.get(key);
					toReturn.put(key, vals);
				}
			}

			return toReturn;
		} catch (DSProxyException dpe) {
			throw new RuntimeException(dpe);
		}
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void exportResults(File outputFile) {
		// TODO Auto-generated method stub

	}

	public Hit[] getHits() {
		return latestResults == null ? new Hit[0] : latestResults;
	}

  public int freq(String corpusToSearchIn,
          String annotationSetToSearchIn, String annotationType,
          String featureName, String value) throws SearchException {
    try {
      return this.proxy.getFreq(corpusToSearchIn,
          annotationSetToSearchIn, annotationType,
          featureName, value);
    } catch (DSProxyException e) {
      throw new SearchException(e);
    }
  }

  public int freq(String corpusToSearchIn,
          String annotationSetToSearchIn, String annotationType)
          throws SearchException {
    return this.freq(corpusToSearchIn,
          annotationSetToSearchIn, annotationType,
          null, null);
  }

  public int freq(String corpusToSearchIn,
          String annotationSetToSearchIn, String annotationType,
          String featureName) throws SearchException {
    return this.freq(corpusToSearchIn,
          annotationSetToSearchIn, annotationType,
          featureName, null);
  }

  public int freq(List<Hit> patternsToSearchIn,
          String annotationType, String feature, String value,
          boolean inMatchedSpan, boolean inContext) throws SearchException {
    return StatsCalculator.freq(patternsToSearchIn,
          annotationType, feature, value,
          inMatchedSpan, inContext);
  }

  public int freq(List<Hit> patternsToSearchIn,
          String annotationType, boolean inMatchedSpan, boolean inContext) throws SearchException {
    return StatsCalculator.freq(patternsToSearchIn,
          annotationType, inMatchedSpan, inContext);
  }

  public Map<String, Integer> freqForAllValues(
          List<Hit> patternsToSearchIn, String annotationType,
          String feature, boolean inMatchedSpan, boolean inContext)
          throws SearchException {
    return StatsCalculator.freqForAllValues(
          patternsToSearchIn, annotationType,
          feature, inMatchedSpan, inContext);
  }

}
