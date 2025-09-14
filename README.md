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
│                   └── PlaySongWithAutomation.java
├── test-output/
│   ├── ExtentReport.html
│   └── screenshots/
│       ├── TestCase1-after_start_button_click_*.png
│       └── TestCase3-after_pressing_enter_*.png
├── pom.xml
├── testng.xml
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

To run all test cases without specifying individual methods, modify the XML to:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="WAMP Automation Suite">
  <test name="Desktop Automation Tests">
    <classes>
      <class name="com.automation.PlaySongWithAutomation" />
    </classes>
  </test>
</suite>
```

### TestcaseToRun.config

The `TestcaseToRun.config` file contains a simple configuration value:

```
ALL
```

This indicates that all test cases should be run.

## Test Execution

To run the tests, use Maven:

```bash
mvn clean test
```

This will:
1. Clean the project
2. Compile the code
3. Run the tests specified in the TestNG XML file
4. Generate test reports

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

---

This project demonstrates how to automate desktop applications using image recognition, which is particularly useful for applications that don't have accessible UI elements or APIs.
