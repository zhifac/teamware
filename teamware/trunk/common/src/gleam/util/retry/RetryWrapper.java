/*
 *  RetryWrapper.java
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
package gleam.util.retry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is an {@link InvocationHandler} to wrap an object that
 * implements an interface with a proxy implementing the same interface
 * that calls the real object's methods, retrying if the call throws an
 * exception. The exception types that trigger retry are configurable.
 * If a retry is necessary, the wrapper waits a random number of
 * milliseconds before retrying, then uses an exponential backoff
 * strategy - wait N ms, then FN, FFN, etc. up to the maximum number of
 * retries. The minimum and maximum wait before the initial retry, the
 * backoff factor (F), and the number of retries are all configurable.
 * The static {@link #getRetryWrapper} method provides a convenient way
 * to obtain a {@link Proxy} for such an object which implements the
 * same interface.
 * 
 * @author ian
 */
public class RetryWrapper implements InvocationHandler {

  /**
   * Log object used for exceptions. The logger used is based on the
   * class of the real object.
   */
  private Log log;

  /**
   * The real object that receives the method invocations.
   */
  private Object realObject;

  /**
   * The minimum number of milliseconds to wait before the first retry.
   * The actual wait time will be some random number between this and
   * maxWait.
   */
  private int minWait = 500;

  /**
   * The maximum number of milliseconds to wait before the first retry.
   * The actual wait time will be some random number between minWait and
   * this.
   */
  private int maxWait = 2000;

  /**
   * The backoff multiplier. After the initial wait, if a second retry
   * is required, we wait retryTime * backoffMultiplier milliseconds. If
   * a third retry is required, we multiply the wait time by
   * backoffMultiplier again, etc.
   */
  private float backoffMultiplier = 1.5f;

  /**
   * Total number of retries before we give up completely.
   */
  private int numRetries = 3;

  /**
   * The types of exceptions that should cause a retry. If null or
   * empty, all exceptions cause a retry.
   */
  private Class<? extends Throwable>[] retryExceptionTypes = null;

  /**
   * Create a RetryWrapper wrapping the given real object. Once the
   * wrapper is configured you must call {@link #getProxy} to get a
   * proxy implementing the same interface as the real object.
   */
  public RetryWrapper(Object realObject) {
    this.realObject = realObject;
    this.log = LogFactory.getLog(this.getClass().getName() + "."
            + realObject.getClass().getName());
  }

  public void setMinWait(int minWait) {
    this.minWait = minWait;
  }

  public void setMaxWait(int maxWait) {
    this.maxWait = maxWait;
  }

  public void setBackoffMultiplier(float backoffMultiplier) {
    this.backoffMultiplier = backoffMultiplier;
  }

  public void setNumRetries(int numRetries) {
    this.numRetries = numRetries;
  }

  public void setRetryExceptionTypes(
          Class<? extends Throwable>... retryExceptionTypes) {
    this.retryExceptionTypes = retryExceptionTypes;
  }

  /**
   * Random number generator used to create wait times.
   */
  private Random rng = new Random();

  public Object invoke(Object proxy, Method method, Object[] args)
          throws Throwable {
    // Don't retry methods declared by java.lang.Object, i.e. equals,
    // hashCode or toString
    if(method.getDeclaringClass() == Object.class) {
      try {
        return method.invoke(realObject, args);
      }
      catch(InvocationTargetException ite) {
        throw ite.getCause();
      }
    }

    int remainingRetries = numRetries;
    int waitTime = minWait + rng.nextInt(maxWait - minWait);

    Throwable thrown = null;

    while(remainingRetries >= 0) {
      try {
        return method.invoke(realObject, args);
      }
      catch(InvocationTargetException ite) {
        thrown = ite.getCause();
        if(remainingRetries == 0) {
          log.info("Reached maximum retry count, giving up.");
          break;
        }
        else {
          if(retryExceptionTypes != null && retryExceptionTypes.length > 0) {
            boolean shouldRetry = false;
            for(Class<? extends Throwable> exceptionType : retryExceptionTypes) {
              if(exceptionType.isInstance(thrown)) {
                shouldRetry = true;
                break;
              }
            }

            // the exception is of a type that doesn't trigger a retry
            if(!shouldRetry) {
              log
                      .info("Call threw exception of type "
                              + thrown.getClass().getName()
                              + ", which is not configured to cause a retry - giving up.");
              break;
            }
          }

          log.debug("Exception thrown by method " + method.getName() + " of "
                  + realObject + ". Retrying in " + waitTime + "ms", thrown);
          try {
            Thread.sleep(waitTime);
          }
          catch(InterruptedException ie) {
            log.info("Thread interrupted, giving up retrying.");
            throw thrown;
          }

          // actually do the retry, updating the remaining retries and
          // wait time appropriately
          remainingRetries--;
          waitTime = (int)(waitTime * backoffMultiplier);
        }
      }
    }

    // if we get here we've got a thrown exception and either run out of
    // retries or got an exception of a non-retry type, so throw it.
    throw thrown;
  }

  /**
   * Get a proxy implementing the given interface that uses this
   * RetryWrapper to retry calls to the real object.
   */
  public <T> T getProxy(Class<T> interfaceType) {
    return interfaceType.cast(Proxy.newProxyInstance(interfaceType
            .getClassLoader(), new Class<?>[] {interfaceType}, this));
  }

}
