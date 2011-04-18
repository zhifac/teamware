package gleam.executive.service;

public interface SecurityTokenManager {
  
  /**
   * Requests the creation of a new token.
   * @return the ID for the new token.
   */
  public String newToken();
  
  /**
   * Checks if a token is still valid.
   * @param tokenID the ID for the token to be verified.
   * @return <code>true</code> iff the token with the specified ID was found and
   * is still valid.
   */
  public boolean isValid(String tokenID);
  
}
