/*
 *  AllWaysFMeasureIAAResultImpl.java
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

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import gleam.docservice.IAADetail;
import gleam.docservice.IAAResult;
import gleam.docservice.iaa.AllWaysFMeasureDetail;
import gleam.docservice.iaa.FMeasureDetailForAnnotator;
import gleam.docservice.iaa.FMeasureDetailForLabel;
import gleam.docservice.proxy.DSProxyException;
import gleam.docservice.proxy.IAAAlgorithm;
import gleam.docservice.proxy.iaa.AllWaysFMeasureIAAResult;
import gleam.docservice.proxy.iaa.FMeasure;

/**
 * Implementation class for the all-ways F-measure IAA results.
 */
public class AllWaysFMeasureIAAResultImpl extends IAAResultImpl implements
                                                               AllWaysFMeasureIAAResult {

  private static final Log log = LogFactory
          .getLog(AllWaysFMeasureIAAResultImpl.class);

  /**
   * The IAADetail element returned from the doc service.
   */
  private AllWaysFMeasureDetail detailFromServer;

  /**
   * Map to enable looking up results by annotation set name.
   */
  private Map<String, FMeasureDetailForAnnotator> detailMap;

  /**
   * Map to enable looking up results by annotation set name and label.
   */
  private Map<String, Map<String, FMeasureDetailForLabel>> labelDetailsMap;

  /**
   * We create the FMeasure objects lazily and cache them.
   */
  private Map<String, FMeasure> fMeasureCache = new HashMap<String, FMeasure>();

  private Map<String, Map<String, FMeasure>> fMeasureCacheForLabels = new HashMap<String, Map<String, FMeasure>>();

  private FMeasure overallF;

  /**
   * Create a result object from the given server result.
   */
  public AllWaysFMeasureIAAResultImpl(IAAResult resultFromServer)
          throws DSProxyException {
    super(resultFromServer, IAAAlgorithm.ALL_WAYS_F_MEASURE);
    IAADetail iaaDetail = resultFromServer.getDetail();
    if(!(iaaDetail instanceof AllWaysFMeasureDetail)) {
      log.error("Wrong detail type received from doc service.  Expected "
              + AllWaysFMeasureDetail.class.getName() + " but got "
              + iaaDetail.getClass().getName());
      throw new DSProxyException("Unexpected response type from doc service");
    }
    this.detailFromServer = (AllWaysFMeasureDetail)iaaDetail;

    FMeasureDetailForAnnotator[] detailForAnnotators = detailFromServer
            .getDetailForAnnotators();
    this.detailMap = new HashMap<String, FMeasureDetailForAnnotator>();
    this.labelDetailsMap = new HashMap<String, Map<String, FMeasureDetailForLabel>>();
    if(detailForAnnotators != null) {
      for(FMeasureDetailForAnnotator d : detailForAnnotators) {
        detailMap.put(d.getAnnotationSetName(), d);
        Map<String, FMeasureDetailForLabel> labelDetails = new HashMap<String, FMeasureDetailForLabel>();
        labelDetailsMap.put(d.getAnnotationSetName(), labelDetails);
        if(d.getDetailForLabels() != null) {
          for(FMeasureDetailForLabel l : d.getDetailForLabels()) {
            labelDetails.put(l.getLabelValue(), l);
          }
        }
      }
    }

    overallF = new FMeasureImpl(detailFromServer.getOverallFMeasure());
  }

  public FMeasure getFMeasure(String asName) {
    if(fMeasureCache.containsKey(asName)) {
      return fMeasureCache.get(asName);
    }

    FMeasureDetailForAnnotator d = detailMap.get(asName);
    if(d == null) {
      return null;
    }

    FMeasure fm = new FMeasureImpl(d.getOverallFMeasure());
    fMeasureCache.put(asName, fm);
    return fm;
  }

  public FMeasure getFMeasureForLabel(String asName, String label) {
    Map<String, FMeasure> fmc = fMeasureCacheForLabels.get(asName);
    if(fmc != null && fmc.containsKey(label)) {
      return fmc.get(label);
    }

    Map<String, FMeasureDetailForLabel> detailForAnnotator = labelDetailsMap
            .get(asName);
    if(detailForAnnotator == null) {
      return null;
    }

    FMeasureDetailForLabel d = detailForAnnotator.get(label);
    if(d == null) {
      return null;
    }

    FMeasure fm = new FMeasureImpl(d.getFMeasure());
    // create fmc if necessary
    if(fmc == null) {
      fmc = new HashMap<String, FMeasure>();
      fMeasureCacheForLabels.put(asName, fmc);
    }
    fmc.put(label, fm);
    return fm;
  }

  public FMeasure getOverallFMeasure() {
    return overallF;
  }
}
