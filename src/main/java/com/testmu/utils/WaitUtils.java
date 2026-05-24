package com.testmu.utils;

import com.testmu.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

/**
 * Wait Utilities - Provides various wait mechanisms for stable test execution.
 * Includes explicit waits, fluent waits, and custom wait conditions.
 */
public class WaitUtils {
    private static final Logger logger = LogManager.getLogger(WaitUtils.class);
    private final WebDriver driver;
    private final int defaultTimeout;
    
    /**
     * Constructor with WebDriver
     * @param driver WebDriver instance
     */
    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        this.defaultTimeout = ConfigReader.getExplicitWait();
    }
    
    /**
     * Get WebDriverWait with default timeout
     * @return WebDriverWait instance
     */
    private WebDriverWait getWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(defaultTimeout));
    }
    
    /**
     * Get WebDriverWait with custom timeout
     * @param timeoutSeconds Custom timeout in seconds
     * @return WebDriverWait instance
     */
    private WebDriverWait getWait(int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }
    
    // ==================== Visibility Waits ====================
    
    /**
     * Wait for element to be visible
     * @param locator Element locator
     * @return WebElement when visible
     */
    public WebElement waitForVisible(By locator) {
        logger.debug("Waiting for element to be visible: {}", locator);
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    /**
     * Wait for element to be visible with custom timeout
     * @param locator Element locator
     * @param timeoutSeconds Custom timeout
     * @return WebElement when visible
     */
    public WebElement waitForVisible(By locator, int timeoutSeconds) {
        logger.debug("Waiting for element to be visible: {} (timeout: {}s)", locator, timeoutSeconds);
        return getWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    /**
     * Wait for element to be visible
     * @param element WebElement
     * @return WebElement when visible
     */
    public WebElement waitForVisible(WebElement element) {
        logger.debug("Waiting for element to be visible");
        return getWait().until(ExpectedConditions.visibilityOf(element));
    }
    
    /**
     * Wait for all elements to be visible
     * @param locator Element locator
     * @return List of visible WebElements
     */
    public List<WebElement> waitForAllVisible(By locator) {
        logger.debug("Waiting for all elements to be visible: {}", locator);
        return getWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }
    
    // ==================== Clickability Waits ====================
    
    /**
     * Wait for element to be clickable
     * @param locator Element locator
     * @return WebElement when clickable
     */
    public WebElement waitForClickable(By locator) {
        logger.debug("Waiting for element to be clickable: {}", locator);
        return getWait().until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    /**
     * Wait for element to be clickable
     * @param element WebElement
     * @return WebElement when clickable
     */
    public WebElement waitForClickable(WebElement element) {
        logger.debug("Waiting for element to be clickable");
        return getWait().until(ExpectedConditions.elementToBeClickable(element));
    }
    
    /**
     * Wait for element to be clickable with custom timeout
     * @param locator Element locator
     * @param timeoutSeconds Custom timeout
     * @return WebElement when clickable
     */
    public WebElement waitForClickable(By locator, int timeoutSeconds) {
        logger.debug("Waiting for element to be clickable: {} (timeout: {}s)", locator, timeoutSeconds);
        return getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    // ==================== Presence Waits ====================
    
    /**
     * Wait for element to be present in DOM
     * @param locator Element locator
     * @return WebElement when present
     */
    public WebElement waitForPresence(By locator) {
        logger.debug("Waiting for element presence: {}", locator);
        return getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
    }
    
    /**
     * Wait for all elements to be present in DOM
     * @param locator Element locator
     * @return List of WebElements
     */
    public List<WebElement> waitForAllPresent(By locator) {
        logger.debug("Waiting for all elements presence: {}", locator);
        return getWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }
    
    // ==================== Invisibility Waits ====================
    
    /**
     * Wait for element to be invisible/disappear
     * @param locator Element locator
     * @return true when element is invisible
     */
    public boolean waitForInvisible(By locator) {
        logger.debug("Waiting for element to be invisible: {}", locator);
        return getWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
    
    /**
     * Wait for element to be invisible with custom timeout
     * @param locator Element locator
     * @param timeoutSeconds Custom timeout
     * @return true when element is invisible
     */
    public boolean waitForInvisible(By locator, int timeoutSeconds) {
        logger.debug("Waiting for element to be invisible: {} (timeout: {}s)", locator, timeoutSeconds);
        return getWait(timeoutSeconds).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
    
    /**
     * Wait for element staleness (removed from DOM)
     * @param element WebElement
     * @return true when element is stale
     */
    public boolean waitForStaleness(WebElement element) {
        logger.debug("Waiting for element staleness");
        return getWait().until(ExpectedConditions.stalenessOf(element));
    }
    
    // ==================== Text Waits ====================
    
    /**
     * Wait for text to be present in element
     * @param locator Element locator
     * @param text Expected text
     * @return true when text is present
     */
    public boolean waitForTextPresent(By locator, String text) {
        logger.debug("Waiting for text '{}' in element: {}", text, locator);
        return getWait().until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }
    
    /**
     * Wait for text to be present in element value attribute
     * @param locator Element locator
     * @param text Expected text
     * @return true when text is present in value
     */
    public boolean waitForTextInValue(By locator, String text) {
        logger.debug("Waiting for text '{}' in element value: {}", text, locator);
        return getWait().until(ExpectedConditions.textToBePresentInElementValue(locator, text));
    }
    
    // ==================== URL and Title Waits ====================
    
    /**
     * Wait for URL to contain specific text
     * @param urlPart URL substring
     * @return true when URL contains text
     */
    public boolean waitForUrlContains(String urlPart) {
        logger.debug("Waiting for URL to contain: {}", urlPart);
        return getWait().until(ExpectedConditions.urlContains(urlPart));
    }
    
    /**
     * Wait for URL to be exact value
     * @param url Expected URL
     * @return true when URL matches
     */
    public boolean waitForUrlToBe(String url) {
        logger.debug("Waiting for URL to be: {}", url);
        return getWait().until(ExpectedConditions.urlToBe(url));
    }
    
    /**
     * Wait for page title to contain text
     * @param titlePart Title substring
     * @return true when title contains text
     */
    public boolean waitForTitleContains(String titlePart) {
        logger.debug("Waiting for title to contain: {}", titlePart);
        return getWait().until(ExpectedConditions.titleContains(titlePart));
    }
    
    /**
     * Wait for page title to be exact value
     * @param title Expected title
     * @return true when title matches
     */
    public boolean waitForTitleIs(String title) {
        logger.debug("Waiting for title to be: {}", title);
        return getWait().until(ExpectedConditions.titleIs(title));
    }
    
    // ==================== Attribute Waits ====================
    
    /**
     * Wait for element attribute to have specific value
     * @param locator Element locator
     * @param attribute Attribute name
     * @param value Expected value
     * @return true when attribute matches
     */
    public boolean waitForAttributeValue(By locator, String attribute, String value) {
        logger.debug("Waiting for attribute '{}' to be '{}': {}", attribute, value, locator);
        return getWait().until(ExpectedConditions.attributeToBe(locator, attribute, value));
    }
    
    /**
     * Wait for element attribute to contain specific value
     * @param locator Element locator
     * @param attribute Attribute name
     * @param value Expected partial value
     * @return true when attribute contains value
     */
    public boolean waitForAttributeContains(By locator, String attribute, String value) {
        logger.debug("Waiting for attribute '{}' to contain '{}': {}", attribute, value, locator);
        return getWait().until(ExpectedConditions.attributeContains(locator, attribute, value));
    }
    
    // ==================== Frame Waits ====================
    
    /**
     * Wait for frame and switch to it
     * @param locator Frame locator
     * @return WebDriver focused on frame
     */
    public WebDriver waitForFrameAndSwitch(By locator) {
        logger.debug("Waiting for frame and switching: {}", locator);
        return getWait().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    }
    
    /**
     * Wait for frame and switch to it by index
     * @param frameIndex Frame index
     * @return WebDriver focused on frame
     */
    public WebDriver waitForFrameAndSwitch(int frameIndex) {
        logger.debug("Waiting for frame and switching by index: {}", frameIndex);
        return getWait().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameIndex));
    }
    
    // ==================== Alert Waits ====================
    
    /**
     * Wait for alert to be present
     * @return Alert when present
     */
    public Alert waitForAlertPresent() {
        logger.debug("Waiting for alert to be present");
        return getWait().until(ExpectedConditions.alertIsPresent());
    }
    
    // ==================== Count Waits ====================
    
    /**
     * Wait for specific number of elements
     * @param locator Element locator
     * @param expectedCount Expected count
     * @return true when count matches
     */
    public List<WebElement> waitForNumberOfElements(By locator, int expectedCount) {
        logger.debug("Waiting for {} elements: {}", expectedCount, locator);
        return getWait().until(ExpectedConditions.numberOfElementsToBe(locator, expectedCount));
    }
    
    /**
     * Wait for minimum number of elements
     * @param locator Element locator
     * @param minCount Minimum count
     * @return List of WebElements when condition met
     */
    public List<WebElement> waitForMinimumElements(By locator, int minCount) {
        logger.debug("Waiting for minimum {} elements: {}", minCount, locator);
        return getWait().until(ExpectedConditions.numberOfElementsToBeMoreThan(locator, minCount - 1));
    }
    
    // ==================== Page Load Waits ====================
    
    /**
     * Wait for page to fully load (document.readyState = complete)
     */
    public void waitForPageLoad() {
        logger.debug("Waiting for page to fully load");
        getWait().until(driver -> {
            String readyState = ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").toString();
            return "complete".equals(readyState);
        });
    }
    
    /**
     * Wait for AJAX calls to complete (jQuery)
     */
    public void waitForAjax() {
        logger.debug("Waiting for AJAX calls to complete");
        getWait().until(driver -> {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            return (Boolean) js.executeScript(
                "return (typeof jQuery === 'undefined') || (jQuery.active === 0)"
            );
        });
    }
    
    /**
     * Wait for Angular to finish rendering
     */
    public void waitForAngular() {
        logger.debug("Waiting for Angular to finish");
        getWait().until(driver -> {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            return (Boolean) js.executeScript(
                "return (typeof angular === 'undefined') || " +
                "(angular.element(document.body).injector() === undefined) || " +
                "(angular.element(document.body).injector().get('$http').pendingRequests.length === 0)"
            );
        });
    }
    
    // ==================== Fluent Wait ====================
    
    /**
     * Create fluent wait with custom polling
     * @param condition Condition to wait for
     * @param timeoutSeconds Timeout in seconds
     * @param pollingMillis Polling interval in milliseconds
     * @param <T> Return type
     * @return Result of condition
     */
    public <T> T fluentWait(Function<WebDriver, T> condition, int timeoutSeconds, int pollingMillis) {
        logger.debug("Starting fluent wait (timeout: {}s, polling: {}ms)", timeoutSeconds, pollingMillis);
        
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
            .withTimeout(Duration.ofSeconds(timeoutSeconds))
            .pollingEvery(Duration.ofMillis(pollingMillis))
            .ignoring(NoSuchElementException.class)
            .ignoring(StaleElementReferenceException.class)
            .ignoring(ElementNotInteractableException.class);
        
        return fluentWait.until(condition);
    }
    
    /**
     * Wait for custom condition with error message
     * @param condition Custom condition
     * @param errorMessage Error message on timeout
     */
    public void waitForCondition(ExpectedCondition<Boolean> condition, String errorMessage) {
        try {
            getWait().until(condition);
        } catch (TimeoutException e) {
            throw new TimeoutException(errorMessage, e);
        }
    }
    
    // ==================== Utility Methods ====================
    
    /**
     * Sleep for specified milliseconds (use sparingly)
     * @param milliseconds Sleep duration
     */
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted");
        }
    }
}
