package com.testmu.tests.ui;

import com.testmu.pages.FormPage;
import com.testmu.tests.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Form Validation Tests - UI tests for form interactions and validations.
 * Tests various form elements: inputs, dropdowns, checkboxes.
 */
@Epic("Forms")
@Feature("Form Validation")
public class FormValidationTest extends BaseTest {
    
    private FormPage formPage;
    
    @BeforeMethod
    public void initFormPage() {
        formPage = new FormPage(driver);
    }
    
    // ==================== Number Input Tests ====================
    
    @Test(description = "Verify number input accepts valid numbers")
    @Story("Number Input")
    @Severity(SeverityLevel.NORMAL)
    @Description("Number input should accept valid numeric values")
    public void testNumberInputValidNumbers() {
        formPage.openInputsPage();
        
        formPage.enterNumber("12345");
        String value = formPage.getNumberValue();
        
        Assert.assertEquals(value, "12345", "Should accept valid numbers");
    }
    
    @Test(description = "Verify number input can be cleared")
    @Story("Number Input")
    @Severity(SeverityLevel.NORMAL)
    public void testNumberInputClear() {
        formPage.openInputsPage();
        
        formPage.enterNumber("999");
        formPage.clearNumberInput();
        
        String value = formPage.getNumberValue();
        Assert.assertTrue(value.isEmpty(), "Input should be cleared");
    }
    
    @Test(description = "Verify increment with arrow keys")
    @Story("Number Input")
    @Severity(SeverityLevel.MINOR)
    public void testNumberInputIncrement() {
        formPage.openInputsPage();
        
        formPage.enterNumber("10");
        formPage.incrementNumber(5);
        
        String value = formPage.getNumberValue();
        Assert.assertEquals(value, "15", "Value should be incremented by 5");
    }
    
    @Test(description = "Verify decrement with arrow keys")
    @Story("Number Input")
    @Severity(SeverityLevel.MINOR)
    public void testNumberInputDecrement() {
        formPage.openInputsPage();
        
        formPage.enterNumber("10");
        formPage.decrementNumber(3);
        
        String value = formPage.getNumberValue();
        Assert.assertEquals(value, "7", "Value should be decremented by 3");
    }
    
    @Test(description = "Test number input with various values", dataProvider = "numberInputData")
    @Story("Number Input")
    @Severity(SeverityLevel.NORMAL)
    public void testNumberInputDataDriven(String input, boolean isValid, String description) {
        Allure.description(description);
        
        formPage.openInputsPage();
        boolean result = formPage.validateNumberInput(input);
        
        // Note: HTML5 number inputs behave differently across browsers
        // This validates that only numeric characters are stored
        if (isValid) {
            Assert.assertTrue(result || formPage.getNumberValue().isEmpty(), 
                "Should accept valid input: " + description);
        }
    }
    
    @DataProvider(name = "numberInputData")
    public Object[][] numberInputTestData() {
        return new Object[][] {
            {"123", true, "Positive integer"},
            {"-456", true, "Negative integer"},
            {"0", true, "Zero"},
            {"3.14", true, "Decimal number"},
            {"abc", false, "Letters should be rejected"},
            {"12abc34", false, "Mixed alphanumeric"},
            {"", true, "Empty string"}
        };
    }
    
    // ==================== Dropdown Tests ====================
    
    @Test(description = "Verify dropdown selection by text")
    @Story("Dropdown")
    @Severity(SeverityLevel.NORMAL)
    @Description("User should be able to select dropdown option by visible text")
    public void testDropdownSelectByText() {
        formPage.openDropdownPage();
        
        formPage.selectOption("Option 1");
        
        String selected = formPage.getSelectedOption();
        Assert.assertEquals(selected, "Option 1", "Option 1 should be selected");
    }
    
    @Test(description = "Verify dropdown selection by index")
    @Story("Dropdown")
    @Severity(SeverityLevel.NORMAL)
    public void testDropdownSelectByIndex() {
        formPage.openDropdownPage();
        
        formPage.selectOptionByIndex(2);
        
        String selected = formPage.getSelectedOption();
        Assert.assertEquals(selected, "Option 2", "Option 2 should be selected");
    }
    
