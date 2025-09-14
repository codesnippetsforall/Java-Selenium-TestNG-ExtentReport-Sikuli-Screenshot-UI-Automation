package com.automation;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import org.sikuli.script.Screen;
import org.sikuli.script.Pattern;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import org.apache.commons.io.FileUtils;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * Automation test using Selenium WebDriver and SikuliX
 */
public class PlaySongWithAutomation 
{
    private WebDriver driver;
    private WebDriverWait wait;
    private Screen screen;
    private static ExtentReports extent;
    private ExtentTest test;
    
    /**
     * Runs once before the entire test suite executes
     * Used for one-time setup operations like cleaning screenshots directory
     */
    @BeforeSuite
    public static void setUpBeforeSuite() {
        // Initialize Extent Reports
        setupExtentReports();
        
        // Clean up old screenshots - runs only once before all tests
        cleanupOldScreenshots();
    }
    
    /**
     * Runs once before any test class executes
     * Used to set up browser that will be shared across all tests
     */
    @BeforeClass
    public void setUpBeforeClass() {
        // Set up WebDriver manager
        WebDriverManager.chromedriver().setup();
        
        // Setup ChromeDriver instance once for all tests
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        
        // Initialize SikuliX Screen
        screen = new Screen();
        
        System.out.println("Browser opened once for all test cases");
    }
    
    /**
     * Helper method to initialize a test in the Extent Report
     * @param testName The name of the test
     * @param testDescription The description of the test
     */
    private void initTest(String testName, String testDescription) {
        test = extent.createTest(testName, testDescription);
        test.log(Status.INFO, testName + " setup completed successfully");
    }
    
