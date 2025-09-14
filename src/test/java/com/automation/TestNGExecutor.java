package com.automation;

import org.testng.TestNG;

/**
 * TestNG executor to run tests programmatically
 */
public class TestNGExecutor {
    
    /**
     * Main method to execute TestNG with dynamically generated XML
     */
    public static void main(String[] args) {
        try {
            // First generate the XML
            TestNGXmlGenerator.generateTestNGXml();
            
            // Create TestNG instance
            TestNG testng = new TestNG();
            
            // Set the XML file
            testng.setTestSuites(java.util.Collections.singletonList("dynamic-testng.xml"));
            
            // Run tests
            testng.run();
            
            // Exit with appropriate code
            System.exit(testng.getStatus());
        } catch (Exception e) {
            System.err.println("Error executing TestNG: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
