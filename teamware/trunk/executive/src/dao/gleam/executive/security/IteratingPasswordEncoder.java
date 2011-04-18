package gleam.executive.security;

import org.acegisecurity.providers.encoding.MessageDigestPasswordEncoder;

/**
 * PasswordEncoder that expects {@link SaltWithIterations} as its salt values,
 * and uses the iteration count specified by the salt to run the underlying
 * encoder repeatedly.
 */
public class IteratingPasswordEncoder extends MessageDigestPasswordEncoder {
  
  public IteratingPasswordEncoder() {
    this("SHA-1");
  }
  
  public IteratingPasswordEncoder(String algorithm) {
    super(algorithm);
  }

  @Override
  public String encodePassword(String rawPass, Object salt) {
    int iterations = 1;
    Object realSalt = salt;
    if(salt instanceof SaltWithIterations) {
      iterations = ((SaltWithIterations)salt).getIterations();
      realSalt = ((SaltWithIterations)salt).getSalt();
    }
    String encoded = rawPass;
    for(int i = 0; i < iterations; i++) {
      encoded = super.encodePassword(encoded, realSalt);
    }
    return encoded;
  }
}