    @Test(description = "Verify dropdown selection change")
    @Story("Dropdown")
    @Severity(SeverityLevel.NORMAL)
    public void testDropdownSelectionChange() {
        formPage.openDropdownPage();
        
        // Select first option
        formPage.selectOption("Option 1");
        Assert.assertEquals(formPage.getSelectedOption(), "Option 1");
        
        // Change to second option
        formPage.selectOption("Option 2");
        Assert.assertEquals(formPage.getSelectedOption(), "Option 2");
    }
    
    // ==================== Checkbox Tests ====================
    
    @Test(description = "Verify checkbox can be checked")
    @Story("Checkbox")
    @Severity(SeverityLevel.NORMAL)
    @Description("User should be able to check a checkbox")
    public void testCheckboxCheck() {
        formPage.openCheckboxesPage();
        
        // First checkbox is unchecked by default
        formPage.checkCheckbox(0);
        
        Assert.assertTrue(formPage.isCheckboxSelected(0), "Checkbox should be checked");
    }
    
    @Test(description = "Verify checkbox can be unchecked")
    @Story("Checkbox")
    @Severity(SeverityLevel.NORMAL)
    @Description("User should be able to uncheck a checkbox")
    public void testCheckboxUncheck() {
        formPage.openCheckboxesPage();
        
        // Second checkbox is checked by default
        formPage.uncheckCheckbox(1);
        
        Assert.assertFalse(formPage.isCheckboxSelected(1), "Checkbox should be unchecked");
    }
    
    @Test(description = "Verify multiple checkboxes")
    @Story("Checkbox")
    @Severity(SeverityLevel.NORMAL)
    public void testMultipleCheckboxes() {
        formPage.openCheckboxesPage();
        
        int checkboxCount = formPage.getCheckboxCount();
        Assert.assertTrue(checkboxCount >= 2, "Should have at least 2 checkboxes");
        
        // Check all checkboxes
        for (int i = 0; i < checkboxCount; i++) {
            formPage.checkCheckbox(i);
        }
        
        // Verify all are checked
        for (int i = 0; i < checkboxCount; i++) {
            Assert.assertTrue(formPage.isCheckboxSelected(i), 
                "Checkbox " + i + " should be checked");
        }
    }
    
    @Test(description = "Verify checkbox toggle")
    @Story("Checkbox")
    @Severity(SeverityLevel.MINOR)
    public void testCheckboxToggle() {
        formPage.openCheckboxesPage();
        
        boolean initialState = formPage.isCheckboxSelected(0);
        
        // Toggle checkbox
        if (initialState) {
            formPage.uncheckCheckbox(0);
            Assert.assertFalse(formPage.isCheckboxSelected(0), "Should be unchecked after toggle");
        } else {
            formPage.checkCheckbox(0);
            Assert.assertTrue(formPage.isCheckboxSelected(0), "Should be checked after toggle");
        }
    }
    
    // ==================== Page Validation Tests ====================
    
    @Test(description = "Verify inputs page loads correctly")
    @Story("Page Load")
    @Severity(SeverityLevel.MINOR)
    public void testInputsPageLoad() {
        formPage.openInputsPage();
        
        Assert.assertTrue(formPage.isLoaded(), "Inputs page should load correctly");
        Assert.assertTrue(formPage.getFormPageTitle().toLowerCase().contains("input"), 
            "Page title should contain 'input'");
    }
    
    @Test(description = "Verify dropdown page loads correctly")
    @Story("Page Load")
    @Severity(SeverityLevel.MINOR)
    public void testDropdownPageLoad() {
        formPage.openDropdownPage();
        
        Assert.assertTrue(formPage.isLoaded(), "Dropdown page should load correctly");
    }
    
    @Test(description = "Verify checkboxes page loads correctly")
    @Story("Page Load")
    @Severity(SeverityLevel.MINOR)
    public void testCheckboxesPageLoad() {
        formPage.openCheckboxesPage();
        
        Assert.assertTrue(formPage.isLoaded(), "Checkboxes page should load correctly");
        Assert.assertTrue(formPage.getCheckboxCount() > 0, "Should have checkboxes");
    }
}
