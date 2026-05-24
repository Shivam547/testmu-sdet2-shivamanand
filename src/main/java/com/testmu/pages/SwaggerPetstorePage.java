package com.testmu.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import io.qameta.allure.Step;

/**
 * Page object for interacting with the Petstore Swagger UI at https://petstore.swagger.io/
 * Provides helpers to open the UI and execute the "GET /pet/{petId}" operation via the UI.
 */
public class SwaggerPetstorePage extends BasePage {

    private static final String SWAGGER_UI_URL = "https://petstore.swagger.io/";
    private static final String PET_ID_PATH = "/pet/{petId}";

    private static final By COOKIES_BUTTON = By.xpath("//button[text()='Allow all cookies']");
    private static final By SWAGGER_CONTAINER = By.cssSelector("div.swagger-ui, div#swagger-ui");
    private static final By OPBLOCK_GET_PET = By.xpath("//div[contains(@class,'opblock') and .//span[contains(@class,'opblock-summary-path') and contains(., '/pet/{petId}')]]");
    private static final By OPBLOCK_SUMMARY_BUTTON = By.xpath(".//button[contains(@class,'opblock-summary')]");
    private static final By TRY_IT_OUT_BUTTON = By.xpath(".//button[normalize-space(.)='Try it out' or contains(., 'Try it out')]");
    private static final By PET_ID_INPUT = By.xpath(".//input[@name='petId'] | .//input[contains(@placeholder,'petId')] | .//input[@type='number'] | .//textarea[@name='petId']");
    private static final By EXECUTE_BUTTON = By.xpath(".//button[normalize-space(.)='Execute' or contains(., 'Execute')]");
    private static final By RESPONSE_BODY = By.xpath(".//div[contains(@class,'responses-wrapper')]//pre//span[contains(text(), 'https')]");

    public SwaggerPetstorePage(WebDriver driver) {
        super(driver);
    }

    @Step("Open Swagger Petstore UI")
    public SwaggerPetstorePage open() {
        driver.get(SWAGGER_UI_URL);
        waitUtils.waitForPageLoad();
        clickElement(COOKIES_BUTTON);
        return this;
    }

    @Step("Check if Swagger UI is loaded")
    public boolean isLoaded() {
        try {
            waitUtils.waitForVisible(SWAGGER_CONTAINER, 15);
            return true;
        } catch (Exception e) {
            logger.warn("Swagger UI not loaded: {}", e.getMessage());
            return false;
        }
    }

    @Step("Execute GET /pet/{petId} via Swagger UI for id: {petId}")
    public String executeGetPetById(long petId) {
        WebElement opblock = waitUtils.waitForVisible(OPBLOCK_GET_PET, 15);

        expandOpblock(opblock);
        clickFirstButton(opblock, TRY_IT_OUT_BUTTON, "Try it out");
        enterPetId(opblock, petId);
        clickFirstButton(opblock, EXECUTE_BUTTON, "Execute");

        return readResponseBody(opblock);
    }

    private void expandOpblock(WebElement opblock) {
        try {
            WebElement summary = opblock.findElement(OPBLOCK_SUMMARY_BUTTON);
            if (summary != null && summary.isDisplayed()) {
                summary.click();
            }
        } catch (Exception ignored) {
            // Already expanded or not clickable
        }
    }

    private void clickFirstButton(WebElement context, By locator, String buttonName) {
        try {
            List<WebElement> buttons = context.findElements(locator);
            if (!buttons.isEmpty()) {
                waitUtils.waitForClickable(locator, 15);
                buttons.get(0).click();
            }
        } catch (Exception e) {
            logger.warn("Could not click {} button: {}", buttonName, e.getMessage());
        }
    }

    private void enterPetId(WebElement opblock, long petId) {
        try {
            WebElement input = opblock.findElement(PET_ID_INPUT);
            input.clear();
            input.sendKeys(String.valueOf(petId));
        } catch (Exception e) {
            logger.warn("Could not find parameter input for petId: {}", e.getMessage());
        }
    }

    private String readResponseBody(WebElement opblock) {
        try {
            WebElement pre = opblock.findElement(RESPONSE_BODY);
            waitUtils.waitForVisible(RESPONSE_BODY, 15);
            return pre.getText();
        } catch (Exception e) {
            logger.warn("Could not extract response body via UI: {}", e.getMessage());
            return "";
        }
    }

    private void clickElement(By locator) {
        try {
            waitUtils.waitForClickable(locator, 15);
            driver.findElement(locator).click();
        } catch (Exception e) {
            logger.warn("Could not click element {}: {}", locator, e.getMessage());
        }
    }
}
