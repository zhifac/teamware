package gleam.executive.security;

import java.util.Random;

/**
 * A compound salt that specifies the number of iterations of
 * the underlying message digest that should be performed.
 */
public class SaltWithIterations {
  private static Random rand = new Random();
  
  /**
   * The actual salt.
   */
  private String salt;
  
  /**
   * The number of iterations.
   */
  private int iterations;

  public SaltWithIterations(String salt, int iterations) {
    super();
    this.salt = salt;
    this.iterations = iterations;
  }

  public String getSalt() {
    return salt;
  }

  public int getIterations() {
    return iterations;
  }
  
  /**
   * Utility method to create a random salt/iterations pair.
   */
  public static SaltWithIterations createRandom() {
    return new SaltWithIterations(Integer.toHexString(rand.nextInt()),
            10000 + rand.nextInt(2000));
  }
}
