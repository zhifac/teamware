package gleam.docservice.proxy.impl.iaa;

import gleam.docservice.proxy.IAAAlgorithm;

public class IAAResultImpl implements gleam.docservice.proxy.IAAResult {

  protected gleam.docservice.IAAResult resultFromServer;

  protected IAAAlgorithm algorithm;

  public IAAResultImpl(gleam.docservice.IAAResult resultFromServer,
          IAAAlgorithm algorithm) {
    this.resultFromServer = resultFromServer;
    this.algorithm = algorithm;
  }

  public float getAgreement() {
    return resultFromServer.getAgreement();
  }

  public IAAAlgorithm getAlgorithm() {
    return algorithm;
  }

  public String[] getLabelValues() {
    return resultFromServer.getLabelValues();
  }

}