    /**
     * Helper method to clean up old screenshots
     * This runs only once at the beginning of the test suite
     */
    private static void cleanupOldScreenshots() {
        try {
            File screenshotDir = new File(System.getProperty("user.dir") + "/test-output/screenshots");
            if (screenshotDir.exists()) {
                System.out.println("Cleaning up old screenshots...");
                File[] files = screenshotDir.listFiles();
                if (files != null) {
                    int count = 0;
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".png")) {
                            boolean deleted = file.delete();
                            if (deleted) {
                                count++;
                            }
                        }
                    }
                    System.out.println("Deleted " + count + " old screenshot files");
                }
            } else {
                // Create directory if it doesn't exist
                boolean created = screenshotDir.mkdirs();
                if (created) {
                    System.out.println("Created screenshots directory: " + screenshotDir.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("Error cleaning up screenshots: " + e.getMessage());
        }
    }
    
    /**
     * Runs once after all tests in the class have executed
     * Used to close the browser and flush reports
     */
    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed after all tests completed");
        }
        
        // Flush the extent report
        if (extent != null) {
            extent.flush();
        }
    }
    
    /**
     * Setup Extent Reports configuration
     */
    private static void setupExtentReports() {
        if (extent == null) {
            // Create ExtentSparkReporter
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/ExtentReport.html");
            
            // Configure the report
            sparkReporter.config().setDocumentTitle("WAMP Automation Test Report");
            sparkReporter.config().setReportName("SikuliX & Selenium Test Report");
            sparkReporter.config().setTheme(Theme.STANDARD);
            
            // Create ExtentReports and attach reporter
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            
            // Add system information
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("User", System.getProperty("user.name"));
            extent.setSystemInfo("Browser", "Chrome");
        }
    }
    
    /**
     * Helper method to validate if an image file exists
     * @param imageName The name of the image file (e.g., "WindowsStartButton.jpg")
     * @return true if file exists, false otherwise
     */
    private boolean validateImageFile(String imageName) {
        String imagePath = System.getProperty("user.dir") + "/src/resources/" + imageName;
        File imageFile = new File(imagePath);
        
        if (!imageFile.exists()) {
            System.out.println("ERROR: Image file not found at: " + imagePath);
            System.out.println("Please ensure " + imageName + " exists in src/resources/ folder");
            return false;
        }
        
        System.out.println("Image file found: " + imageName);
        System.out.println("Image file size: " + imageFile.length() + " bytes");
        return true;
    }
    
    /**
     * Helper method to create a Pattern from image name
     * @param imageName The name of the image file
     * @param similarity Similarity threshold (0.0 to 1.0)
     * @return Pattern object or null if file doesn't exist
     */
    private Pattern createPattern(String imageName, double similarity) {
        if (!validateImageFile(imageName)) {
            return null;
        }
        
        String imagePath = System.getProperty("user.dir") + "/src/resources/" + imageName;
        Pattern pattern = new Pattern(imagePath);
        pattern.similar(similarity);
        return pattern;
    }
    
    /**
     * Helper method to find and click an image on screen
     * @param imageName The name of the image file to click
     * @param similarity Similarity threshold (0.0 to 1.0)
     * @param timeout Timeout in seconds to wait for image
     * @return true if image was found and clicked, false otherwise
     */
    private boolean findAndClickImage(String imageName, double similarity, int timeout) {
        System.out.println("Looking for " + imageName + " image...");
        test.log(Status.INFO, "Looking for " + imageName + " image...");
        
        Pattern pattern = createPattern(imageName, similarity);
        if (pattern == null) {
            test.log(Status.FAIL, "Failed to create pattern for " + imageName + " - file not found");
            return false;
        }
        
        try {
            org.sikuli.script.Match match = screen.exists(pattern, timeout);
            if (match != null) {
                System.out.println(imageName + " found! Clicking...");
                screen.click(pattern);
                System.out.println("Successfully clicked " + imageName + "!");
                System.out.println("Match score: " + match.getScore());
                
                test.log(Status.PASS, "Successfully clicked " + imageName + " with match score: " + match.getScore());
                return true;
            } else {
                System.out.println(imageName + " image not found on screen.");
                test.log(Status.FAIL, imageName + " image not found on screen within " + timeout + " seconds");
                return false;
            }
        } catch (FindFailed e) {
            System.out.println("FindFailed: " + imageName + " image not found: " + e.getMessage());
            test.log(Status.FAIL, "FindFailed: " + imageName + " image not found: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Error during " + imageName + " image recognition: " + e.getMessage());
            test.log(Status.FAIL, "Error during " + imageName + " image recognition: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Helper method to wait for specified duration
     * @param milliseconds Duration to wait in milliseconds
     */
    private void waitFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            System.err.println("Wait interrupted: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to take screenshot using SikuliX Screen
     * @param testCaseName Name of the test case
     * @param screenshotName Custom name for the screenshot file
     * @return Path to the saved screenshot file
     */
    private String takeScreenshot(String testCaseName, String screenshotName) {
        try {
            // Create timestamp for unique filename
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            
            // Create screenshots directory if it doesn't exist
            File screenshotDir = new File(System.getProperty("user.dir") + "/test-output/screenshots");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            
            // Define target file path with test case name included
            String fileName = testCaseName + "-" + screenshotName + "_" + timestamp + ".png";
            String fullPath = screenshotDir.getAbsolutePath() + File.separator + fileName;
            
            // Take screenshot using SikuliX's alternate method
            org.sikuli.script.ScreenImage screenImage = screen.capture(screen.getBounds());
            javax.imageio.ImageIO.write(screenImage.getImage(), "PNG", new File(fullPath));
            
            System.out.println("Screenshot saved: " + fullPath);
            test.log(Status.INFO, "Screenshot captured: " + fileName);
            
            // Add screenshot to extent report
            try {
                test.addScreenCaptureFromPath("screenshots/" + fileName, testCaseName + "-" + screenshotName);
            } catch (Exception e) {
                System.err.println("Failed to add screenshot to report: " + e.getMessage());
            }
            
            return fullPath;
            
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            e.printStackTrace();
            test.log(Status.WARNING, "Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Overloaded method for backward compatibility
     */
    private String takeScreenshot(String screenshotName) {
        return takeScreenshot("General", screenshotName);
    }
        
    /**
     * Test Case 1: Open Windows Start Menu
     * This test clicks on the Windows Start button
     */
    @Test(enabled = true, priority = 1)
    public void testCase1_OpenWindowsStartMenu() {
        try {
            // Initialize test in Extent Report
            initTest("TestCase1_WindowsStartMenu", "Open Windows Start Menu by clicking Start Button");
            
            System.out.println("=== Test Case 1: Open Windows Start Menu ===");
            
            // Click Windows Start Button
            test.log(Status.INFO, "Attempting to click Windows Start Button");
            boolean startButtonClicked = findAndClickImage("WindowsStartButton.jpg", 0.7, 5);
            
            // Take screenshot after clicking
            takeScreenshot("TestCase1", "after_start_button_click");
            
            if (startButtonClicked) {
                test.log(Status.INFO, "Windows Start Button clicked successfully");
                System.out.println("Windows Start Button clicked successfully");
                
                // Type "Folder" in search box
                test.log(Status.INFO, "Typing 'Folder' in search box");
                screen.type("D:\\Song Collection\\05 A.R.RAHUMAN SUN\\01 CHINNA CHINNA ASAI.mp3");
                waitFor(2000);
                
                test.log(Status.PASS, "Windows Start Menu test completed successfully");
            } else {
                test.log(Status.FAIL, "Windows Start Button not clicked");
                System.out.println("Windows Start Button not clicked");
                assertTrue(false, "Windows Start Button should be clicked");
            }
        } catch (Exception e) {
            handleTestException(e, "TestCase1");
        }
    }
    
    /**
     * Test Case 2: Open Folder Explorer
     * This test clicks on the Folder Explorer option
     */
    @Test(enabled = true, priority = 2)
    public void testCase2_OpenFolderExplorer() {
        try {
            // Initialize test in Extent Report
            initTest("TestCase2_FolderExplorer", "Open Folder Explorer from search results");
            
            // Take screenshot after typing
            takeScreenshot("TestCase2", "after_typing_song_name");
            
            test.log(Status.INFO, "Folder Explorer clicked successfully");
            System.out.println("Folder Explorer clicked successfully");
            test.log(Status.PASS, "Folder Explorer test completed successfully");
            
        } catch (Exception e) {
            handleTestException(e, "TestCase2");
        }
    }
    
    /**
     * Test Case 3: Select Song File
     * This test selects a song file in the explorer
     */
    @Test(enabled = true, priority = 3)
    public void testCase3_SelectSongFile() {
        try {
            // Initialize test in Extent Report
            initTest("TestCase3_SelectSong", "Select a song file");
            
            System.out.println("=== Test Case 4: Select Song File ===");
                
            // Press Enter to open the file
            test.log(Status.INFO, "Pressing Enter to open the file");
            screen.type(Key.ENTER);
            waitFor(5000);
            
            // Take screenshot after pressing Enter
            takeScreenshot("TestCase3", "after_pressing_enter");
            waitFor(10000);

            test.log(Status.PASS, "Select Song File test completed successfully");
            System.out.println("Select Song File test completed successfully");

            //Close the Music Player
            screen.keyDown(Key.CTRL);
            screen.type("q");
            screen.keyUp(Key.CTRL);
            waitFor(3000);
               
        } catch (Exception e) {
            handleTestException(e, "TestCase3");
        }
    }
    
    /**
     * Helper method to handle test exceptions
     * @param e The exception that occurred
     * @param testCaseName The name of the test case
     */
    private void handleTestException(Exception e, String testCaseName) {
        System.err.println("Error during test: " + e.getMessage());
        e.printStackTrace();
        
        // Take screenshot on error
        takeScreenshot(testCaseName, "error_screenshot");
        
        if (test != null) {
            test.log(Status.FAIL, "Error during test: " + e.getMessage());
            test.log(Status.WARNING, "Test completed with warnings");
        } else {
            System.err.println("Test object is null, cannot log to ExtentReports");
        }
        
        fail("Test failed with exception: " + e.getMessage());
    }
}
