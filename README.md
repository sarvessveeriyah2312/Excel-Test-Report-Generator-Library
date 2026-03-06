# Excel Test Report Generator

A professional Java library for generating beautiful, color-coded Excel test reports from automated or manual test results. Works with JUnit 5, TestNG, or any custom test framework.

---

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [API Reference](#api-reference)
- [Advanced Usage](#advanced-usage)
- [Report Structure](#report-structure)
- [Project Structure](#project-structure)
- [Building the Library](#building-the-library)
- [Changelog](#changelog)
- [Future Enhancements](#future-enhancements)

---

## Features

- **Professional Excel Reports** — Color-coded summary and detailed test case sheets
- **Framework Agnostic** — Works with JUnit 4/5, TestNG, or any custom test framework
- **Fluent Builder API** — Clean, readable code with builder pattern throughout
- **Flexible Output** — Choose which sheets to include, set custom output paths
- **Rich Test Data** — Track test ID, category, duration, expected/actual results, error messages, stack traces
- **Custom Fields** — Attach any key-value data to test cases and suites
- **Auto Statistics** — Pass rate, counts by status, total duration, and category grouping calculated automatically

---

## Requirements

- Java 11 or higher
- Apache POI 5.4.1 (bundled — no extra setup needed)

---

## Installation

### Option 1: Maven (Recommended)

Install the library to your local Maven repository:

```bash
cd Excel-Test-Report-Generator-Library
mvn clean install
```

Then add the dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>com.testgenerator.testing</groupId>
    <artifactId>excel-test-report-generator</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Option 2: JAR File

Copy the built JAR directly into your project:

```bash
mvn clean package
cp target/excel-test-report-generator-1.0.0.jar /your/project/lib/
```

Compile and run with it on the classpath:

```bash
javac -cp "lib/*" YourTest.java
java -cp "lib/*:." YourTest
```

---

## Quick Start

```java
import com.testgenerator.testing.report.ExcelTestReportGenerator;
import com.testgenerator.testing.report.model.*;

// 1. Create a test suite
TestSuite suite = TestSuite.builder()
    .suiteName("Login Module Tests")
    .projectName("My Application")
    .tester("Jane Doe")
    .environment("UAT")
    .build();

// 2. Add test cases
suite.getTestCases().add(
    TestCase.builder()
        .testId("TC001")
        .testName("Valid user login")
        .category("Authentication")
        .status(TestStatus.PASS)
        .expectedResult("Dashboard is shown")
        .actualResult("Dashboard loaded successfully")
        .durationMs(1500)
        .build()
);

suite.getTestCases().add(
    TestCase.builder()
        .testId("TC002")
        .testName("Login with wrong password")
        .category("Authentication")
        .status(TestStatus.FAIL)
        .expectedResult("Error message displayed")
        .actualResult("Application crashed with NullPointerException")
        .errorMessage("NullPointerException at AuthService.java:42")
        .durationMs(2300)
        .build()
);

// 3. Generate the report
String path = ExcelTestReportGenerator.builder()
    .testSuite(suite)
    .outputFile("login-test-report.xlsx")
    .generate();

System.out.println("Report saved to: " + path);
```

---

## API Reference

### `TestSuite`

Represents a collection of test cases and suite-level metadata.

```java
TestSuite suite = TestSuite.builder()
    .suiteName("Suite Name")          // Defaults to "Test Suite" if not set
    .projectName("Project Name")      // Optional
    .description("Suite description") // Optional
    .tester("Tester Name")            // Optional
    .environment("UAT / PROD / DEV")  // Optional
    .executionDate(LocalDateTime.now())// Optional — defaults to now
    .addTestCase(testCase)            // Add a single TestCase
    .addTestCases(listOfTestCases)    // Add multiple TestCases
    .addMetadata("key", "value")      // Add custom suite-level metadata
    .build();
```

**Statistics methods:**

| Method | Returns | Description |
|---|---|---|
| `getTotalTests()` | `int` | Total number of test cases |
| `getPassedTests()` | `int` | Count of PASS results |
| `getFailedTests()` | `int` | Count of FAIL results |
| `getErrorTests()` | `int` | Count of ERROR results |
| `getSkippedTests()` | `int` | Count of SKIP results |
| `getPassRate()` | `double` | Percentage of passed tests |
| `getTotalDurationMs()` | `long` | Sum of all test durations in ms |
| `getTotalDurationFormatted()` | `String` | Human-readable duration (e.g. `"2m 30s"`) |
| `getTestsByCategory()` | `Map<String, Long>` | Test count grouped by category |

---

### `TestCase`

Represents a single test case with its result and metadata.

```java
TestCase testCase = TestCase.builder()
    .testId("TC001")                       // Optional: unique identifier
    .testName("Test name")                 // Required
    .description("What this test checks")  // Optional
    .category("API / UI / Database")       // Optional
    .status(TestStatus.PASS)               // Optional — defaults to PENDING
    .expectedResult("Expected behaviour")  // Optional
    .actualResult("What actually happened")// Optional
    .errorMessage("Error details")         // Optional
    .stackTrace("Full stack trace")        // Optional
    .durationMs(1500)                      // Optional: duration in milliseconds
    .addCustomField("Key", "Value")        // Optional: any extra data
    .build();
```

**Helper method:**

| Method | Example Output |
|---|---|
| `getDurationFormatted()` | `"500ms"`, `"1.50s"`, `"1m 30s"` |

---

### `TestStatus`

```java
TestStatus.PASS     // Test passed
TestStatus.FAIL     // Test failed (assertion failure)
TestStatus.ERROR    // Test errored (unexpected exception)
TestStatus.SKIP     // Test was skipped
TestStatus.PENDING  // Test not yet executed
```

---

### `ExcelTestReportGenerator`

```java
String reportPath = ExcelTestReportGenerator.builder()
    .testSuite(suite)              // Required
    .outputFile("report.xlsx")     // Optional — defaults to "test-report.xlsx"
    .includeSummary(true)          // Optional — defaults to true
    .includeDetailedTests(true)    // Optional — defaults to true
    .generate();                   // Builds and writes the file, returns the path
```

- Throws `IllegalStateException` if `testSuite` is not set
- Throws `IOException` if the file cannot be written

---

## Advanced Usage

### JUnit 5 Integration

Implement `TestExecutionListener` to collect results automatically:

```java
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.engine.TestExecutionResult;
import com.testgenerator.testing.report.ExcelTestReportGenerator;
import com.testgenerator.testing.report.model.*;

public class ExcelReportListener implements TestExecutionListener {

    private final TestSuite suite = TestSuite.builder()
        .suiteName("JUnit Test Execution")
        .projectName("My Application")
        .build();

    private final Map<TestIdentifier, Long> startTimes = new HashMap<>();

    @Override
    public void executionStarted(TestIdentifier id) {
        if (id.isTest()) startTimes.put(id, System.currentTimeMillis());
    }

    @Override
    public void executionFinished(TestIdentifier id, TestExecutionResult result) {
        if (!id.isTest()) return;

        long duration = System.currentTimeMillis() - startTimes.get(id);
        TestStatus status = result.getStatus() == TestExecutionResult.Status.SUCCESSFUL
            ? TestStatus.PASS : TestStatus.FAIL;

        TestCase.Builder builder = TestCase.builder()
            .testName(id.getDisplayName())
            .status(status)
            .durationMs(duration);

        result.getThrowable().ifPresent(ex -> {
            builder.errorMessage(ex.getMessage());
            builder.stackTrace(stackTraceToString(ex));
        });

        suite.getTestCases().add(builder.build());
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        try {
            ExcelTestReportGenerator.builder()
                .testSuite(suite)
                .outputFile("junit-report.xlsx")
                .generate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

Register it in `src/test/resources/META-INF/services/org.junit.platform.launcher.TestExecutionListener`:
```
com.yourpackage.ExcelReportListener
```

---

### Custom Fields and Metadata

Attach any additional data to test cases or the suite:

```java
TestCase testCase = TestCase.builder()
    .testName("Batch Query Performance")
    .status(TestStatus.PASS)
    .addCustomField("Database", "SQL Server 2019")
    .addCustomField("Total Parameters", "62,285")
    .addCustomField("Batches Created", "4,368")
    .addCustomField("Max Batch Size", "1,600 params")
    .build();

TestSuite suite = TestSuite.builder()
    .suiteName("Performance Tests")
    .addMetadata("Server", "UAT-SERVER-01")
    .addMetadata("DB Version", "15.0.4312")
    .addTestCase(testCase)
    .build();
```

---

### Selective Sheet Generation

```java
// Summary sheet only
ExcelTestReportGenerator.builder()
    .testSuite(suite)
    .outputFile("summary-only.xlsx")
    .includeSummary(true)
    .includeDetailedTests(false)
    .generate();

// Test cases sheet only
ExcelTestReportGenerator.builder()
    .testSuite(suite)
    .outputFile("cases-only.xlsx")
    .includeSummary(false)
    .includeDetailedTests(true)
    .generate();
```

---

### Category Grouping

```java
TestSuite suite = TestSuite.builder()
    .suiteName("Integration Tests")
    .addTestCase(TestCase.builder().testName("API health check").category("API").status(TestStatus.PASS).build())
    .addTestCase(TestCase.builder().testName("DB connection").category("Database").status(TestStatus.PASS).build())
    .addTestCase(TestCase.builder().testName("Login UI").category("UI").status(TestStatus.FAIL).build())
    .build();

Map<String, Long> byCategory = suite.getTestsByCategory();
// {"API": 1, "Database": 1, "UI": 1}
```

---

## Report Structure

The generated `.xlsx` file contains up to two sheets:

### Sheet 1 — Test Summary
- Project name, suite name, tester, environment, execution date
- Test statistics table: total, passed, failed, errors, skipped
- Pass rate with color indicator (green ≥ 90%, neutral 70–89%, red < 70%)
- Total execution duration

### Sheet 2 — Test Cases
| Column | Description |
|---|---|
| Test ID | Unique identifier |
| Test Name | Name of the test |
| Category | Grouping label |
| Status | PASS / FAIL / ERROR / SKIP / PENDING with color |
| Duration | Formatted execution time |
| Expected | Expected result |
| Actual | Actual result |

**Status colors:**
- PASS — Green
- FAIL / ERROR — Red/Orange
- SKIP / PENDING — Neutral

---

## Project Structure

```
Excel-Test-Report-Generator-Library/
├── pom.xml
├── README.md
├── QUICKSTART.md
├── LIBRARY_SUMMARY.md
├── examples/
│   └── SimpleExample.java
└── src/
    └── main/
        └── java/
            └── com/testgenerator/testing/report/
                ├── ExcelTestReportGenerator.java
                └── model/
                    ├── TestSuite.java
                    ├── TestCase.java
                    └── TestStatus.java
```

---

## Building the Library

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Package (creates JAR in target/)
mvn clean package

# Install to local Maven repository
mvn clean install

# Generate sources + javadoc JARs
mvn clean package source:jar javadoc:jar
```

---

## Changelog

### v1.0.0 — 2026-03-06
- Initial release
- Summary and detailed test case sheets
- Support for JUnit 4/5, TestNG, and custom frameworks
- Custom fields and suite metadata
- Professional Excel styling with color coding
- Auto-calculated statistics and pass rate

---

## Future Enhancements

- Charts (pie chart for pass/fail, bar chart by category)
- PDF export
- HTML report generation
- TestNG listener integration
- Screenshot attachments
- Test trend tracking across runs
- Email report delivery

---

**Made with care for better test reporting.**
