package com.slinfo.testing.report;

import com.slinfo.testing.report.model.TestCase;
import com.slinfo.testing.report.model.TestStatus;
import com.slinfo.testing.report.model.TestSuite;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Main class for generating professional Excel test reports.
 *
 * <p>Usage example:
 * <pre>
 * TestSuite suite = TestSuite.builder()
 *     .suiteName("My Test Suite")
 *     .projectName("My Project")
 *     .addTestCase(TestCase.builder()
 *         .testName("Test 1")
 *         .status(TestStatus.PASS)
 *         .build())
 *     .build();
 *
 * ExcelTestReportGenerator.builder()
 *     .testSuite(suite)
 *     .outputFile("report.xlsx")
 *     .generate();
 * </pre>
 */
public class ExcelTestReportGenerator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TestSuite testSuite;
    private final String outputFile;
    private final boolean includeSummary;
    private final boolean includeDetailedTests;
    private final boolean includeCharts;

    private XSSFWorkbook workbook;
    private Map<String, CellStyle> styles;

    private ExcelTestReportGenerator(Builder builder) {
        this.testSuite = builder.testSuite;
        this.outputFile = builder.outputFile;
        this.includeSummary = builder.includeSummary;
        this.includeDetailedTests = builder.includeDetailedTests;
        this.includeCharts = builder.includeCharts;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Generates the Excel report.
     *
     * @return The path to the generated file
     * @throws IOException if file writing fails
     */
    public String generate() throws IOException {
        workbook = new XSSFWorkbook();
        styles = createStyles();

        if (includeSummary) {
            createSummarySheet();
        }

        if (includeDetailedTests) {
            createDetailedTestsSheet();
        }

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            workbook.write(fos);
        }

        workbook.close();
        return outputFile;
    }

    private void createSummarySheet() {
        Sheet sheet = workbook.createSheet("Test Summary");
        int rowNum = 0;

        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Test Execution Report");
        titleCell.setCellStyle(styles.get("title"));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
        rowNum++;

        // Project info
        if (testSuite.getProjectName() != null) {
            createInfoRow(sheet, rowNum++, "Project:", testSuite.getProjectName());
        }
        if (testSuite.getSuiteName() != null) {
            createInfoRow(sheet, rowNum++, "Test Suite:", testSuite.getSuiteName());
        }
        if (testSuite.getDescription() != null) {
            createInfoRow(sheet, rowNum++, "Description:", testSuite.getDescription());
        }
        if (testSuite.getTester() != null) {
            createInfoRow(sheet, rowNum++, "Tester:", testSuite.getTester());
        }
        if (testSuite.getEnvironment() != null) {
            createInfoRow(sheet, rowNum++, "Environment:", testSuite.getEnvironment());
        }
        if (testSuite.getExecutionDate() != null) {
            createInfoRow(sheet, rowNum++, "Execution Date:",
                testSuite.getExecutionDate().format(DATE_FORMATTER));
        }
        createInfoRow(sheet, rowNum++, "Total Duration:", testSuite.getTotalDurationFormatted());
        rowNum++;

        // Test statistics
        createSectionHeader(sheet, rowNum++, "TEST STATISTICS");

        Row statsHeaderRow = sheet.createRow(rowNum++);
        String[] statsHeaders = {"Metric", "Count", "Percentage"};
        for (int i = 0; i < statsHeaders.length; i++) {
            Cell cell = statsHeaderRow.createCell(i);
            cell.setCellValue(statsHeaders[i]);
            cell.setCellStyle(styles.get("header"));
        }

        createStatRow(sheet, rowNum++, "Total Tests", testSuite.getTotalTests(), 100.0);
        createStatRow(sheet, rowNum++, "Passed", testSuite.getPassedTests(),
            testSuite.getTotalTests() > 0 ? (testSuite.getPassedTests() * 100.0 / testSuite.getTotalTests()) : 0);
        createStatRow(sheet, rowNum++, "Failed", testSuite.getFailedTests(),
            testSuite.getTotalTests() > 0 ? (testSuite.getFailedTests() * 100.0 / testSuite.getTotalTests()) : 0);
        createStatRow(sheet, rowNum++, "Errors", testSuite.getErrorTests(),
            testSuite.getTotalTests() > 0 ? (testSuite.getErrorTests() * 100.0 / testSuite.getTotalTests()) : 0);
        createStatRow(sheet, rowNum++, "Skipped", testSuite.getSkippedTests(),
            testSuite.getTotalTests() > 0 ? (testSuite.getSkippedTests() * 100.0 / testSuite.getTotalTests()) : 0);

        rowNum++;
        Row passRateRow = sheet.createRow(rowNum++);
        Cell passRateLabel = passRateRow.createCell(0);
        passRateLabel.setCellValue("Pass Rate:");
        passRateLabel.setCellStyle(styles.get("header"));

        Cell passRateValue = passRateRow.createCell(1);
        passRateValue.setCellValue(String.format("%.2f%%", testSuite.getPassRate()));
        if (testSuite.getPassRate() >= 90) {
            passRateValue.setCellStyle(styles.get("pass"));
        } else if (testSuite.getPassRate() >= 70) {
            passRateValue.setCellStyle(styles.get("normal"));
        } else {
            passRateValue.setCellStyle(styles.get("fail"));
        }

        // Auto-size columns
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createDetailedTestsSheet() {
        Sheet sheet = workbook.createSheet("Test Cases");
        int rowNum = 0;

        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Detailed Test Cases");
        titleCell.setCellStyle(styles.get("title"));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        rowNum++;

        // Headers
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Test ID", "Test Name", "Category", "Status", "Duration", "Expected", "Actual"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styles.get("header"));
        }

        // Test cases
        for (TestCase testCase : testSuite.getTestCases()) {
            Row row = sheet.createRow(rowNum++);

            Cell idCell = row.createCell(0);
            idCell.setCellValue(testCase.getTestId() != null ? testCase.getTestId() : "");
            idCell.setCellStyle(styles.get("normal"));

            Cell nameCell = row.createCell(1);
            nameCell.setCellValue(testCase.getTestName());
            nameCell.setCellStyle(styles.get("normal"));

            Cell categoryCell = row.createCell(2);
            categoryCell.setCellValue(testCase.getCategory() != null ? testCase.getCategory() : "");
            categoryCell.setCellStyle(styles.get("normal"));

            Cell statusCell = row.createCell(3);
            statusCell.setCellValue(testCase.getStatus().getCode());
            statusCell.setCellStyle(getStatusStyle(testCase.getStatus()));

            Cell durationCell = row.createCell(4);
            durationCell.setCellValue(testCase.getDurationFormatted());
            durationCell.setCellStyle(styles.get("number"));

            Cell expectedCell = row.createCell(5);
            expectedCell.setCellValue(testCase.getExpectedResult() != null ? testCase.getExpectedResult() : "");
            expectedCell.setCellStyle(styles.get("normal"));

            Cell actualCell = row.createCell(6);
            actualCell.setCellValue(testCase.getActualResult() != null ? testCase.getActualResult() : "");
            actualCell.setCellStyle(styles.get("normal"));

            row.setHeightInPoints(30);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            if (i == 1 || i == 5 || i == 6) {
                sheet.setColumnWidth(i, 8000);
            } else {
                sheet.autoSizeColumn(i);
            }
        }
    }

    private void createStatRow(Sheet sheet, int rowNum, String label, int count, double percentage) {
        Row row = sheet.createRow(rowNum);

        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(styles.get("normal"));

        Cell countCell = row.createCell(1);
        countCell.setCellValue(count);
        countCell.setCellStyle(styles.get("number"));

        Cell percentCell = row.createCell(2);
        percentCell.setCellValue(String.format("%.2f%%", percentage));
        percentCell.setCellStyle(styles.get("number"));
    }

    private void createSectionHeader(Sheet sheet, int rowNum, String title) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(styles.get("header"));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 5));
    }

    private void createInfoRow(Sheet sheet, int rowNum, String label, String value) {
        Row row = sheet.createRow(rowNum);

        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(styles.get("normal"));

        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
        valueCell.setCellStyle(styles.get("normal"));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 1, 5));
    }

    private CellStyle getStatusStyle(TestStatus status) {
        switch (status) {
            case PASS:
                return styles.get("pass");
            case FAIL:
            case ERROR:
                return styles.get("fail");
            default:
                return styles.get("normal");
        }
    }

    private Map<String, CellStyle> createStyles() {
        Map<String, CellStyle> styles = new HashMap<>();

        // Header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        styles.put("header", headerStyle);

        // Title style
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        styles.put("title", titleStyle);

        // Pass style (green)
        CellStyle passStyle = workbook.createCellStyle();
        passStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        passStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        passStyle.setBorderBottom(BorderStyle.THIN);
        passStyle.setBorderTop(BorderStyle.THIN);
        passStyle.setBorderLeft(BorderStyle.THIN);
        passStyle.setBorderRight(BorderStyle.THIN);
        Font passFont = workbook.createFont();
        passFont.setBold(true);
        passFont.setColor(IndexedColors.DARK_GREEN.getIndex());
        passStyle.setFont(passFont);
        passStyle.setAlignment(HorizontalAlignment.CENTER);
        styles.put("pass", passStyle);

        // Fail style (red)
        CellStyle failStyle = workbook.createCellStyle();
        failStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        failStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        failStyle.setBorderBottom(BorderStyle.THIN);
        failStyle.setBorderTop(BorderStyle.THIN);
        failStyle.setBorderLeft(BorderStyle.THIN);
        failStyle.setBorderRight(BorderStyle.THIN);
        Font failFont = workbook.createFont();
        failFont.setBold(true);
        failFont.setColor(IndexedColors.DARK_RED.getIndex());
        failStyle.setFont(failFont);
        failStyle.setAlignment(HorizontalAlignment.CENTER);
        styles.put("fail", failStyle);

        // Normal cell style
        CellStyle normalStyle = workbook.createCellStyle();
        normalStyle.setBorderBottom(BorderStyle.THIN);
        normalStyle.setBorderTop(BorderStyle.THIN);
        normalStyle.setBorderLeft(BorderStyle.THIN);
        normalStyle.setBorderRight(BorderStyle.THIN);
        normalStyle.setWrapText(true);
        normalStyle.setVerticalAlignment(VerticalAlignment.TOP);
        styles.put("normal", normalStyle);

        // Number style
        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.setBorderBottom(BorderStyle.THIN);
        numberStyle.setBorderTop(BorderStyle.THIN);
        numberStyle.setBorderLeft(BorderStyle.THIN);
        numberStyle.setBorderRight(BorderStyle.THIN);
        numberStyle.setAlignment(HorizontalAlignment.RIGHT);
        styles.put("number", numberStyle);

        return styles;
    }

    // Builder class
    public static class Builder {
        private TestSuite testSuite;
        private String outputFile = "test-report.xlsx";
        private boolean includeSummary = true;
        private boolean includeDetailedTests = true;
        private boolean includeCharts = false;

        public Builder testSuite(TestSuite testSuite) {
            this.testSuite = testSuite;
            return this;
        }

        public Builder outputFile(String outputFile) {
            this.outputFile = outputFile;
            return this;
        }

        public Builder includeSummary(boolean includeSummary) {
            this.includeSummary = includeSummary;
            return this;
        }

        public Builder includeDetailedTests(boolean includeDetailedTests) {
            this.includeDetailedTests = includeDetailedTests;
            return this;
        }

        public Builder includeCharts(boolean includeCharts) {
            this.includeCharts = includeCharts;
            return this;
        }

        public ExcelTestReportGenerator build() {
            if (testSuite == null) {
                throw new IllegalStateException("testSuite is required");
            }
            return new ExcelTestReportGenerator(this);
        }

        public String generate() throws IOException {
            return build().generate();
        }
    }
}
