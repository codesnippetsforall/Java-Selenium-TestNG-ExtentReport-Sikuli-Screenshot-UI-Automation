package com.automation;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.testng.annotations.Test;

/**
 * Utility class to dynamically generate TestNG XML configuration based on TestcaseToRun.config
 */
public class TestNGXmlGenerator {
    
    private static final String CONFIG_FILE = "TestcaseToRun.config";
    private static final String PACKAGE_TO_SCAN = "com.automation";
    private static final String OUTPUT_XML = "dynamic-testng.xml";
    
    /**
     * Main method to generate TestNG XML
     */
    public static void main(String[] args) {
        try {
            // Generate XML based on config
            generateTestNGXml();
            System.out.println("TestNG XML generated successfully at: " + OUTPUT_XML);
        } catch (Exception e) {
            System.err.println("Error generating TestNG XML: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Generate TestNG XML based on the config file content
     */
    public static void generateTestNGXml() throws IOException {
        // Read config file
        List<String> configLines = Files.readAllLines(Paths.get(CONFIG_FILE));
        
        // Create TestNG suite
        XmlSuite suite = new XmlSuite();
        suite.setName("WAMP Automation Suite");
        
        // Create test
        XmlTest test = new XmlTest(suite);
        test.setName("Desktop Automation Tests");
        
        // Process config file content
        if (configLines.isEmpty()) {
            System.err.println("Config file is empty. Using default behavior (run all tests).");
            configLines.add("ALL");
        }
        
        // Check if we should run all tests
        boolean runAllTests = configLines.stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .anyMatch(line -> line.equalsIgnoreCase("ALL"));
        
        // Get specific test methods if not running all
        List<String> specificMethods = configLines.stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.equalsIgnoreCase("ALL"))
                .collect(Collectors.toList());
        
        // Get all test classes in the package
        Map<String, List<String>> classToMethodsMap = new HashMap<>();
        
        if (runAllTests) {
            // Find all classes with @Test methods
            classToMethodsMap = findAllTestClasses();
        } else {
            // Process specific methods
            for (String methodPath : specificMethods) {
                String[] parts = methodPath.split("\\.");
                if (parts.length < 2) {
                    System.err.println("Invalid method format: " + methodPath);
                    continue;
                }
                
                // Last part is the method name
                String methodName = parts[parts.length - 1];
                
                // Everything before the last part is the class name
                String className = String.join(".", Arrays.copyOfRange(parts, 0, parts.length - 1));
                
                // Add to map
                classToMethodsMap.computeIfAbsent(className, k -> new ArrayList<>()).add(methodName);
            }
        }
        
        // Create XML classes
        List<XmlClass> xmlClasses = new ArrayList<>();
        
        for (Map.Entry<String, List<String>> entry : classToMethodsMap.entrySet()) {
            String className = entry.getKey();
            List<String> methods = entry.getValue();
            
            XmlClass xmlClass = new XmlClass(className);
            
            // If not running all tests, include specific methods
            if (!runAllTests && !methods.isEmpty()) {
                List<XmlInclude> includes = methods.stream()
                        .map(XmlInclude::new)
                        .collect(Collectors.toList());
                xmlClass.setIncludedMethods(includes);
            }
            
            xmlClasses.add(xmlClass);
        }
        
        test.setXmlClasses(xmlClasses);
        
        // Write to file
        try (FileWriter writer = new FileWriter(OUTPUT_XML)) {
            writer.write(suite.toXml());
        }
    }
    
    /**
     * Find all classes with @Test methods in the package
     */
    private static Map<String, List<String>> findAllTestClasses() {
        Map<String, List<String>> result = new HashMap<>();
        
        try {
            // Configure Reflections to scan the package
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .setUrls(ClasspathHelper.forPackage(PACKAGE_TO_SCAN))
                    .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner()));
            
            // Find all classes with @Test methods
            Set<Class<?>> testClasses = reflections.getTypesAnnotatedWith(Test.class);
            
            // Also find classes that have methods annotated with @Test
            Set<Class<?>> classesWithTestMethods = reflections.getSubTypesOf(Object.class)
                    .stream()
                    .filter(clazz -> {
                        for (Method method : clazz.getDeclaredMethods()) {
                            if (method.isAnnotationPresent(Test.class)) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toSet());
            
            // Combine both sets
            testClasses.addAll(classesWithTestMethods);
            
            // Process each class
            for (Class<?> testClass : testClasses) {
                String className = testClass.getName();
                
                // Get all @Test methods
                List<String> testMethods = Arrays.stream(testClass.getDeclaredMethods())
                        .filter(method -> method.isAnnotationPresent(Test.class))
                        .map(Method::getName)
                        .collect(Collectors.toList());
                
                if (!testMethods.isEmpty()) {
                    result.put(className, testMethods);
                }
            }
        } catch (Exception e) {
            System.err.println("Error scanning for test classes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
}
