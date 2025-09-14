package com.automation;

import org.testng.IAlterSuiteListener;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * TestNG listener to dynamically alter the test suite based on TestcaseToRun.config
 */
public class DynamicTestNGListener implements IAlterSuiteListener {
    
    @Override
    public void alter(List<XmlSuite> suites) {
        try {
            // Generate the dynamic TestNG XML
            TestNGXmlGenerator.generateTestNGXml();
            
            // Read the generated XML
            File xmlFile = new File("dynamic-testng.xml");
            if (xmlFile.exists()) {
                // Parse the XML content
                String xmlContent = new String(Files.readAllBytes(Paths.get("dynamic-testng.xml")));
                
                // Clear existing suites and add our dynamic one
                if (!suites.isEmpty()) {
                    XmlSuite existingSuite = suites.get(0);
                    
                    // Parse the dynamic XML into a suite using Parser
                    Parser parser = new Parser("dynamic-testng.xml");
                    List<XmlSuite> dynamicSuites = parser.parseToList();
                    
                    if (!dynamicSuites.isEmpty()) {
                        XmlSuite dynamicSuite = dynamicSuites.get(0);
                        
                        // Copy tests from dynamic suite to existing suite
                        existingSuite.getTests().clear();
                        existingSuite.getTests().addAll(dynamicSuite.getTests());
                    }
                    
                    System.out.println("Dynamic TestNG configuration applied successfully.");
                }
            } else {
                System.err.println("Dynamic TestNG XML file not found. Using default configuration.");
            }
        } catch (Exception e) {
            System.err.println("Error applying dynamic TestNG configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
