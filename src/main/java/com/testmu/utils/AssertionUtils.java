package com.testmu.utils;

import io.qameta.allure.Step;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Custom Assertion Utilities - Provides fluent assertions with Allure integration.
 * Built on AssertJ for readable, chainable assertions.
 */
public class AssertionUtils {
    
    // ==================== Element Assertions ====================
    
    /**
     * Assert element is displayed
     * @param element WebElement to check
     * @param elementName Name for reporting
     */
    @Step("Assert element '{elementName}' is displayed")
    public static void assertElementDisplayed(WebElement element, String elementName) {
        Assertions.assertThat(element.isDisplayed())
            .as("Element '%s' should be displayed", elementName)
            .isTrue();
    }
    
    /**
     * Assert element is not displayed or not present
     * @param element WebElement to check
     * @param elementName Name for reporting
     */
    @Step("Assert element '{elementName}' is not displayed")
    public static void assertElementNotDisplayed(WebElement element, String elementName) {
        try {
            Assertions.assertThat(element.isDisplayed())
                .as("Element '%s' should not be displayed", elementName)
                .isFalse();
        } catch (Exception e) {
            // Element not found is also acceptable
        }
    }
    
    /**
     * Assert element is enabled
     * @param element WebElement to check
     * @param elementName Name for reporting
     */
    @Step("Assert element '{elementName}' is enabled")
    public static void assertElementEnabled(WebElement element, String elementName) {
        Assertions.assertThat(element.isEnabled())
            .as("Element '%s' should be enabled", elementName)
            .isTrue();
    }
    
    /**
     * Assert element is disabled
     * @param element WebElement to check
     * @param elementName Name for reporting
     */
    @Step("Assert element '{elementName}' is disabled")
    public static void assertElementDisabled(WebElement element, String elementName) {
        Assertions.assertThat(element.isEnabled())
            .as("Element '%s' should be disabled", elementName)
            .isFalse();
    }
    
    /**
     * Assert element is selected (checkbox, radio)
     * @param element WebElement to check
     * @param elementName Name for reporting
     */
    @Step("Assert element '{elementName}' is selected")
    public static void assertElementSelected(WebElement element, String elementName) {
        Assertions.assertThat(element.isSelected())
            .as("Element '%s' should be selected", elementName)
            .isTrue();
    }
    
    // ==================== Text Assertions ====================
    
    /**
     * Assert element text equals expected
     * @param element WebElement to check
     * @param expectedText Expected text
     */
    @Step("Assert element text equals '{expectedText}'")
    public static void assertElementText(WebElement element, String expectedText) {
        Assertions.assertThat(element.getText().trim())
            .as("Element text should equal '%s'", expectedText)
            .isEqualTo(expectedText);
    }
    
    /**
     * Assert element text contains expected substring
     * @param element WebElement to check
     * @param expectedText Expected substring
     */
    @Step("Assert element text contains '{expectedText}'")
    public static void assertElementTextContains(WebElement element, String expectedText) {
        Assertions.assertThat(element.getText())
            .as("Element text should contain '%s'", expectedText)
            .contains(expectedText);
    }
    
    /**
     * Assert element text matches regex pattern
     * @param element WebElement to check
     * @param pattern Regex pattern
     */
    @Step("Assert element text matches pattern '{pattern}'")
    public static void assertElementTextMatches(WebElement element, String pattern) {
        Assertions.assertThat(element.getText())
            .as("Element text should match pattern '%s'", pattern)
            .matches(pattern);
    }
    
    /**
     * Assert element text is not empty
     * @param element WebElement to check
     * @param elementName Name for reporting
     */
    @Step("Assert element '{elementName}' text is not empty")
    public static void assertElementTextNotEmpty(WebElement element, String elementName) {
        Assertions.assertThat(element.getText().trim())
            .as("Element '%s' text should not be empty", elementName)
            .isNotEmpty();
    }
    
    // ==================== Attribute Assertions ====================
    
