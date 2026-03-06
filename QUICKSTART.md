# Quick Start Guide

## Installation Complete! ✅

Your Excel Test Report Generator library has been successfully built and installed!

### Files Created:
- **JAR File**: `target/excel-test-report-generator-1.0.0.jar`
- **Sources JAR**: `target/excel-test-report-generator-1.0.0-sources.jar`
- **Javadoc JAR**: `target/excel-test-report-generator-1.0.0-javadoc.jar`

The library has been installed to your local Maven repository at:
```
~/.m2/repository/com/testgenerator/testing/excel-test-report-generator/1.0.0/
```

---

## How to Use in Your Projects

### Option 1: Use with Maven (Recommended)

Add this dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>com.testgenerator.testing</groupId>
    <artifactId>excel-test-report-generator</artifactId>
    <version>1.0.0</version>
</dependency>
```

Then use it in your code:

```java
import com.testgenerator.testing.report.*;
import com.testgenerator.testing.report.model.*;

public class MyTest {
    public static void main(String[] args) throws Exception {
        // Create test suite
        TestSuite suite = TestSuite.builder()
            .suiteName("My Tests")
            .projectName("My Project")
            .build();

        // Add test cases
        suite.getTestCases().add(
            TestCase.builder()
                .testName("Test 1")
                .status(TestStatus.PASS)
                .durationMs(1500)
                .build()
        );

        // Generate report
        ExcelTestReportGenerator.builder()
            .testSuite(suite)
            .outputFile("my-report.xlsx")
            .generate();

        System.out.println("Report generated!");
    }
}
```

### Option 2: Copy JAR to Your Project

Copy the JAR file to your project:
```bash
cp target/excel-test-report-generator-1.0.0.jar /path/to/your/project/lib/
```

Add it to your classpath when compiling/running:
```bash
javac -cp "lib/*" YourTest.java
java -cp "lib/*:." YourTest
```

---

## Run the Example

Test the library with the provided example:

```bash
# From the library directory
cd C:\Users\Sarvess\Documents\TestReportGenerator-Library

# Compile the example (copy it to src/test/java first)
mvn test-compile

# Or manually:
javac -cp "target/excel-test-report-generator-1.0.0.jar" examples/SimpleExample.java
java -cp "target/excel-test-report-generator-1.0.0.jar;examples" SimpleExample
```

This will generate `My_Test_Report.xlsx` in the current directory.

---

## Integrate with Your Existing Project

### For the BursaCr2 Project:

1. Open `A3_Archived_Scheduler/pom.xml`

2. Add the dependency:
```xml
<dependency>
    <groupId>com.testgenerator.testing</groupId>
    <artifactId>excel-test-report-generator</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

3. Update your test class to use the library:
```java
@AfterAll
static void generateReport() throws IOException {
    // Collect test results
    TestSuite suite = TestSuite.builder()
        .suiteName("ConsolidateStatement Tests")
        .projectName("BursaCr2 Scheduler")
        .build();

    // Add your test cases here...

    // Generate report
    ExcelTestReportGenerator.builder()
        .testSuite(suite)
        .outputFile("test-report.xlsx")
        .generate();
}
```

---

## Command-Line Usage (Quick Test)

Create a simple test and generate a report:

```bash
cd C:\Users\Sarvess\Documents\TestReportGenerator-Library

# Run the generate-sample-report script
./generate-sample-report.sh   # Linux/Mac
generate-sample-report.bat     # Windows
```

This will create a sample report named `Sample_Test_Report_[timestamp].xlsx`.

---

## API Summary

### Create Test Suite
```java
TestSuite suite = TestSuite.builder()
    .suiteName("Suite Name")
    .projectName("Project Name")
    .tester("Your Name")
    .environment("UAT/PROD/DEV")
    .build();
```

### Add Test Cases
```java
TestCase test = TestCase.builder()
    .testId("TC001")
    .testName("Test Name")
    .category("Category")
    .status(TestStatus.PASS)  // or FAIL, ERROR, SKIP, PENDING
    .expectedResult("Expected")
    .actualResult("Actual")
    .durationMs(1500)
    .build();

suite.getTestCases().add(test);
```

### Generate Report
```java
String reportPath = ExcelTestReportGenerator.builder()
    .testSuite(suite)
    .outputFile("report.xlsx")
    .generate();

System.out.println("Report: " + reportPath);
```

---

## Get Statistics

```java
int total = suite.getTotalTests();
int passed = suite.getPassedTests();
int failed = suite.getFailedTests();
double passRate = suite.getPassRate();
String duration = suite.getTotalDurationFormatted();

System.out.println("Total: " + total);
System.out.println("Passed: " + passed);
System.out.println("Failed: " + failed);
System.out.println("Pass Rate: " + passRate + "%");
System.out.println("Duration: " + duration);
```

---

## Next Steps

1. ✅ Library is built and installed
2. 📖 Read the full [README.md](README.md) for detailed documentation
3. 💡 Check [examples/](examples/) directory for more usage patterns
4. 🚀 Integrate into your test projects
5. 📊 Generate beautiful test reports!

---

## Troubleshooting

### Issue: "Package com.testgenerator.testing.report does not exist"

**Solution**: Make sure you've added the dependency to your pom.xml and run:
```bash
mvn clean install
```

### Issue: "NoClassDefFoundError: org/apache/poi/..."

**Solution**: Apache POI dependency missing. Make sure your pom.xml includes:
```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.4.1</version>
</dependency>
```

### Issue: Report generation fails

**Solution**: Check that:
- Output directory exists and is writable
- No other program has the Excel file open
- You have at least one test case in the suite

---

## Support

For questions or issues:
- Email: sarvess@slinfo.com
- Create an issue in the project repository

---

**Happy Testing! 🎉**
