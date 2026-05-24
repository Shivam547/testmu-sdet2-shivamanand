package com.testmu.tests.integration;

import com.testmu.api.ApiClient;
import com.testmu.pages.SwaggerPetstorePage;
import com.testmu.tests.BaseTest;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

@Epic("Integration Testing")
@Feature("Petstore API-UI Integration")
public class PetstoreApiUiIntegrationTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(PetstoreApiUiIntegrationTest.class);

    private ApiClient apiClient;
    private SwaggerPetstorePage swaggerPage;

    private static final String PETSTORE_API_BASE = "https://petstore.swagger.io/v2";

    @BeforeClass
    public void setup() {
        apiClient = new ApiClient(PETSTORE_API_BASE);
    }

    @Test(description = "Create a pet via API, verify via API and Swagger UI, then cleanup")
    @Severity(SeverityLevel.BLOCKER)
    public void testCreatePetAndVerifyViaUi() {
        long petId = System.currentTimeMillis() % 1_000_000;
        String petName = "test-pet-" + petId;

        Map<String, Object> petPayload = new HashMap<>();
        petPayload.put("id", petId);
        petPayload.put("name", petName);
        petPayload.put("photoUrls", Collections.singletonList("http://example.com/photo.jpg"));
        petPayload.put("status", "available");

        Allure.step("Create pet via API");
        Response createResp = apiClient.post("/pet", petPayload);
        Assert.assertTrue(apiClient.isSuccessful(createResp), "Create pet API should succeed");

        logger.info("Created pet id={} name={}", petId, petName);

        try {
            Allure.step("Verify pet via API GET");
            Response getResp = apiClient.get("/pet/" + petId);
            Assert.assertTrue(apiClient.isSuccessful(getResp), "GET pet by id via API should succeed");
            String apiBody = getResp.getBody().asString();
            Assert.assertTrue(apiBody.contains(petName), "API response should contain pet name");

            Allure.step("Open Swagger UI and execute GET /pet/{petId}");
            swaggerPage = new SwaggerPetstorePage(driver);
            swaggerPage.open();
            Assert.assertTrue(swaggerPage.isLoaded(), "Swagger UI should load");

            String uiResponse = swaggerPage.executeGetPetById(petId);
            Assert.assertNotNull(uiResponse, "UI response should not be null");
            Assert.assertTrue(uiResponse.contains(String.valueOf(petId)) || uiResponse.contains(petName),
                "UI response should contain pet id or name");

            logger.info("UI executed GET returned data containing pet: {}", petName);
        } finally {
            Allure.step("Cleanup: delete pet via API");
            Response del = apiClient.delete("/pet/" + petId);
            if (!apiClient.isSuccessful(del)) {
                logger.warn("Failed to delete pet id={} via API; status={}", petId, del.getStatusCode());
            } else {
                logger.info("Deleted pet id={}", petId);
            }
        }
    }
}
