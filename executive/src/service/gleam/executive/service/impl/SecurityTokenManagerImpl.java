package gleam.executive.service.impl;

import gleam.executive.service.SecurityTokenManager;

import java.security.SecureRandom;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A simple security token manager implementation. It provides token IDs, which
 * can later be checked for validity. Token IDs expire automatically after a 
 * given period (which can be set). Testing a token ID for validity resets its 
 * time counter, thus delaying its expiration.
 */
public class SecurityTokenManagerImpl implements SecurityTokenManager{
  protected final Log log = LogFactory.getLog(getClass());

  /**
   * Representation of a security token.
   */
  protected class SecurityToken{
    
    /**
     * Creates a new security token with a given ID.
     * @param id
     */
    public SecurityToken(String id) {
      this.id = id;
      touch();
    }
    
    public String getId() {
      return id;
    }

    /**
     * Get the last time this token was touched (see {@link #touch()}). 
     * @return a long value, as returned by {@link System#currentTimeMillis()}.
     */
    public long getLastAccessTime() {
      return lastAccessTime;
    }

    /**
     * Sets the last access time to the current time.
     */
    public void touch(){
      lastAccessTime = System.currentTimeMillis();
    }
    
    /**
     * The ID for this security token.
     */
    private final String id;
    
    /**
     * The last access time for this session. This is initialised to the current
     * time on creation, and updated every time {@link #touch()} is called.
     */
    private long lastAccessTime;
  }

  
  
  /**
   * The default value for the amount of inactivity time (in milliseconds) after
   * which a session ID expires.
   */
  public static final int DEFAULT_TOKEN_TIMEOUT = 30 * 60 * 1000;
  
  /**
   * How frequently do we remove expired session IDs? 
   */
  protected static final int TIMER_PERIOD = 30 * 1000;
  
  /**
   * How many characters are in a token ID?
   */
  protected static final int TOKEN_ID_LENGTH = 6;
  
  /**
   * The characters that are permitted inside a token ID. 
   */
  protected static final char[] PERMITTED_CHARS;
  
  /**
   * A random number generator used to create random token IDs. 
   */
  protected static final Random random;
  
  /**
   * Static initialiser
   */
  static{
    // permitted chars are letters and digits. 
    List<Character> chars = new LinkedList<Character>();
    for(char c ='a';  c <= 'z'; c++){
      chars.add(c);
    }
    for(char c ='0';  c <= '9'; c++){
      chars.add(c);
    }
    for(char c ='A';  c <= 'Z'; c++){
      chars.add(c);
    }    
    PERMITTED_CHARS = new char[chars.size()];
    int i = 0;
    for(Character c : chars){
      PERMITTED_CHARS[i++] = c;
    }
    
    random = new SecureRandom();
  }
  
  /**
   * The set of known security tokens. This is a map from token ID to 
   * {@link SecurityToken}. There is a timer task that removes expired tokens
   * at regular intervals (see {@link #TIMER_PERIOD}). 
   */
  protected Map<String, SecurityToken> knownTokens;
  
  /**
   * Timer used to expire old security tokens. 
   */
  protected Timer expirationTimer;
  
  /**
   * How many milliseconds until session expire since the last access.
   */
  private long tokenTimeout = DEFAULT_TOKEN_TIMEOUT;
  
  
  /**
   * Gets the current value for the token timeout. Defaults to 
   * {@link #DEFAULT_TOKEN_TIMEOUT}.
   * @return
   */
  public synchronized long getTokenTimeout() {
    return tokenTimeout;
  }


  /**
   * Sets the new value for the token timeout. Defaults to 
   * {@link #DEFAULT_TOKEN_TIMEOUT}. This new value will be used starting from 
   * the next time the set of known security tokens is filtered out for expired
   * tokens. If a filtering operation is currently active, this method call will
   * wait for that operation to complete, before setting the new value and 
   * returning. 
   */  
  public synchronized void setTokenTimeout(long sessionTimeout) {
    this.tokenTimeout = sessionTimeout;
  }


  /**
   * Creates a new SimpleSessionManager.
   */
  public SecurityTokenManagerImpl() {
    //create a LRU map for the sessions.
    knownTokens = new LinkedHashMap<String, SecurityToken>();
    expirationTimer = new Timer(true);
    expirationTimer.schedule(new TimerTask() {
      public void run() {
        expireOldTokens();
      }
    }, TIMER_PERIOD, TIMER_PERIOD);
  }


  public synchronized boolean isValid(String tokenID) {
    boolean valid = false;
    SecurityToken token = knownTokens.remove(tokenID);
    if(token != null){
      token.touch();
      knownTokens.put(token.getId(), token);
      valid = true;
    }
    if(log.isDebugEnabled()) {
      log.debug("Security token " + tokenID + " is " + (valid ? "" : "NOT ") + "valid");
    }
    return valid;
  }


  public synchronized String newToken() {
    SecurityToken token = new SecurityToken(generateNewId());
    knownTokens.put(token.getId(), token);
    if(log.isDebugEnabled()) {
      log.debug("Issued new security token " + token.getId());
    }
    return token.getId();
  }


  /**
   * Removes all security tokens that have note been refreshed (through 
   * creation or by calling {@link #isValid(String)}) for longer than the 
   * timeout period (see {@link #getTokenTimeout()}).  
   */
  protected synchronized void expireOldTokens(){
    long timeNow = System.currentTimeMillis();
    //iterate through the known sessions and remove all expired ones.
    Iterator<SecurityToken> iter = knownTokens.values().iterator();
    while(iter.hasNext()){
      SecurityToken aToken = iter.next();
      if(timeNow - aToken.getLastAccessTime() > tokenTimeout) {
        iter.remove();
        log.debug("Token " + aToken.getId() + " expired");
      }
      else {
        break;
      }
    }    
    
  }

  /**
   * Generates a new random string that can be used as a token ID.  
   * @return
   */
  protected static synchronized String generateNewId(){
    StringBuilder str = new StringBuilder(TOKEN_ID_LENGTH);
    for(int i = 0; i < TOKEN_ID_LENGTH; i++){
      str.append(PERMITTED_CHARS[random.nextInt(PERMITTED_CHARS.length)]);
    }
    return str.toString();
  }

}
