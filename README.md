# WAMP Automation Project

This project demonstrates desktop automation using Selenium WebDriver and SikuliX to play songs on Windows. The automation process uses image recognition to interact with the Windows interface, search for a song file, and play it.

## Table of Contents

- [Project Overview](#project-overview)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Dependencies](#dependencies)
- [Test Cases](#test-cases)
- [Configuration](#configuration)
- [Test Execution](#test-execution)
- [Test Reports](#test-reports)
- [Screenshots](#screenshots)
- [How It Works](#how-it-works)
- [Dynamic Test Execution](#dynamic-test-execution)

## Project Overview

This automation project demonstrates how to:

1. Open the Windows Start Menu
2. Type a song path in the search box
3. Press Enter to play the song
4. Capture screenshots at each step
5. Generate detailed test reports

The project uses a combination of Selenium WebDriver for browser automation and SikuliX for desktop automation through image recognition.

## Prerequisites

- Java 8 or higher
- Maven
- Windows operating system
- Chrome browser
- Image files in the `src/resources/` directory:
  - WindowsStartButton.jpg
  - FolderExplorer.jpg
  - SelectSong.jpg

## Project Structure

```
wampautomation/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── automation/
│   │               └── App.java
│   ├── resources/
│   │   ├── FolderExplorer.jpg
│   │   ├── SelectSong.jpg
│   │   └── WindowsStartButton.jpg
│   └── test/
│       └── java/
│           └── com/
│               └── automation/
│                   ├── PlaySongWithAutomation.java
│                   ├── TestNGXmlGenerator.java
│                   ├── TestNGExecutor.java
│                   └── DynamicTestNGListener.java
├── test-output/
│   ├── ExtentReport.html
│   └── screenshots/
│       ├── TestCase1-after_start_button_click_*.png
│       ├── TestCase2-after_typing_song_name_*.png
│       └── TestCase3-after_pressing_enter_*.png
├── pom.xml
├── testng.xml
├── dynamic-testng.xml
├── run-tests.bat
└── TestcaseToRun.config
```

## Dependencies

The project uses the following dependencies (defined in `pom.xml`):

```xml
<dependencies>
  <!-- JUnit for testing -->
  <dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
  </dependency>
  
  <!-- Selenium WebDriver -->
  <dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.15.0</version>
  </dependency>
  
  <!-- WebDriverManager for automatic driver management -->
  <dependency>
    <groupId>io.github.bonigarcia</groupId>
    <artifactId>webdrivermanager</artifactId>
    <version>5.6.2</version>
  </dependency>
  
  <!-- SikuliX for image-based automation -->
  <dependency>
    <groupId>com.sikulix</groupId>
    <artifactId>sikulixapi</artifactId>
    <version>2.0.5</version>
  </dependency>
  
  <!-- Extent Reports for test reporting -->
  <dependency>
    <groupId>com.aventstack</groupId>
    <artifactId>extentreports</artifactId>
    <version>5.0.9</version>
  </dependency>
  
  <!-- Apache Commons IO for file operations -->
  <dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
  </dependency>
  
  <!-- TestNG for testing -->
  <dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.8.0</version>
    <scope>test</scope>
  </dependency>
  
  <!-- Reflections for scanning test classes -->
  <dependency>
    <groupId>org.reflections</groupId>
    <artifactId>reflections</artifactId>
    <version>0.10.2</version>
    <scope>test</scope>
  </dependency>
</dependencies>
```

## Test Cases

The project includes three test cases with priority execution order:

1. **testCase1_OpenWindowsStartMenu** (priority=1): Opens Windows Start Menu by clicking the Start Button and types the song path.
2. **testCase2_OpenFolderExplorer** (priority=2): Takes a screenshot after typing the song name.
3. **testCase3_SelectSongFile** (priority=3): Presses Enter to play the song file and takes a screenshot after the song starts playing.

## Configuration

### TestNG XML Configuration

The `testng.xml` file configures which test cases to run:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="WAMP Automation Suite">
  <test name="Desktop Automation Tests">
    <classes>
      <class name="com.automation.PlaySongWithAutomation">
        <methods>
          <include name="testCase1_OpenWindowsStartMenu" />
          <include name="testCase2_OpenFolderExplorer" />
          <include name="testCase3_SelectSongFile" />
        </methods>
      </class>
    </classes>
  </test>
</suite>
```

### Dynamic TestNG XML Configuration

The `dynamic-testng.xml` file is automatically generated based on the `TestcaseToRun.config` file. This allows for flexible test execution without modifying the source code.

### TestcaseToRun.config

The `TestcaseToRun.config` file controls which test cases are executed. It supports two formats:

1. **Run all tests** - Simply add the keyword `ALL` to run all test methods in all classes:
   ```
   ALL
   ```

2. **Run specific tests** - List the fully qualified method names to run specific test methods:
   ```
   com.automation.PlaySongWithAutomation.testCase1_OpenWindowsStartMenu
   com.automation.PlaySongWithAutomation.testCase3_SelectSongFile
   ```

You can combine both approaches. If `ALL` is present anywhere in the file, all tests will run regardless of other entries.

## Test Execution

### Using the Batch File (Recommended)

The easiest way to run tests is using the provided batch file:

```bash
run-tests.bat
```

This batch file:
1. Generates the dynamic TestNG XML based on TestcaseToRun.config
2. Runs the tests using the generated XML file

### Using Maven Directly

Alternatively, you can use Maven commands:

```bash
# Generate the dynamic TestNG XML
mvn exec:java -Dexec.mainClass="com.automation.TestNGXmlGenerator" -Dexec.classpathScope=test

# Run tests with the generated XML
mvn test -DsuiteXmlFile=dynamic-testng.xml -DskipTests=false
```

This approach ensures that:
1. The dynamic TestNG XML is generated based on the current TestcaseToRun.config
2. Tests are executed only once using the generated XML

## Test Reports

The project generates detailed test reports using ExtentReports:

- **Location**: `test-output/ExtentReport.html`
- **Contents**:
  - Test case status (Pass/Fail)
  - Test steps with timestamps
  - Screenshots captured during test execution
  - System information
  - Browser details

## Screenshots

Screenshots are captured at key points during test execution and stored in the test-output directory:

- **Location**: `test-output/screenshots/`
- **Naming Convention**: `TestCaseX-description_timestamp.png`
- **Current Screenshots**:
  - TestCase1-after_start_button_click_20250914_180247.png (97KB)
  - TestCase2-after_typing_song_name_20250914_180256.png (307KB)
  - TestCase3-after_pressing_enter_20250914_180302.png (1.2MB)
- **Key Screenshots**:
  - TestCase1-after_start_button_click_*.png - After clicking the Windows Start button
  - TestCase2-after_typing_song_name_*.png - After typing the song name
  - TestCase3-after_pressing_enter_*.png - After pressing Enter to play the song

## How It Works

### 1. Setup Phase

The test begins by initializing:
- WebDriver for browser control
- SikuliX Screen for desktop automation
- ExtentReports for reporting

### 2. Test Case Execution with Priority

Test cases are executed in order based on their priority attribute:

```java
@Test(enabled = true, priority = 1)
public void testCase1_OpenWindowsStartMenu() {
    // Click Windows Start Button
    boolean startButtonClicked = findAndClickImage("WindowsStartButton.jpg", 0.7, 5);
    
    // Type song path in search box
    screen.type("D:\\Song Collection\\05 A.R.RAHUMAN SUN\\01 CHINNA CHINNA ASAI.mp3");
}
```

### 3. Playing the Song

```java
@Test(enabled = true, priority = 3)
public void testCase3_SelectSongFile() {
    // Press Enter to open the file
    screen.type(Key.ENTER);
    waitFor(5000);
    
    // Take screenshot after pressing Enter
    takeScreenshot("TestCase3", "after_pressing_enter");
}
```

### 4. Cleanup

After playing the song, the automation closes the music player:

```java
// Close the Music Player
screen.keyDown(Key.CTRL);
screen.type("q");
screen.keyUp(Key.CTRL);
```

### 5. Reporting and Screenshots

Throughout the process, the automation captures screenshots and logs information:

```java
// Take screenshot after clicking
takeScreenshot("TestCase1", "after_start_button_click");

// Log test progress
test.log(Status.INFO, "Windows Start Button clicked successfully");
```

## Dynamic Test Execution

The project includes a dynamic test execution system that reads the `TestcaseToRun.config` file to determine which tests to run:

### How Dynamic Test Execution Works

1. **TestNGXmlGenerator**: Reads the TestcaseToRun.config file and generates a dynamic TestNG XML configuration.
   - If the config contains "ALL", all test classes and methods are included
   - If specific methods are listed, only those methods are included

2. **DynamicTestNGListener**: A TestNG listener that applies the dynamic configuration during test execution.

3. **Execution Flow**:
   - The `run-tests.bat` script first calls TestNGXmlGenerator to create the dynamic-testng.xml file
   - Then it runs Maven with the generated XML file to execute the tests
   - This two-step approach prevents duplicate test execution

### Example Configuration

Current TestcaseToRun.config:
```
ALL
```

This configuration will run all test cases because the "ALL" keyword is present.

To run only specific test methods, remove the "ALL" line and list the methods you want to run:
```
com.automation.PlaySongWithAutomation.testCase1_OpenWindowsStartMenu
com.automation.PlaySongWithAutomation.testCase3_SelectSongFile
```

### Troubleshooting

If you encounter duplicate test executions when running with Maven directly, use the provided `run-tests.bat` script instead, which properly separates the XML generation and test execution phases.

---

This project demonstrates how to automate desktop applications using image recognition, which is particularly useful for applications that don't have accessible UI elements or APIs.