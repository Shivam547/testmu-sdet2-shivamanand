package com.testmu.listeners;

import com.testmu.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retry Analyzer - TestNG retry mechanism for flaky tests.
 * Automatically retries failed tests up to configured max attempts.
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);
    
    private int retryCount = 0;
    private final int maxRetryCount;
    
    /**
     * Constructor - loads max retry count from configuration
     */
    public RetryAnalyzer() {
        this.maxRetryCount = ConfigReader.getRetryCount();
    }
    
    /**
     * Determine if the test should be retried
     * @param result Test result
     * @return true if should retry, false otherwise
     */
    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            
            logger.warn("┌──────────────────────────────────────────────────────────────┐");
            logger.warn("│ RETRYING FAILED TEST                                         │");
            logger.warn("│ Test: {}                                                     ", 
                result.getMethod().getMethodName());
            logger.warn("│ Attempt: {} of {}                                            ", 
                retryCount, maxRetryCount);
            logger.warn("│ Reason: {}                                                   ", 
                result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown");
            logger.warn("└──────────────────────────────────────────────────────────────┘");
            
            return true;
        }
        
        logger.error("Test exhausted all {} retry attempts: {}", 
            maxRetryCount, result.getMethod().getMethodName());
        return false;
    }
    
    /**
     * Get current retry count
     * @return Current retry count
     */
    public int getRetryCount() {
        return retryCount;
    }
    
    /**
     * Get max retry count
     * @return Max retry count
     */
    public int getMaxRetryCount() {
        return maxRetryCount;
    }
}
