/*
 *  PairwiseFMeasureIAAResultImpl.java
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
package gleam.docservice.proxy.impl.iaa;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gleam.docservice.IAADetail;
import gleam.docservice.IAAResult;
import gleam.docservice.iaa.FMeasureDetailForAnnotator;
import gleam.docservice.iaa.FMeasureDetailForAnnotatorPairs;
import gleam.docservice.iaa.FMeasureDetailForLabel;
import gleam.docservice.iaa.PairwiseFMeasureDetail;
import gleam.docservice.proxy.DSProxyException;
import gleam.docservice.proxy.IAAAlgorithm;
import gleam.docservice.proxy.iaa.FMeasure;
import gleam.docservice.proxy.iaa.PairwiseFMeasureIAAResult;

public class PairwiseFMeasureIAAResultImpl extends IAAResultImpl implements
                                                                PairwiseFMeasureIAAResult {

  private static final Log log = LogFactory
          .getLog(PairwiseFMeasureIAAResultImpl.class);

  private PairwiseFMeasureDetail detailFromServer;

  /**
   * Map to enable looking up results by pairs of annotator names.
   */
  private TwoWayMap<String, FMeasureDetailForAnnotator> detailMap;

  /**
   * Map to enable looking up results by pairs of annotator names and
   * the label value.
   */
  private TwoWayMap<String, Map<String, FMeasureDetailForLabel>> labelDetailsMap;

  /**
   * We create the F-measure objects lazily and cache them.
   */
  private TwoWayMap<String, FMeasure> fMeasureCache = new TwoWayMap<String, FMeasure>();

  private TwoWayMap<String, Map<String, FMeasure>> fMeasureCacheForLabels = new TwoWayMap<String, Map<String, FMeasure>>();

  private FMeasure overallF;

  public PairwiseFMeasureIAAResultImpl(IAAResult resultFromServer)
          throws DSProxyException {
    super(resultFromServer, IAAAlgorithm.PAIRWISE_F_MEASURE);
    IAADetail iaaDetail = resultFromServer.getDetail();
    if(!(iaaDetail instanceof PairwiseFMeasureDetail)) {
      log.error("Wrong detail type received from doc service.  Expected "
              + PairwiseFMeasureDetail.class.getName() + " but got "
              + iaaDetail.getClass().getName());
      throw new DSProxyException("Unexpected response type from doc service");
    }
    this.detailFromServer = (PairwiseFMeasureDetail)iaaDetail;

    this.detailMap = new TwoWayMap<String, FMeasureDetailForAnnotator>();
    this.labelDetailsMap = new TwoWayMap<String, Map<String, FMeasureDetailForLabel>>();
    FMeasureDetailForAnnotatorPairs[] pairsDetail = detailFromServer
            .getDetailForPairs();
    if(pairsDetail != null) {
      for(FMeasureDetailForAnnotatorPairs p : pairsDetail) {
        FMeasureDetailForAnnotator[] annotatorsDetail = p
                .getDetailForResponses();
        if(annotatorsDetail != null) {
          for(FMeasureDetailForAnnotator a : annotatorsDetail) {
            detailMap.put(p.getKeyAnnotationSetName(),
                    a.getAnnotationSetName(), a);
            Map<String, FMeasureDetailForLabel> labelDetails = new HashMap<String, FMeasureDetailForLabel>();
            labelDetailsMap.put(p.getKeyAnnotationSetName(), a
                    .getAnnotationSetName(), labelDetails);
            if(a.getDetailForLabels() != null) {
              for(FMeasureDetailForLabel l : a.getDetailForLabels()) {
                labelDetails.put(l.getLabelValue(), l);
              }
            }
          }
        }
      }
    }

    overallF = new FMeasureImpl(detailFromServer.getOverallFMeasure());
  }

  public FMeasure getFMeasureForLabel(String as1, String as2, String labelValue) {
    Map<String, FMeasure> fmc = fMeasureCacheForLabels.get(as1, as2);
    if(fmc != null && fmc.containsKey(labelValue)) {
      return fmc.get(labelValue);
    }

    Map<String, FMeasureDetailForLabel> detailForAnnotator = labelDetailsMap
            .get(as1, as2);
    if(detailForAnnotator == null) {
      return null;
    }

    FMeasureDetailForLabel d = detailForAnnotator.get(labelValue);
    if(d == null) {
      return null;
    }

    FMeasure fm = new FMeasureImpl(d.getFMeasure());
    // create fmc if necessary
    if(fmc == null) {
      fmc = new HashMap<String, FMeasure>();
      fMeasureCacheForLabels.put((String)detailMap.getFirstKey(as1, as2),
              (String)detailMap.getSecondKey(as1, as2), fmc);
    }
    fmc.put(labelValue, fm);
    return fm;
  }

  public FMeasure getFMeasureForPair(String as1, String as2) {
    if(fMeasureCache.containsKeys(as1, as2)) {
      return fMeasureCache.get(as1, as2);
    }

    FMeasureDetailForAnnotator d = detailMap.get(as1, as2);
    if(d == null) {
      return null;
    }

    FMeasure fm = new FMeasureImpl(d.getOverallFMeasure());
    fMeasureCache.put((String)detailMap.getFirstKey(as1, as2),
            (String)detailMap.getSecondKey(as1, as2), fm);
    return fm;
  }

  public FMeasure getOverallFMeasure() {
    return overallF;
  }

  public String getKeyASName(String as1, String as2) {
    if(detailMap.containsKeys(as1, as2)) {
      return (String)detailMap.getFirstKey(as1, as2);
    }
    else {
      return null;
    }
  }

  public String getResponseASName(String as1, String as2) {
    if(detailMap.containsKeys(as1, as2)) {
      return (String)detailMap.getSecondKey(as1, as2);
    }
    else {
      return null;
    }
  }

}
