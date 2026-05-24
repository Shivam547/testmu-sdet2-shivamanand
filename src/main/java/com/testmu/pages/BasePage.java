package com.testmu.pages;

import com.testmu.config.ConfigReader;
import com.testmu.utils.WaitUtils;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Base Page - Abstract base class for all Page Objects.
 * Provides common functionality, locator strategies, and element interactions.
 * All page classes should extend this class.
 */
public abstract class BasePage {
    protected WebDriver driver;
    protected WaitUtils waitUtils;
    protected Actions actions;
    protected final Logger logger = LogManager.getLogger(this.getClass());
    
    /**
     * Constructor initializes WebDriver, WaitUtils, and PageFactory
     * @param driver WebDriver instance
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
        this.actions = new Actions(driver);
        PageFactory.initElements(driver, this);
    }
    
    // ==================== Locator Strategy Methods ====================
    // Prefer data-testid over other selectors for stability
    
    /**
     * Create locator using data-testid attribute (preferred strategy)
     * @param testId The data-testid value
     * @return By locator
     */
    protected By byTestId(String testId) {
        return By.cssSelector("[data-testid='" + testId + "']");
    }
    
    /**
     * Create locator using aria-label attribute (semantic)
     * @param label The aria-label value
     * @return By locator
     */
    protected By byAriaLabel(String label) {
        return By.cssSelector("[aria-label='" + label + "']");
    }
    
    /**
     * Create locator using role and name (semantic)
     * @param role Element role
     * @param name Element name or text
     * @return By locator
     */
    protected By byRole(String role, String name) {
        return By.xpath(String.format(
            "//*[@role='%s' and (normalize-space(text())='%s' or @aria-label='%s' or @name='%s')]",
            role, name, name, name));
    }
    
    /**
     * Create locator for button by text
     * @param text Button text
     * @return By locator
     */
    protected By byButtonText(String text) {
        return By.xpath(String.format(
            "//button[normalize-space(text())='%s' or normalize-space(.)='%s']", text, text));
    }
    
    /**
     * Create locator for link by text
     * @param text Link text
     * @return By locator
     */
    protected By byLinkText(String text) {
        return By.linkText(text);
    }
    
    /**
     * Create locator for partial link text
     * @param text Partial link text
     * @return By locator
     */
    protected By byPartialLinkText(String text) {
        return By.partialLinkText(text);
    }
    
    // ==================== Element Interaction Methods ====================
    
    /**
     * Click on element with wait
     * @param locator Element locator
     */
    @Step("Click on element: {locator}")
    protected void click(By locator) {
        logger.debug("Clicking on element: {}", locator);
        waitUtils.waitForClickable(locator).click();
    }
    
    /**
     * Click on WebElement with wait
     * @param element WebElement to click
     */
    @Step("Click on element")
    protected void click(WebElement element) {
        logger.debug("Clicking on element");
        waitUtils.waitForClickable(element).click();
    }
    
    /**
     * Type text into element (clears first)
     * @param locator Element locator
     * @param text Text to type
     */
    @Step("Type '{text}' into element: {locator}")
    protected void type(By locator, String text) {
        logger.debug("Typing '{}' into element: {}", text, locator);
        WebElement element = waitUtils.waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }
    
    /**
     * Type text into WebElement (clears first)
     * @param element WebElement
     * @param text Text to type
     */
    @Step("Type '{text}' into element")
    protected void type(WebElement element, String text) {
        logger.debug("Typing '{}' into element", text);
        waitUtils.waitForVisible(element);
        element.clear();
        element.sendKeys(text);
    }
    
    /**
     * Type text without clearing first
     * @param locator Element locator
     * @param text Text to append
     */
    protected void typeWithoutClear(By locator, String text) {
        logger.debug("Typing '{}' into element without clearing: {}", text, locator);
        waitUtils.waitForVisible(locator).sendKeys(text);
    }
    
    /**
     * Clear element text
     * @param locator Element locator
     */
    @Step("Clear element: {locator}")
    protected void clear(By locator) {
        logger.debug("Clearing element: {}", locator);
        waitUtils.waitForVisible(locator).clear();
    }
    
    /**
     * Get element text
     * @param locator Element locator
     * @return Element text
     */
    protected String getText(By locator) {
        return waitUtils.waitForVisible(locator).getText().trim();
    }
    
    /**
     * Get WebElement text
     * @param element WebElement
     * @return Element text
     */
    protected String getText(WebElement element) {
        return waitUtils.waitForVisible(element).getText().trim();
    }
    
    /**
     * Get element attribute value
     * @param locator Element locator
     * @param attribute Attribute name
     * @return Attribute value
     */
    protected String getAttribute(By locator, String attribute) {
        return waitUtils.waitForPresence(locator).getAttribute(attribute);
    }
    