    /**
     * Assert element attribute value equals expected
     * @param element WebElement to check
     * @param attribute Attribute name
     * @param expectedValue Expected value
     */
    @Step("Assert element attribute '{attribute}' equals '{expectedValue}'")
    public static void assertElementAttribute(WebElement element, String attribute, String expectedValue) {
        Assertions.assertThat(element.getAttribute(attribute))
            .as("Element attribute '%s' should equal '%s'", attribute, expectedValue)
            .isEqualTo(expectedValue);
    }
    
    /**
     * Assert element attribute contains expected value
     * @param element WebElement to check
     * @param attribute Attribute name
     * @param expectedValue Expected partial value
     */
    @Step("Assert element attribute '{attribute}' contains '{expectedValue}'")
    public static void assertElementAttributeContains(WebElement element, String attribute, String expectedValue) {
        Assertions.assertThat(element.getAttribute(attribute))
            .as("Element attribute '%s' should contain '%s'", attribute, expectedValue)
            .contains(expectedValue);
    }
    
    /**
     * Assert element has specific CSS class
     * @param element WebElement to check
     * @param className CSS class name
     */
    @Step("Assert element has CSS class '{className}'")
    public static void assertElementHasClass(WebElement element, String className) {
        Assertions.assertThat(element.getAttribute("class"))
            .as("Element should have CSS class '%s'", className)
            .contains(className);
    }
    
    // ==================== URL and Title Assertions ====================
    
    /**
     * Assert current URL equals expected
     * @param driver WebDriver instance
     * @param expectedUrl Expected URL
     */
    @Step("Assert URL equals '{expectedUrl}'")
    public static void assertUrl(WebDriver driver, String expectedUrl) {
        Assertions.assertThat(driver.getCurrentUrl())
            .as("URL should equal '%s'", expectedUrl)
            .isEqualTo(expectedUrl);
    }
    
    /**
     * Assert current URL contains expected substring
     * @param driver WebDriver instance
     * @param urlPart Expected URL part
     */
    @Step("Assert URL contains '{urlPart}'")
    public static void assertUrlContains(WebDriver driver, String urlPart) {
        Assertions.assertThat(driver.getCurrentUrl())
            .as("URL should contain '%s'", urlPart)
            .contains(urlPart);
    }
    
    /**
     * Assert page title equals expected
     * @param driver WebDriver instance
     * @param expectedTitle Expected title
     */
    @Step("Assert page title equals '{expectedTitle}'")
    public static void assertPageTitle(WebDriver driver, String expectedTitle) {
        Assertions.assertThat(driver.getTitle())
            .as("Page title should equal '%s'", expectedTitle)
            .isEqualTo(expectedTitle);
    }
    
    /**
     * Assert page title contains expected substring
     * @param driver WebDriver instance
     * @param titlePart Expected title part
     */
    @Step("Assert page title contains '{titlePart}'")
    public static void assertPageTitleContains(WebDriver driver, String titlePart) {
        Assertions.assertThat(driver.getTitle())
            .as("Page title should contain '%s'", titlePart)
            .contains(titlePart);
    }
    
    // ==================== Collection Assertions ====================
    
    /**
     * Assert list size equals expected
     * @param list List to check
     * @param expectedSize Expected size
     */
    @Step("Assert list size equals {expectedSize}")
    public static void assertListSize(List<?> list, int expectedSize) {
        Assertions.assertThat(list)
            .as("List size should equal %d", expectedSize)
            .hasSize(expectedSize);
    }
    
    /**
     * Assert list is not empty
     * @param list List to check
     */
    @Step("Assert list is not empty")
    public static void assertListNotEmpty(List<?> list) {
        Assertions.assertThat(list)
            .as("List should not be empty")
            .isNotEmpty();
    }
    
    /**
     * Assert list is empty
     * @param list List to check
     */
    @Step("Assert list is empty")
    public static void assertListEmpty(List<?> list) {
        Assertions.assertThat(list)
            .as("List should be empty")
            .isEmpty();
    }
    
