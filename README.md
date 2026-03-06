# Excel Test Report Generator Library

A professional Java library for generating beautiful Excel test reports from your automated tests (JUnit, TestNG, or custom test frameworks).

## Features

- ✅ **Beautiful Excel Reports** - Professional, color-coded reports with charts and statistics
- ✅ **Framework Agnostic** - Works with JUnit, TestNG, or any custom test framework
- ✅ **Easy to Use** - Simple fluent API with builder pattern
- ✅ **Customizable** - Add custom fields, metadata, and styling
- ✅ **Comprehensive** - Summary sheets, detailed test cases, pass/fail statistics
- ✅ **Zero Dependencies** - Only requires Apache POI (included)

## Installation

### Option 1: Install to Local Maven Repository

```bash
cd TestReportGenerator-Library
mvn clean install
```

Then add to your project's `pom.xml`:

```xml
<dependency>
    <groupId>com.slinfo.testing</groupId>
    <artifactId>excel-test-report-generator</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Option 2: Copy JAR to Your Project

```bash
mvn clean package
# Copy target/excel-test-report-generator-1.0.0.jar to your project's lib folder
```

## Quick Start

### Basic Usage

```java
import com.slinfo.testing.report.ExcelTestReportGenerator;
import com.slinfo.testing.report.model.*;

// Create a test suite
TestSuite suite = TestSuite.builder()
    .suiteName("My Test Suite")
    .projectName("My Project")
    .tester("Your Name")
    .environment("UAT")
    .build();

// Add test cases
suite.getTestCases().add(
    TestCase.builder()
        .testId("TC001")
        .testName("Test Login Functionality")
        .status(TestStatus.PASS)
        .expectedResult("User logs in successfully")
        .actualResult("Login successful")
        .durationMs(1500)
        .build()
);

suite.getTestCases().add(
    TestCase.builder()
        .testId("TC002")
        .testName("Test Invalid Password")
        .status(TestStatus.FAIL)
        .expectedResult("Error message displayed")
        .actualResult("Application crashed")
        .errorMessage("NullPointerException at line 42")
        .durationMs(2300)
        .build()
);

// Generate report
String reportPath = ExcelTestReportGenerator.builder()
    .testSuite(suite)
    .outputFile("test-report.xlsx")
    .generate();

System.out.println("Report generated: " + reportPath);
```

## Advanced Usage

### Using with JUnit 5

```java
import org.junit.platform.launcher.*;
import org.junit.platform.engine.TestExecutionResult;
import com.slinfo.testing.report.model.*;

public class JUnitReportGenerator implements TestExecutionListener {

    private TestSuite testSuite;
    private Map<TestIdentifier, Long> startTimes = new HashMap<>();

