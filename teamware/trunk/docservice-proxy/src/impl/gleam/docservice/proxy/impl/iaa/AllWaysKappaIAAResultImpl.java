package gleam.docservice.proxy.impl.iaa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gleam.docservice.IAADetail;
import gleam.docservice.IAAResult;
import gleam.docservice.iaa.AllWaysKappaDetail;
import gleam.docservice.proxy.DSProxyException;
import gleam.docservice.proxy.IAAAlgorithm;
import gleam.docservice.proxy.iaa.AllWaysKappaIAAResult;

/**
 * Implementation class for the all-ways kappa IAA results.
 */
public class AllWaysKappaIAAResultImpl extends IAAResultImpl implements
                                                            AllWaysKappaIAAResult {
  private static final Log log = LogFactory
          .getLog(AllWaysKappaIAAResultImpl.class);

  /**
   * The detail object returned from the doc service.
   */
  private AllWaysKappaDetail detailFromServer;

  public AllWaysKappaIAAResultImpl(IAAResult resultFromServer)
          throws DSProxyException {
    super(resultFromServer, IAAAlgorithm.ALL_WAYS_KAPPA);
    IAADetail iaaDetail = resultFromServer.getDetail();
    if(!(iaaDetail instanceof AllWaysKappaDetail)) {
      log.error("Wrong detail type received from doc service.  Expected "
              + AllWaysKappaDetail.class.getName() + " but got "
              + iaaDetail.getClass().getName());
      throw new DSProxyException("Unexpected response type from doc service");
    }
    this.detailFromServer = (AllWaysKappaDetail)iaaDetail;
  }

  public float getKappaDF() {
    return detailFromServer.getOverallKappaDF();
  }

  public float getKappaSC() {
    return detailFromServer.getOverallKappaSC();
  }

  public float getObservedAgreement() {
    return detailFromServer.getOverallObservedAgreement();
  }

}