    /**
     * Assert list contains item
     * @param list List to check
     * @param item Item to find
     * @param <T> Type parameter
     */
    @Step("Assert list contains item")
    public static <T> void assertListContains(List<T> list, T item) {
        Assertions.assertThat(list)
            .as("List should contain item")
            .contains(item);
    }
    
    // ==================== Numeric Assertions ====================
    
    /**
     * Assert value is greater than expected
     * @param actual Actual value
     * @param expected Expected minimum value
     */
    @Step("Assert {actual} is greater than {expected}")
    public static void assertGreaterThan(long actual, long expected) {
        Assertions.assertThat(actual)
            .as("Value should be greater than %d", expected)
            .isGreaterThan(expected);
    }
    
    /**
     * Assert value is less than expected
     * @param actual Actual value
     * @param expected Expected maximum value
     */
    @Step("Assert {actual} is less than {expected}")
    public static void assertLessThan(long actual, long expected) {
        Assertions.assertThat(actual)
            .as("Value should be less than %d", expected)
            .isLessThan(expected);
    }
    
    /**
     * Assert response time is within limit
     * @param actualTimeMs Actual time in milliseconds
     * @param maxTimeMs Maximum allowed time in milliseconds
     */
    @Step("Assert response time {actualTimeMs}ms is less than {maxTimeMs}ms")
    public static void assertResponseTime(long actualTimeMs, long maxTimeMs) {
        Assertions.assertThat(actualTimeMs)
            .as("Response time should be less than %d ms", maxTimeMs)
            .isLessThan(maxTimeMs);
    }
    
    // ==================== Boolean Assertions ====================
    
    /**
     * Assert condition is true
     * @param condition Condition to check
     * @param message Description of what is being checked
     */
    @Step("Assert: {message}")
    public static void assertTrue(boolean condition, String message) {
        Assertions.assertThat(condition)
            .as(message)
            .isTrue();
    }
    
    /**
     * Assert condition is false
     * @param condition Condition to check
     * @param message Description of what is being checked
     */
    @Step("Assert NOT: {message}")
    public static void assertFalse(boolean condition, String message) {
        Assertions.assertThat(condition)
            .as(message)
            .isFalse();
    }
    
    // ==================== Null Assertions ====================
    
    /**
     * Assert object is not null
     * @param object Object to check
     * @param objectName Name for reporting
     */
    @Step("Assert '{objectName}' is not null")
    public static void assertNotNull(Object object, String objectName) {
        Assertions.assertThat(object)
            .as("'%s' should not be null", objectName)
            .isNotNull();
    }
    
    /**
     * Assert object is null
     * @param object Object to check
     * @param objectName Name for reporting
     */
    @Step("Assert '{objectName}' is null")
    public static void assertNull(Object object, String objectName) {
        Assertions.assertThat(object)
            .as("'%s' should be null", objectName)
            .isNull();
    }
    
    // ==================== String Assertions ====================
    
    /**
     * Assert strings are equal (ignoring case)
     * @param actual Actual string
     * @param expected Expected string
     */
    @Step("Assert strings equal (ignoring case)")
    public static void assertEqualsIgnoreCase(String actual, String expected) {
        Assertions.assertThat(actual)
            .as("Strings should be equal ignoring case")
            .isEqualToIgnoringCase(expected);
    }
    
    /**
     * Assert string starts with expected prefix
     * @param actual Actual string
     * @param prefix Expected prefix
     */
    @Step("Assert string starts with '{prefix}'")
    public static void assertStartsWith(String actual, String prefix) {
        Assertions.assertThat(actual)
            .as("String should start with '%s'", prefix)
            .startsWith(prefix);
    }
    
    /**
     * Assert string ends with expected suffix
     * @param actual Actual string
     * @param suffix Expected suffix
     */
    @Step("Assert string ends with '{suffix}'")
    public static void assertEndsWith(String actual, String suffix) {
        Assertions.assertThat(actual)
            .as("String should end with '%s'", suffix)
            .endsWith(suffix);
    }
}