    public JUnitReportGenerator() {
        testSuite = TestSuite.builder()
            .suiteName("JUnit Test Execution")
            .projectName("My Application")
            .build();
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (testIdentifier.isTest()) {
            startTimes.put(testIdentifier, System.currentTimeMillis());
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier,
                                 TestExecutionResult result) {
        if (!testIdentifier.isTest()) return;

        long duration = System.currentTimeMillis() -
                       startTimes.get(testIdentifier);

        TestStatus status = result.getStatus() == TestExecutionResult.Status.SUCCESSFUL
            ? TestStatus.PASS
            : TestStatus.FAIL;

        TestCase testCase = TestCase.builder()
            .testName(testIdentifier.getDisplayName())
            .status(status)
            .durationMs(duration)
            .build();

        if (result.getThrowable().isPresent()) {
            Throwable ex = result.getThrowable().get();
            testCase.setErrorMessage(ex.getMessage());
            testCase.setStackTrace(getStackTrace(ex));
        }

        testSuite.getTestCases().add(testCase);
    }

    public void generateReport(String filename) throws IOException {
        ExcelTestReportGenerator.builder()
            .testSuite(testSuite)
            .outputFile(filename)
            .generate();
    }
}
```

### Custom Fields and Metadata

```java
TestCase testCase = TestCase.builder()
    .testName("SQL Batching Test")
    .status(TestStatus.PASS)
    .addCustomField("Database", "SQL Server 2019")
    .addCustomField("Total Parameters", "62,285")
    .addCustomField("Batches Created", "4,368")
    .addCustomField("Max Batch Size", "1,600 params")
    .build();

TestSuite suite = TestSuite.builder()
    .suiteName("Performance Tests")
    .addMetadata("Server", "UAT-SERVER-01")
    .addMetadata("Database Version", "15.0.4312")
    .addMetadata("Test Framework", "JUnit 5.10")
    .addTestCase(testCase)
    .build();
```

### Categories and Grouping

```java
// Create tests with categories
TestCase apiTest = TestCase.builder()
    .testName("Test API Endpoint")
    .category("API Tests")
    .status(TestStatus.PASS)
    .build();

TestCase dbTest = TestCase.builder()
    .testName("Test Database Connection")
    .category("Database Tests")
    .status(TestStatus.PASS)
    .build();

TestCase uiTest = TestCase.builder()
    .testName("Test Login UI")
    .category("UI Tests")
    .status(TestStatus.FAIL)
    .build();

TestSuite suite = TestSuite.builder()
    .suiteName("Integration Tests")
    .addTestCase(apiTest)
    .addTestCase(dbTest)
    .addTestCase(uiTest)
    .build();

// Get statistics by category
Map<String, Long> byCategory = suite.getTestsByCategory();
// Returns: {"API Tests": 1, "Database Tests": 1, "UI Tests": 1}
```

## Report Structure

The generated Excel file contains multiple sheets:

### 1. Test Summary Sheet
- Project information (name, tester, environment, date)
- Overall statistics (total, passed, failed, errors, skipped)
- Pass rate percentage
- Total execution duration
- Color-coded status indicators

### 2. Test Cases Sheet
- Detailed list of all test cases
- Test ID, Name, Category, Status, Duration
- Expected vs Actual results
- Error messages and stack traces (if applicable)
- Custom fields

## API Reference

### TestSuite

```java
TestSuite suite = TestSuite.builder()
    .suiteName(String)           // Required: Name of the test suite
    .projectName(String)         // Optional: Project name
    .description(String)         // Optional: Suite description
    .tester(String)              // Optional: Tester name
    .environment(String)         // Optional: Test environment (UAT, PROD, etc.)
    .executionDate(LocalDateTime)// Optional: Execution date (defaults to now)
    .addTestCase(TestCase)       // Add individual test case
    .addTestCases(List<TestCase>)// Add multiple test cases
    .addMetadata(String, String) // Add custom metadata
    .build();

// Statistics methods
int getTotalTests();
int getPassedTests();
int getFailedTests();
int getErrorTests();
int getSkippedTests();
double getPassRate();
long getTotalDurationMs();
String getTotalDurationFormatted();
Map<String, Long> getTestsByCategory();
```

### TestCase

```java
TestCase testCase = TestCase.builder()
    .testId(String)              // Optional: Unique test ID
    .testName(String)            // Required: Test name
    .description(String)         // Optional: Test description
    .category(String)            // Optional: Test category
    .status(TestStatus)          // Optional: PASS, FAIL, ERROR, SKIP, PENDING
    .expectedResult(String)      // Optional: Expected result
    .actualResult(String)        // Optional: Actual result
    .errorMessage(String)        // Optional: Error message
    .stackTrace(String)          // Optional: Stack trace
    .durationMs(long)            // Optional: Duration in milliseconds
    .addCustomField(String, String) // Add custom field
    .build();

// Helper methods
String getDurationFormatted();   // Returns formatted duration (e.g., "1.5s", "2m 30s")
```

### ExcelTestReportGenerator

```java
String reportPath = ExcelTestReportGenerator.builder()
    .testSuite(TestSuite)        // Required: Test suite to report
    .outputFile(String)          // Optional: Output filename (default: "test-report.xlsx")
    .includeSummary(boolean)     // Optional: Include summary sheet (default: true)
    .includeDetailedTests(boolean) // Optional: Include detailed tests (default: true)
    .includeCharts(boolean)      // Optional: Include charts (default: false, future feature)
    .generate();                 // Generate and return file path
```

## Test Status Values

```java
TestStatus.PASS    // Test passed
TestStatus.FAIL    // Test failed (assertion error)
TestStatus.ERROR   // Test error (exception)
TestStatus.SKIP    // Test skipped
TestStatus.PENDING // Test pending execution
```

## Examples

See the `examples/` directory for more usage examples:
- `SimpleExample.java` - Basic usage
- `JUnitIntegrationExample.java` - JUnit 5 integration
- `CustomFieldsExample.java` - Using custom fields and metadata
- `BulkTestExample.java` - Generating reports for large test suites

## Building the Library

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Package (creates JAR)
mvn clean package

# Install to local Maven repository
mvn clean install

# Generate sources and javadoc JARs
mvn clean package source:jar javadoc:jar
```

## Requirements

- Java 11 or higher
- Apache POI 5.4.1 (included)
- No other dependencies required

## License

This library is free to use for personal and commercial projects.

## Contributing

Feel free to submit issues, feature requests, or pull requests!

## Support

For questions or issues, please create an issue in the repository or contact: sarvess@slinfo.com

## Changelog

### Version 1.0.0 (2026-03-06)
- Initial release
- Summary and detailed test sheets
- Support for JUnit, TestNG, and custom frameworks
- Customizable fields and metadata
- Professional Excel styling
- Statistics and pass rate calculations

## Future Enhancements

- Charts and graphs (pie charts, bar charts)
- PDF export
- HTML report generation
- TestNG listener integration
- Screenshot attachments
- Test execution trends over time
- Email report delivery

---

**Made with ❤️ for better test reporting**
#   E x c e l - T e s t - R e p o r t - G e n e r a t o r - L i b r a r y  
 