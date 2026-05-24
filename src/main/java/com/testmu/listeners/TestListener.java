package com.testmu.listeners;

import com.testmu.utils.BrowserFactory;
import com.testmu.utils.ScreenshotUtils;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Test Listener - TestNG listener for test lifecycle events.
 * Handles logging, screenshot capture on failure, and Allure reporting.
 */
public class TestListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(TestListener.class);
    
    /**
     * Called when test suite starts
     */
    @Override
    public void onStart(ITestContext context) {
        logger.info("╔══════════════════════════════════════════════════════════════╗");
        logger.info("║           TEST SUITE STARTED: {}              ", context.getName());
        logger.info("╚══════════════════════════════════════════════════════════════╝");
        
        // Add suite info to Allure
        Allure.suite(context.getName());
    }
    
    /**
     * Called when test suite finishes
     */
    @Override
    public void onFinish(ITestContext context) {
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        int total = passed + failed + skipped;
        
        logger.info("╔══════════════════════════════════════════════════════════════╗");
        logger.info("║           TEST SUITE FINISHED: {}             ", context.getName());
        logger.info("╠══════════════════════════════════════════════════════════════╣");
        logger.info("║  Total Tests: {}                                             ", total);
        logger.info("║  Passed: {}                                                  ", passed);
        logger.info("║  Failed: {}                                                  ", failed);
        logger.info("║  Skipped: {}                                                 ", skipped);
        logger.info("║  Pass Rate: {}%                                              ", 
            total > 0 ? String.format("%.1f", (passed * 100.0 / total)) : "N/A");
        logger.info("╚══════════════════════════════════════════════════════════════╝");
    }
    
    /**
     * Called when a test method starts
     */
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        
        logger.info("┌──────────────────────────────────────────────────────────────┐");
        logger.info("│ STARTING TEST: {}                                            ", testName);
        logger.info("│ Class: {}                                                    ", className);
        logger.info("└──────────────────────────────────────────────────────────────┘");
        
        // Add test name to Allure
        Allure.getLifecycle().updateTestCase(testResult -> 
            testResult.setName(testName));
    }
    
    /**
     * Called when a test method succeeds
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logger.info("┌──────────────────────────────────────────────────────────────┐");
        logger.info("│ ✓ TEST PASSED: {}                                            ", testName);
        logger.info("│ Duration: {} ms                                              ", duration);
        logger.info("└──────────────────────────────────────────────────────────────┘");
    }
    
    /**
     * Called when a test method fails
     */
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logger.error("┌──────────────────────────────────────────────────────────────┐");
        logger.error("│ ✗ TEST FAILED: {}                                            ", testName);
        logger.error("│ Duration: {} ms                                              ", duration);
        logger.error("│ Error: {}                                                    ", 
            throwable != null ? throwable.getMessage() : "Unknown error");
        logger.error("└──────────────────────────────────────────────────────────────┘");
        
        // Capture screenshot on failure
        captureScreenshotOnFailure(result);
        
        // Attach exception to Allure
        if (throwable != null) {
            Allure.addAttachment("Exception", "text/plain", 
                getStackTraceAsString(throwable));
        }
    }
    
    /**
     * Called when a test method is skipped
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();
        
        logger.warn("┌──────────────────────────────────────────────────────────────┐");
        logger.warn("│ ⊘ TEST SKIPPED: {}                                           ", testName);
        if (throwable != null) {
            logger.warn("│ Reason: {}                                                   ", 
                throwable.getMessage());
        }
        logger.warn("└──────────────────────────────────────────────────────────────┘");
    }
    
    /**
     * Called when a test method fails but is within success percentage
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.warn("Test failed but within success percentage: {}", testName);
    }
    
    /**
     * Capture screenshot when test fails
     */
    private void captureScreenshotOnFailure(ITestResult result) {
        try {
            WebDriver driver = BrowserFactory.getDriver();
            if (driver != null) {
                String testName = result.getMethod().getMethodName();
                String screenshotPath = ScreenshotUtils.captureScreenshot(driver, testName + "_FAILED");
                
                if (screenshotPath != null) {
                    logger.info("Screenshot captured: {}", screenshotPath);
                }
            } else {
                logger.warn("Cannot capture screenshot - WebDriver is null");
            }
        } catch (Exception e) {
            logger.error("Failed to capture screenshot: {}", e.getMessage());
        }
    }
    
    /**
     * Convert stack trace to string
     */
    private String getStackTraceAsString(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");
        
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        
        if (throwable.getCause() != null) {
            sb.append("Caused by: ").append(getStackTraceAsString(throwable.getCause()));
        }
        
        return sb.toString();
    }
}
