package com.testmu.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JSON/YAML/CSV Utilities - Handles test data file operations.
 * Supports reading and writing JSON, YAML, and CSV files.
 */
public class JsonUtils {
    private static final Logger logger = LogManager.getLogger(JsonUtils.class);
    
    // Jackson ObjectMappers
    private static final ObjectMapper jsonMapper = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    
    // ==================== JSON Operations ====================
    
    /**
     * Read JSON file and convert to specified class
     * @param filePath Path to JSON file
     * @param clazz Target class
     * @param <T> Type parameter
     * @return Deserialized object
     */
    public static <T> T readJson(String filePath, Class<T> clazz) {
        logger.debug("Reading JSON file: {}", filePath);
        try {
            return jsonMapper.readValue(new File(filePath), clazz);
        } catch (IOException e) {
            logger.error("Failed to read JSON file: {}", filePath, e);
            throw new RuntimeException("Failed to read JSON file: " + filePath, e);
        }
    }
    
    /**
     * Read JSON file and convert to list of specified class
     * @param filePath Path to JSON file
     * @param clazz Element class
     * @param <T> Type parameter
     * @return List of deserialized objects
     */
    public static <T> List<T> readJsonArray(String filePath, Class<T> clazz) {
        logger.debug("Reading JSON array file: {}", filePath);
        try {
            return jsonMapper.readValue(
                new File(filePath),
                jsonMapper.getTypeFactory().constructCollectionType(List.class, clazz)
            );
        } catch (IOException e) {
            logger.error("Failed to read JSON array file: {}", filePath, e);
            throw new RuntimeException("Failed to read JSON array file: " + filePath, e);
        }
    }
    
    /**
     * Read JSON file as Map
     * @param filePath Path to JSON file
     * @return Map representation
     */
    public static Map<String, Object> readJsonAsMap(String filePath) {
        logger.debug("Reading JSON file as map: {}", filePath);
        try {
            return jsonMapper.readValue(
                new File(filePath),
                new TypeReference<Map<String, Object>>() {}
            );
        } catch (IOException e) {
            logger.error("Failed to read JSON as map: {}", filePath, e);
            throw new RuntimeException("Failed to read JSON as map: " + filePath, e);
        }
    }
    
    /**
     * Read JSON file as JsonNode for flexible traversal
     * @param filePath Path to JSON file
     * @return JsonNode
     */
    public static JsonNode readJsonNode(String filePath) {
        logger.debug("Reading JSON file as JsonNode: {}", filePath);
        try {
            return jsonMapper.readTree(new File(filePath));
        } catch (IOException e) {
            logger.error("Failed to read JSON as JsonNode: {}", filePath, e);
            throw new RuntimeException("Failed to read JSON as JsonNode: " + filePath, e);
        }
    }
    