    /**
     * Get element attribute value
     * @param element WebElement
     * @param attribute Attribute name
     * @return Attribute value
     */
    protected String getAttribute(WebElement element, String attribute) {
        return element.getAttribute(attribute);
    }
    
    /**
     * Check if element is displayed
     * @param locator Element locator
     * @return true if displayed
     */
    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }
    
    /**
     * Check if WebElement is displayed
     * @param element WebElement
     * @return true if displayed
     */
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }
    
    /**
     * Check if element is enabled
     * @param locator Element locator
     * @return true if enabled
     */
    protected boolean isEnabled(By locator) {
        try {
            return driver.findElement(locator).isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    /**
     * Check if element is selected
     * @param locator Element locator
     * @return true if selected
     */
    protected boolean isSelected(By locator) {
        try {
            return driver.findElement(locator).isSelected();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    // ==================== Dropdown Handling ====================
    
    /**
     * Select dropdown option by visible text
     * @param locator Dropdown locator
     * @param text Visible text to select
     */
    @Step("Select '{text}' from dropdown: {locator}")
    protected void selectByVisibleText(By locator, String text) {
        logger.debug("Selecting '{}' from dropdown: {}", text, locator);
        Select select = new Select(waitUtils.waitForVisible(locator));
        select.selectByVisibleText(text);
    }
    
    /**
     * Select dropdown option by value
     * @param locator Dropdown locator
     * @param value Value to select
     */
    @Step("Select by value '{value}' from dropdown: {locator}")
    protected void selectByValue(By locator, String value) {
        logger.debug("Selecting by value '{}' from dropdown: {}", value, locator);
        Select select = new Select(waitUtils.waitForVisible(locator));
        select.selectByValue(value);
    }
    
    /**
     * Select dropdown option by index
     * @param locator Dropdown locator
     * @param index Index to select (0-based)
     */
    @Step("Select by index {index} from dropdown: {locator}")
    protected void selectByIndex(By locator, int index) {
        logger.debug("Selecting by index {} from dropdown: {}", index, locator);
        Select select = new Select(waitUtils.waitForVisible(locator));
        select.selectByIndex(index);
    }
    
    /**
     * Get selected option text from dropdown
     * @param locator Dropdown locator
     * @return Selected option text
     */
    protected String getSelectedText(By locator) {
        Select select = new Select(waitUtils.waitForVisible(locator));
        return select.getFirstSelectedOption().getText();
    }
    
    // ==================== JavaScript Interactions ====================
    
    /**
     * Click using JavaScript (for stubborn elements)
     * @param element WebElement to click
     */
    @Step("JavaScript click on element")
    protected void jsClick(WebElement element) {
        logger.debug("JavaScript click on element");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
    
    /**
     * Click using JavaScript by locator
     * @param locator Element locator
     */
    protected void jsClick(By locator) {
        WebElement element = waitUtils.waitForPresence(locator);
        jsClick(element);
    }
    
    /**
     * Scroll element into view
     * @param element WebElement to scroll to
     */
    @Step("Scroll element into view")
    protected void scrollIntoView(WebElement element) {
        logger.debug("Scrolling element into view");
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }
    
    /**
     * Scroll element into view by locator
     * @param locator Element locator
     */
    protected void scrollIntoView(By locator) {
        WebElement element = waitUtils.waitForPresence(locator);
        scrollIntoView(element);
    }
    
    /**
     * Scroll page to top
     */
    protected void scrollToTop() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
    }
    
    /**
     * Scroll page to bottom
     */
    protected void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript(
            "window.scrollTo(0, document.body.scrollHeight);");
    }
    
    /**
     * Set element value using JavaScript
     * @param element WebElement
     * @param value Value to set
     */
    protected void jsSetValue(WebElement element, String value) {
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = arguments[1];", element, value);
    }
    
    /**
     * Highlight element (useful for debugging)
     * @param element WebElement to highlight
     */
    protected void highlightElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].style.border='3px solid red'", element);
    }
    
    // ==================== Actions Interactions ====================
    
    /**
     * Hover over element
     * @param element WebElement to hover
     */
    @Step("Hover over element")
    protected void hover(WebElement element) {
        logger.debug("Hovering over element");
        actions.moveToElement(element).perform();
    }
    
    /**
     * Hover over element by locator
     * @param locator Element locator
     */
    protected void hover(By locator) {
        WebElement element = waitUtils.waitForVisible(locator);
        hover(element);
    }
    
    /**
     * Double-click element
     * @param element WebElement to double-click
     */
    @Step("Double-click element")
    protected void doubleClick(WebElement element) {
        logger.debug("Double-clicking element");
        actions.doubleClick(element).perform();
    }
    
    /**
     * Right-click element (context menu)
     * @param element WebElement to right-click
     */
    @Step("Right-click element")
    protected void rightClick(WebElement element) {
        logger.debug("Right-clicking element");
        actions.contextClick(element).perform();
    }
    
    /**
     * Drag and drop
     * @param source Source element
     * @param target Target element
     */
    @Step("Drag and drop element")
    protected void dragAndDrop(WebElement source, WebElement target) {
        logger.debug("Drag and drop");
        actions.dragAndDrop(source, target).perform();
    }
    
    // ==================== Alert Handling ====================
    
    /**
     * Accept alert (click OK)
     */
    @Step("Accept alert")
    protected void acceptAlert() {
        logger.debug("Accepting alert");
        waitUtils.waitForAlertPresent().accept();
    }
    
    /**
     * Dismiss alert (click Cancel)
     */
    @Step("Dismiss alert")
    protected void dismissAlert() {
        logger.debug("Dismissing alert");
        waitUtils.waitForAlertPresent().dismiss();
    }
    
    /**
     * Get alert text
     * @return Alert text
     */
    protected String getAlertText() {
        return waitUtils.waitForAlertPresent().getText();
    }
    
    /**
     * Type into alert prompt
     * @param text Text to type
     */
    protected void typeInAlert(String text) {
        Alert alert = waitUtils.waitForAlertPresent();
        alert.sendKeys(text);
    }
    
    // ==================== Frame Handling ====================
    
    /**
     * Switch to frame by locator
     * @param locator Frame locator
     */
    @Step("Switch to frame: {locator}")
    protected void switchToFrame(By locator) {
        logger.debug("Switching to frame: {}", locator);
        waitUtils.waitForFrameAndSwitch(locator);
    }
    
    /**
     * Switch to frame by index
     * @param index Frame index
     */
    protected void switchToFrame(int index) {
        logger.debug("Switching to frame by index: {}", index);
        waitUtils.waitForFrameAndSwitch(index);
    }
    
    /**
     * Switch back to default content
     */
    @Step("Switch to default content")
    protected void switchToDefaultContent() {
        logger.debug("Switching to default content");
        driver.switchTo().defaultContent();
    }
    
    /**
     * Switch to parent frame
     */
    protected void switchToParentFrame() {
        driver.switchTo().parentFrame();
    }
    
    // ==================== Window/Tab Handling ====================
    
    /**
     * Switch to new window/tab
     * @param currentHandle Current window handle
     */
    protected void switchToNewWindow(String currentHandle) {
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(currentHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }
    }
    
    /**
     * Close current window and switch to original
     * @param originalHandle Original window handle
     */
    protected void closeAndSwitchToOriginal(String originalHandle) {
        driver.close();
        driver.switchTo().window(originalHandle);
    }
    
    /**
     * Get current window handle
     * @return Window handle
     */
    protected String getWindowHandle() {
        return driver.getWindowHandle();
    }
    
    // ==================== Element List Operations ====================
    
    /**
     * Find all elements matching locator
     * @param locator Element locator
     * @return List of WebElements
     */
    protected List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }
    
    /**
     * Get count of elements matching locator
     * @param locator Element locator
     * @return Element count
     */
    protected int getElementCount(By locator) {
        return driver.findElements(locator).size();
    }
    
    // ==================== Navigation ====================
    
    /**
     * Navigate to URL
     * @param url URL to navigate to
     */
    @Step("Navigate to: {url}")
    protected void navigateTo(String url) {
        logger.info("Navigating to: {}", url);
        driver.get(url);
        waitUtils.waitForPageLoad();
    }
    
    /**
     * Navigate to base URL
     */
    @Step("Navigate to base URL")
    protected void navigateToBaseUrl() {
        navigateTo(ConfigReader.getBaseUrl());
    }
    
    /**
     * Get current URL
     * @return Current URL
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
    
    /**
     * Get page title
     * @return Page title
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }
    
    /**
     * Refresh page
     */
    @Step("Refresh page")
    protected void refreshPage() {
        logger.debug("Refreshing page");
        driver.navigate().refresh();
        waitUtils.waitForPageLoad();
    }
    
    /**
     * Navigate back
     */
    @Step("Navigate back")
    protected void navigateBack() {
        logger.debug("Navigating back");
        driver.navigate().back();
    }
    
    /**
     * Navigate forward
     */
    protected void navigateForward() {
        driver.navigate().forward();
    }
    
    // ==================== Abstract Methods ====================
    
    /**
     * Verify page is loaded correctly
     * Each page must implement this to validate its state
     * @return true if page is loaded correctly
     */
    public abstract boolean isLoaded();
}
