package gleam.docservice.proxy.impl.iaa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gleam.docservice.IAADetail;
import gleam.docservice.IAAResult;
import gleam.docservice.iaa.KappaDetailForAnnotator;
import gleam.docservice.iaa.KappaDetailForAnnotatorPairs;
import gleam.docservice.iaa.PairwiseKappaDetail;
import gleam.docservice.proxy.DSProxyException;
import gleam.docservice.proxy.IAAAlgorithm;
import gleam.docservice.proxy.iaa.KappaResult;
import gleam.docservice.proxy.iaa.PairwiseKappaIAAResult;

public class PairwiseKappaIAAResultImpl extends IAAResultImpl implements
                                                             PairwiseKappaIAAResult {

  private static final Log log = LogFactory
          .getLog(PairwiseKappaIAAResultImpl.class);

  private PairwiseKappaDetail detailFromServer;

  /**
   * Map to enable looking up details by pairs of annotation set names.
   */
  private TwoWayMap<String, KappaDetailForAnnotator> detailMap;

  private TwoWayMap<String, KappaResult> resultCache = new TwoWayMap<String, KappaResult>();

  public PairwiseKappaIAAResultImpl(IAAResult resultFromServer)
          throws DSProxyException {
    super(resultFromServer, IAAAlgorithm.PAIRWISE_KAPPA);
    IAADetail iaaDetail = resultFromServer.getDetail();
    if(!(iaaDetail instanceof PairwiseKappaDetail)) {
      log.error("Wrong detail type received from doc service.  Expected "
              + PairwiseKappaDetail.class.getName() + " but got "
              + iaaDetail.getClass().getName());
      throw new DSProxyException("Unexpected response type from doc service");
    }
    this.detailFromServer = (PairwiseKappaDetail)iaaDetail;

    this.detailMap = new TwoWayMap<String, KappaDetailForAnnotator>();
    KappaDetailForAnnotatorPairs[] pairsDetail = detailFromServer
            .getDetailForPairs();
    if(pairsDetail != null) {
      for(KappaDetailForAnnotatorPairs p : pairsDetail) {
        KappaDetailForAnnotator[] annotatorsDetail = p.getDetailForResponses();
        if(annotatorsDetail != null) {
          for(KappaDetailForAnnotator a : annotatorsDetail) {
            detailMap.put(p.getKeyAnnotationSetName(),
                    a.getAnnotationSetName(), a);
          }
        }
      }
    }
  }

  public float getOverallKappaCohen() {
    return detailFromServer.getOverallKappaCohen();
  }

  public float getOverallKappaPi() {
    return detailFromServer.getOverallKappaPi();
  }

  public float getOverallObservedAgreement() {
    return detailFromServer.getOverallObservedAgreement();
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

  public KappaResult getResult(String as1, String as2) {
    if(resultCache.containsKeys(as1, as2)) {
      return resultCache.get(as1, as2);
    }
    
    KappaDetailForAnnotator d = detailMap.get(as1, as2);
    if(d == null) {
      return null;
    }
    
    KappaResult result = new KappaResultImpl(d, getLabelValues());
    resultCache.put((String)detailMap.getFirstKey(as1, as2),
            (String)detailMap.getSecondKey(as1, as2), result);
    return result;
  }

}
