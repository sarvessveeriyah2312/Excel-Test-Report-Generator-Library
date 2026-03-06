import com.testgenerator.testing.report.ExcelTestReportGenerator;
import com.testgenerator.testing.report.model.TestCase;
import com.testgenerator.testing.report.model.TestStatus;
import com.testgenerator.testing.report.model.TestSuite;

/**
 * Simple example demonstrating how to use the Excel Test Report Generator library.
 */
public class SimpleExample {

    public static void main(String[] args) {
        try {
            // Create a test suite
            TestSuite suite = TestSuite.builder()
                    .suiteName("API Integration Tests")
                    .projectName("E-Invoice System")
                    .description("Testing REST API endpoints")
                    .tester("John Doe")
                    .environment("UAT")
                    .build();

            // Add test cases
            suite.getTestCases().add(
                    TestCase.builder()
                            .testId("TC001")
                            .testName("Test User Login")
                            .category("Authentication")
                            .status(TestStatus.PASS)
                            .expectedResult("User logs in successfully")
                            .actualResult("User logged in with valid credentials")
                            .durationMs(1250)
                            .build()
            );

            suite.getTestCases().add(
                    TestCase.builder()
                            .testId("TC002")
                            .testName("Test Invalid Credentials")
                            .category("Authentication")
                            .status(TestStatus.PASS)
                            .expectedResult("Login fails with error message")
                            .actualResult("Error: Invalid username or password")
                            .durationMs(890)
                            .build()
            );

            suite.getTestCases().add(
                    TestCase.builder()
                            .testId("TC003")
                            .testName("Test Create Invoice")
                            .category("Invoice Management")
                            .status(TestStatus.FAIL)
                            .expectedResult("Invoice created with status 200")
                            .actualResult("Server returned 500 Internal Server Error")
                            .errorMessage("Database connection timeout")
                            .durationMs(5200)
                            .build()
            );

            suite.getTestCases().add(
                    TestCase.builder()
                            .testId("TC004")
                            .testName("Test SQL Parameter Batching")
                            .category("Performance")
                            .status(TestStatus.PASS)
                            .expectedResult("Query executes without SQL Server 2100 param error")
                            .actualResult("Successfully batched into 4,368 queries, all < 2100 params")
                            .durationMs(7200)
                            .addCustomField("Batch Size", "1,600 params avg")
                            .addCustomField("Total Records", "51,324")
                            .build()
            );

            // Generate the report
            String reportPath = ExcelTestReportGenerator.builder()
                    .testSuite(suite)
                    .outputFile("My_Test_Report.xlsx")
                    .includeSummary(true)
                    .includeDetailedTests(true)
                    .generate();

            System.out.println("✓ Report generated successfully: " + reportPath);
            System.out.println("✓ Total tests: " + suite.getTotalTests());
            System.out.println("✓ Pass rate: " + String.format("%.2f%%", suite.getPassRate()));

        } catch (Exception e) {
            System.err.println("Error generating report: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
