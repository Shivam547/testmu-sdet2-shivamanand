package com.testmu.pages;

import com.testmu.config.ConfigReader;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Map;

/**
 * Form Page - Page Object for form validation testing.
 * Uses The Internet Herokuapp's various form pages for demonstration.
 */
public class FormPage extends BasePage {
    
    // Page paths
    private static final String INPUTS_PATH = "/inputs";
    private static final String DROPDOWN_PATH = "/dropdown";
    private static final String CHECKBOXES_PATH = "/checkboxes";
    
    // ==================== Locators for Inputs Page ====================
    
    @FindBy(css = "input[type='number']")
    private WebElement numberInput;
    
    // ==================== Locators for Dropdown Page ====================
    
    @FindBy(id = "dropdown")
    private WebElement dropdown;
    
    // ==================== Locators for Checkboxes Page ====================
    
    @FindBy(css = "#checkboxes input[type='checkbox']")
    private List<WebElement> checkboxes;
    
    // ==================== Generic Form Locators ====================
    
    @FindBy(css = "h3")
    private WebElement pageTitle;
    
    private final By inputLocator = By.cssSelector("input[type='number']");
    private final By dropdownLocator = By.id("dropdown");
    private final By checkboxLocator = By.cssSelector("#checkboxes input[type='checkbox']");
    
    /**
     * Constructor
     * @param driver WebDriver instance
     */
    public FormPage(WebDriver driver) {
        super(driver);
        logger.info("Form Page initialized");
    }
    
    // ==================== Navigation ====================
    
    /**
     * Open the inputs page
     * @return FormPage for chaining
     */
    @Step("Open inputs page")
    public FormPage openInputsPage() {
        navigateTo(ConfigReader.getBaseUrl() + INPUTS_PATH);
        return this;
    }
    
    /**
     * Open the dropdown page
     * @return FormPage for chaining
     */
    @Step("Open dropdown page")
    public FormPage openDropdownPage() {
        navigateTo(ConfigReader.getBaseUrl() + DROPDOWN_PATH);
        return this;
    }
    
    /**
     * Open the checkboxes page
     * @return FormPage for chaining
     */
    @Step("Open checkboxes page")
    public FormPage openCheckboxesPage() {
        navigateTo(ConfigReader.getBaseUrl() + CHECKBOXES_PATH);
        return this;
    }
    
    // ==================== Input Actions ====================
    
    /**
     * Enter number in input field
     * @param number Number to enter
     * @return FormPage for chaining
     */
    @Step("Enter number: {number}")
    public FormPage enterNumber(String number) {
        type(numberInput, number);
        return this;
    }
    
    /**
     * Get current number value
     * @return Number value
     */
    @Step("Get number value")
    public String getNumberValue() {
        return getAttribute(numberInput, "value");
    }
    
    /**
     * Clear number input
     * @return FormPage for chaining
     */
    @Step("Clear number input")
    public FormPage clearNumberInput() {
        numberInput.clear();
        return this;
    }
    
    /**
     * Increment number using arrow keys
     * @param times Number of times to increment
     * @return FormPage for chaining
     */
    @Step("Increment number {times} times")
    public FormPage incrementNumber(int times) {
        for (int i = 0; i < times; i++) {
            numberInput.sendKeys(org.openqa.selenium.Keys.ARROW_UP);
        }
        return this;
    }
    
    /**
     * Decrement number using arrow keys
     * @param times Number of times to decrement
     * @return FormPage for chaining
     */
    @Step("Decrement number {times} times")
    public FormPage decrementNumber(int times) {
        for (int i = 0; i < times; i++) {
            numberInput.sendKeys(org.openqa.selenium.Keys.ARROW_DOWN);
        }
        return this;
    }
    
    // ==================== Dropdown Actions ====================
    
    /**
     * Select dropdown option by visible text
     * @param option Option text to select
     * @return FormPage for chaining
     */
    @Step("Select dropdown option: {option}")
    public FormPage selectOption(String option) {
        selectByVisibleText(dropdownLocator, option);
        return this;
    }
    
    /**
     * Select dropdown option by index
     * @param index Option index (1-based for this dropdown)
     * @return FormPage for chaining
     */
    @Step("Select dropdown option by index: {index}")
    public FormPage selectOptionByIndex(int index) {
        selectByIndex(dropdownLocator, index);
        return this;
    }
    
    /**
     * Get selected dropdown option
     * @return Selected option text
     */
    @Step("Get selected dropdown option")
    public String getSelectedOption() {
        return getSelectedText(dropdownLocator);
    }
    
    // ==================== Checkbox Actions ====================
    
    /**
     * Check checkbox by index
     * @param index Checkbox index (0-based)
     * @return FormPage for chaining
     */
    @Step("Check checkbox at index: {index}")
    public FormPage checkCheckbox(int index) {
        if (index < checkboxes.size()) {
            WebElement checkbox = checkboxes.get(index);
            if (!checkbox.isSelected()) {
                click(checkbox);
            }
        }
        return this;
    }
    
    /**
     * Uncheck checkbox by index
     * @param index Checkbox index (0-based)
     * @return FormPage for chaining
     */
    @Step("Uncheck checkbox at index: {index}")
    public FormPage uncheckCheckbox(int index) {
        if (index < checkboxes.size()) {
            WebElement checkbox = checkboxes.get(index);
            if (checkbox.isSelected()) {
                click(checkbox);
            }
        }
        return this;
    }
    
    /**
     * Check if checkbox is selected
     * @param index Checkbox index (0-based)
     * @return true if selected
     */
    @Step("Check if checkbox {index} is selected")
    public boolean isCheckboxSelected(int index) {
        if (index < checkboxes.size()) {
            return checkboxes.get(index).isSelected();
        }
        return false;
    }
    
    /**
     * Get number of checkboxes
     * @return Number of checkboxes on page
     */
    public int getCheckboxCount() {
        return checkboxes.size();
    }
    
    // ==================== Generic Form Methods ====================
    
    /**
     * Fill form with data from map
     * @param formData Map of field names to values
     * @return FormPage for chaining
     */
    @Step("Fill form with data")
    public FormPage fillForm(Map<String, String> formData) {
        formData.forEach((field, value) -> {
            By fieldLocator = By.id(field);
            if (isDisplayed(fieldLocator)) {
                type(fieldLocator, value);
            } else {
                // Try by name
                fieldLocator = By.name(field);
                if (isDisplayed(fieldLocator)) {
                    type(fieldLocator, value);
                }
            }
        });
        return this;
    }
    
    /**
     * Get page title
     * @return Page title text
     */
    @Step("Get page title")
    public String getFormPageTitle() {
        return getText(pageTitle);
    }
    
    // ==================== Validation ====================
    
    /**
     * Check if number input accepts only numbers
     * @param input Input to test
     * @return true if only numbers were accepted
     */
    @Step("Validate number input")
    public boolean validateNumberInput(String input) {
        clearNumberInput();
        enterNumber(input);
        String value = getNumberValue();
        // Number input should only contain digits
        return value.matches("^-?\\d*\\.?\\d*$");
    }
    
    // ==================== Page Validation ====================
    
    @Override
    public boolean isLoaded() {
        try {
            waitUtils.waitForPageLoad();
            return isDisplayed(pageTitle);
        } catch (Exception e) {
            logger.error("Form page not loaded: {}", e.getMessage());
            return false;
        }
    }
}