    /**
     * Parse JSON string to specified class
     * @param json JSON string
     * @param clazz Target class
     * @param <T> Type parameter
     * @return Deserialized object
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return jsonMapper.readValue(json, clazz);
        } catch (IOException e) {
            logger.error("Failed to parse JSON string", e);
            throw new RuntimeException("Failed to parse JSON string", e);
        }
    }
    
    /**
     * Convert object to JSON string
     * @param object Object to convert
     * @return JSON string
     */
    public static String toJson(Object object) {
        try {
            return jsonMapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.error("Failed to convert object to JSON", e);
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }
    
    /**
     * Convert object to pretty-printed JSON string
     * @param object Object to convert
     * @return Pretty JSON string
     */
    public static String toPrettyJson(Object object) {
        try {
            return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (IOException e) {
            logger.error("Failed to convert object to pretty JSON", e);
            throw new RuntimeException("Failed to convert object to pretty JSON", e);
        }
    }
    
    /**
     * Write object to JSON file
     * @param filePath Path to JSON file
     * @param object Object to write
     */
    public static void writeJson(String filePath, Object object) {
        logger.debug("Writing JSON file: {}", filePath);
        try {
            jsonMapper.writeValue(new File(filePath), object);
        } catch (IOException e) {
            logger.error("Failed to write JSON file: {}", filePath, e);
            throw new RuntimeException("Failed to write JSON file: " + filePath, e);
        }
    }
    
    // ==================== YAML Operations ====================
    
    /**
     * Read YAML file and convert to specified class
     * @param filePath Path to YAML file
     * @param clazz Target class
     * @param <T> Type parameter
     * @return Deserialized object
     */
    public static <T> T readYaml(String filePath, Class<T> clazz) {
        logger.debug("Reading YAML file: {}", filePath);
        try {
            return yamlMapper.readValue(new File(filePath), clazz);
        } catch (IOException e) {
            logger.error("Failed to read YAML file: {}", filePath, e);
            throw new RuntimeException("Failed to read YAML file: " + filePath, e);
        }
    }
    
    /**
     * Read YAML file and convert to list of specified class
     * @param filePath Path to YAML file
     * @param clazz Element class
     * @param <T> Type parameter
     * @return List of deserialized objects
     */
    public static <T> List<T> readYamlArray(String filePath, Class<T> clazz) {
        logger.debug("Reading YAML array file: {}", filePath);
        try {
            return yamlMapper.readValue(
                new File(filePath),
                yamlMapper.getTypeFactory().constructCollectionType(List.class, clazz)
            );
        } catch (IOException e) {
            logger.error("Failed to read YAML array file: {}", filePath, e);
            throw new RuntimeException("Failed to read YAML array file: " + filePath, e);
        }
    }
    
    /**
     * Read YAML file as Map
     * @param filePath Path to YAML file
     * @return Map representation
     */
    public static Map<String, Object> readYamlAsMap(String filePath) {
        logger.debug("Reading YAML file as map: {}", filePath);
        try {
            return yamlMapper.readValue(
                new File(filePath),
                new TypeReference<Map<String, Object>>() {}
            );
        } catch (IOException e) {
            logger.error("Failed to read YAML as map: {}", filePath, e);
            throw new RuntimeException("Failed to read YAML as map: " + filePath, e);
        }
    }
    
    // ==================== CSV Operations ====================
    
    /**
     * Read CSV file and return as list of string arrays
     * @param filePath Path to CSV file
     * @return List of rows (each row is String array)
     */
    public static List<String[]> readCsv(String filePath) {
        logger.debug("Reading CSV file: {}", filePath);
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            return reader.readAll();
        } catch (IOException | CsvException e) {
            logger.error("Failed to read CSV file: {}", filePath, e);
            throw new RuntimeException("Failed to read CSV file: " + filePath, e);
        }
    }
    
    /**
     * Read CSV file skipping header row
     * @param filePath Path to CSV file
     * @return List of data rows (excluding header)
     */
    public static List<String[]> readCsvSkipHeader(String filePath) {
        List<String[]> allRows = readCsv(filePath);
        if (allRows.isEmpty()) {
            return allRows;
        }
        return allRows.subList(1, allRows.size());
    }
    
    /**
     * Read CSV file and convert to list of maps (using header as keys)
     * @param filePath Path to CSV file
     * @return List of maps where keys are column headers
     */
    public static List<Map<String, String>> readCsvAsMaps(String filePath) {
        logger.debug("Reading CSV file as maps: {}", filePath);
        List<String[]> rows = readCsv(filePath);
        
        if (rows.isEmpty()) {
            return new ArrayList<>();
        }
        
        String[] headers = rows.get(0);
        List<Map<String, String>> result = new ArrayList<>();
        
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            Map<String, String> map = new java.util.HashMap<>();
            for (int j = 0; j < headers.length && j < row.length; j++) {
                map.put(headers[j], row[j]);
            }
            result.add(map);
        }
        
        return result;
    }
    
    // ==================== Resource Loading ====================
    
    /**
     * Read JSON from classpath resource
     * @param resourcePath Resource path (e.g., "/testdata/users.json")
     * @param clazz Target class
     * @param <T> Type parameter
     * @return Deserialized object
     */
    public static <T> T readJsonFromResource(String resourcePath, Class<T> clazz) {
        logger.debug("Reading JSON from resource: {}", resourcePath);
        try (InputStream is = JsonUtils.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Resource not found: " + resourcePath);
            }
            return jsonMapper.readValue(is, clazz);
        } catch (IOException e) {
            logger.error("Failed to read JSON resource: {}", resourcePath, e);
            throw new RuntimeException("Failed to read JSON resource: " + resourcePath, e);
        }
    }
    
    /**
     * Get the ObjectMapper instance for advanced operations
     * @return ObjectMapper instance
     */
    public static ObjectMapper getMapper() {
        return jsonMapper;
    }
}
