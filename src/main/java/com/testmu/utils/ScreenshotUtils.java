package com.testmu.utils;

import com.testmu.config.ConfigReader;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Screenshot Utilities - Captures and manages screenshots for test reporting.
 * Integrates with Allure for automatic attachment.
 */
public class ScreenshotUtils {
    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = ConfigReader.getScreenshotPath();
    private static final DateTimeFormatter TIMESTAMP_FORMAT = 
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    
    /**
     * Capture screenshot and save to file
     * @param driver WebDriver instance
     * @param testName Test name for filename
     * @return Path to saved screenshot or null on failure
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        if (driver == null) {
            logger.warn("Cannot capture screenshot - driver is null");
            return null;
        }
        
        try {
            // Ensure screenshot directory exists
            Path screenshotDir = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(screenshotDir)) {
                Files.createDirectories(screenshotDir);
                logger.debug("Created screenshot directory: {}", screenshotDir);
            }
            
            // Generate unique filename
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String sanitizedTestName = sanitizeFilename(testName);
            String fileName = String.format("%s_%s.png", sanitizedTestName, timestamp);
            
            // Capture screenshot as bytes
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            
            // Save to file
            Path screenshotPath = screenshotDir.resolve(fileName);
            Files.write(screenshotPath, screenshotBytes);
            
            // Attach to Allure report
            attachToAllure(screenshotBytes, testName + " - Screenshot");
            
            logger.info("Screenshot saved: {}", screenshotPath);
            return screenshotPath.toAbsolutePath().toString();
            
        } catch (IOException e) {
            logger.error("Failed to capture screenshot: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error capturing screenshot: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Capture screenshot and attach to Allure only (no file save)
     * @param driver WebDriver instance
     * @param attachmentName Name for the Allure attachment
     */
    public static void attachScreenshotToAllure(WebDriver driver, String attachmentName) {
        if (driver == null) {
            logger.warn("Cannot capture screenshot - driver is null");
            return;
        }
        
        try {
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            attachToAllure(screenshotBytes, attachmentName);
            logger.debug("Screenshot attached to Allure: {}", attachmentName);
        } catch (Exception e) {
            logger.error("Failed to attach screenshot to Allure: {}", e.getMessage());
        }
    }
    
    /**
     * Capture screenshot as Base64 string
     * @param driver WebDriver instance
     * @return Base64 encoded screenshot string or null on failure
     */
    public static String captureScreenshotAsBase64(WebDriver driver) {
        if (driver == null) {
            logger.warn("Cannot capture screenshot - driver is null");
            return null;
        }
        
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            logger.error("Failed to capture Base64 screenshot: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Capture screenshot as byte array
     * @param driver WebDriver instance
     * @return Screenshot bytes or null on failure
     */
    public static byte[] captureScreenshotAsBytes(WebDriver driver) {
        if (driver == null) {
            logger.warn("Cannot capture screenshot - driver is null");
            return null;
        }
        
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot bytes: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Attach bytes to Allure report as image
     * @param screenshotBytes Screenshot bytes
     * @param name Attachment name
     */
    public static void attachToAllure(byte[] screenshotBytes, String name) {
        if (screenshotBytes == null || screenshotBytes.length == 0) {
            logger.warn("Cannot attach empty screenshot to Allure");
            return;
        }
        
        Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshotBytes), "png");
    }
    
    /**
     * Attach text content to Allure report
     * @param content Text content
     * @param name Attachment name
     */
    public static void attachTextToAllure(String content, String name) {
        Allure.addAttachment(name, "text/plain", content);
    }
    
    /**
     * Attach HTML content to Allure report
     * @param html HTML content
     * @param name Attachment name
     */
    public static void attachHtmlToAllure(String html, String name) {
        Allure.addAttachment(name, "text/html", html);
    }
    
    /**
     * Attach JSON content to Allure report
     * @param json JSON content
     * @param name Attachment name
     */
    public static void attachJsonToAllure(String json, String name) {
        Allure.addAttachment(name, "application/json", json);
    }
    
    /**
     * Clean old screenshots from directory (older than specified days)
     * @param olderThanDays Delete files older than this many days
     */
    public static void cleanOldScreenshots(int olderThanDays) {
        try {
            Path screenshotDir = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(screenshotDir)) {
                return;
            }
            
            long cutoffTime = System.currentTimeMillis() - (olderThanDays * 24 * 60 * 60 * 1000L);
            
            Files.list(screenshotDir)
                .filter(path -> path.toString().endsWith(".png"))
                .filter(path -> {
                    try {
                        return Files.getLastModifiedTime(path).toMillis() < cutoffTime;
                    } catch (IOException e) {
                        return false;
                    }
                })
                .forEach(path -> {
                    try {
                        Files.delete(path);
                        logger.debug("Deleted old screenshot: {}", path);
                    } catch (IOException e) {
                        logger.warn("Failed to delete screenshot: {}", path);
                    }
                });
                
            logger.info("Cleaned screenshots older than {} days", olderThanDays);
        } catch (IOException e) {
            logger.error("Failed to clean old screenshots: {}", e.getMessage());
        }
    }
    
    /**
     * Sanitize filename by removing invalid characters
     * @param filename Original filename
     * @return Sanitized filename
     */
    private static String sanitizeFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "screenshot";
        }
        // Replace invalid characters with underscore
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_")
                      .replaceAll("_+", "_")  // Replace multiple underscores with single
                      .substring(0, Math.min(filename.length(), 100));  // Limit length
    }
}
