package com.testmu.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Retry Utilities - Provides retry mechanisms for flaky operations.
 * Helps handle transient failures in tests.
 */
public class RetryUtils {
    private static final Logger logger = LogManager.getLogger(RetryUtils.class);
    private static final int DEFAULT_RETRY_COUNT = 3;
    private static final long DEFAULT_DELAY_MS = 1000;
    
    /**
     * Retry an action that returns a value
     * @param action Action to retry
     * @param maxAttempts Maximum number of attempts
     * @param delayMs Delay between attempts in milliseconds
     * @param <T> Return type
     * @return Result of action
     * @throws RuntimeException if all attempts fail
     */
    public static <T> T retry(Supplier<T> action, int maxAttempts, long delayMs) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                logger.debug("Attempt {} of {}", attempt, maxAttempts);
                return action.get();
            } catch (Exception e) {
                lastException = e;
                logger.warn("Attempt {} failed: {}", attempt, e.getMessage());
                
                if (attempt < maxAttempts) {
                    logger.debug("Retrying in {} ms...", delayMs);
                    sleep(delayMs);
                }
            }
        }
        
        logger.error("All {} attempts failed", maxAttempts);
        throw new RuntimeException("Action failed after " + maxAttempts + " attempts", lastException);
    }
    
    /**
     * Retry an action with default settings
     * @param action Action to retry
     * @param <T> Return type
     * @return Result of action
     */
    public static <T> T retry(Supplier<T> action) {
        return retry(action, DEFAULT_RETRY_COUNT, DEFAULT_DELAY_MS);
    }
    
    /**
     * Retry a void action (no return value)
     * @param action Action to retry
     * @param maxAttempts Maximum number of attempts
     * @param delayMs Delay between attempts in milliseconds
     * @throws RuntimeException if all attempts fail
     */
    public static void retryAction(Runnable action, int maxAttempts, long delayMs) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                logger.debug("Attempt {} of {}", attempt, maxAttempts);
                action.run();
                return; // Success
            } catch (Exception e) {
                lastException = e;
                logger.warn("Attempt {} failed: {}", attempt, e.getMessage());
                
                if (attempt < maxAttempts) {
                    logger.debug("Retrying in {} ms...", delayMs);
                    sleep(delayMs);
                }
            }
        }
        
        logger.error("All {} attempts failed", maxAttempts);
        throw new RuntimeException("Action failed after " + maxAttempts + " attempts", lastException);
    }
    
    /**
     * Retry a void action with default settings
     * @param action Action to retry
     */
    public static void retryAction(Runnable action) {
        retryAction(action, DEFAULT_RETRY_COUNT, DEFAULT_DELAY_MS);
    }
    
    /**
     * Retry until a condition is true
     * @param condition Condition to check
     * @param maxAttempts Maximum number of attempts
     * @param delayMs Delay between attempts in milliseconds
     * @return true if condition was met, false otherwise
     */
    public static boolean retryUntil(BooleanSupplier condition, int maxAttempts, long delayMs) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                logger.debug("Checking condition, attempt {} of {}", attempt, maxAttempts);
                if (condition.getAsBoolean()) {
                    logger.debug("Condition met on attempt {}", attempt);
                    return true;
                }
            } catch (Exception e) {
                logger.warn("Condition check failed on attempt {}: {}", attempt, e.getMessage());
            }
            
            if (attempt < maxAttempts) {
                sleep(delayMs);
            }
        }
        
        logger.warn("Condition not met after {} attempts", maxAttempts);
        return false;
    }
    
    /**
     * Retry until condition is true with default settings
     * @param condition Condition to check
     * @return true if condition was met
     */
    public static boolean retryUntil(BooleanSupplier condition) {
        return retryUntil(condition, DEFAULT_RETRY_COUNT, DEFAULT_DELAY_MS);
    }
    
    /**
     * Retry with exponential backoff
     * @param action Action to retry
     * @param maxAttempts Maximum number of attempts
     * @param initialDelayMs Initial delay in milliseconds
     * @param maxDelayMs Maximum delay in milliseconds
     * @param <T> Return type
     * @return Result of action
     */
    public static <T> T retryWithBackoff(Supplier<T> action, int maxAttempts, 
                                          long initialDelayMs, long maxDelayMs) {
        Exception lastException = null;
        long currentDelay = initialDelayMs;
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                logger.debug("Attempt {} of {} (delay: {}ms)", attempt, maxAttempts, currentDelay);
                return action.get();
            } catch (Exception e) {
                lastException = e;
                logger.warn("Attempt {} failed: {}", attempt, e.getMessage());
                
                if (attempt < maxAttempts) {
                    sleep(currentDelay);
                    // Exponential backoff with cap
                    currentDelay = Math.min(currentDelay * 2, maxDelayMs);
                }
            }
        }
        
        throw new RuntimeException("Action failed after " + maxAttempts + " attempts with backoff", 
            lastException);
    }
    
    /**
     * Sleep for specified duration
     * @param milliseconds Duration in milliseconds
     */
    private static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted");
        }
    }
}
