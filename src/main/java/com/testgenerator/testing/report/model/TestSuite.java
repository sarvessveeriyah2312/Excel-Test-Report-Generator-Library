package com.testgenerator.testing.report.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a test suite containing multiple test cases and summary information.
 */
public class TestSuite {
    private String suiteName;
    private String projectName;
    private String description;
    private String tester;
    private String environment;
    private LocalDateTime executionDate;
    private List<TestCase> testCases;
    private Map<String, String> metadata;

    public TestSuite() {
        this.testCases = new ArrayList<>();
        this.metadata = new HashMap<>();
        this.executionDate = LocalDateTime.now();
    }

    public TestSuite(String suiteName) {
        this();
        this.suiteName = suiteName;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final TestSuite testSuite;

        public Builder() {
            this.testSuite = new TestSuite();
        }

        public Builder suiteName(String suiteName) {
            testSuite.suiteName = suiteName;
            return this;
        }

        public Builder projectName(String projectName) {
            testSuite.projectName = projectName;
            return this;
        }

        public Builder description(String description) {
            testSuite.description = description;
            return this;
        }

        public Builder tester(String tester) {
            testSuite.tester = tester;
            return this;
        }

        public Builder environment(String environment) {
            testSuite.environment = environment;
            return this;
        }

        public Builder executionDate(LocalDateTime executionDate) {
            testSuite.executionDate = executionDate;
            return this;
        }

        public Builder addTestCase(TestCase testCase) {
            testSuite.testCases.add(testCase);
            return this;
        }

        public Builder addTestCases(List<TestCase> testCases) {
            testSuite.testCases.addAll(testCases);
            return this;
        }

        public Builder addMetadata(String key, String value) {
            testSuite.metadata.put(key, value);
            return this;
        }

        public TestSuite build() {
            if (testSuite.suiteName == null) {
                testSuite.suiteName = "Test Suite";
            }
            return testSuite;
        }
    }

    // Computed statistics
    public int getTotalTests() {
        return testCases.size();
    }

    public int getPassedTests() {
        return (int) testCases.stream()
                .filter(tc -> tc.getStatus() == TestStatus.PASS)
                .count();
    }

    public int getFailedTests() {
        return (int) testCases.stream()
                .filter(tc -> tc.getStatus() == TestStatus.FAIL)
                .count();
    }

    public int getErrorTests() {
        return (int) testCases.stream()
                .filter(tc -> tc.getStatus() == TestStatus.ERROR)
                .count();
    }

    public int getSkippedTests() {
        return (int) testCases.stream()
                .filter(tc -> tc.getStatus() == TestStatus.SKIP)
                .count();
    }

    public double getPassRate() {
        if (getTotalTests() == 0) return 0.0;
        return (getPassedTests() * 100.0) / getTotalTests();
    }

    public long getTotalDurationMs() {
        return testCases.stream()
                .mapToLong(TestCase::getDurationMs)
                .sum();
    }

    public String getTotalDurationFormatted() {
        long totalMs = getTotalDurationMs();
        if (totalMs < 1000) {
            return totalMs + "ms";
        } else if (totalMs < 60000) {
            return String.format("%.2fs", totalMs / 1000.0);
        } else {
            long minutes = totalMs / 60000;
            long seconds = (totalMs % 60000) / 1000;
            return minutes + "m " + seconds + "s";
        }
    }

    public Map<String, Long> getTestsByCategory() {
        return testCases.stream()
                .filter(tc -> tc.getCategory() != null)
                .collect(Collectors.groupingBy(
                        TestCase::getCategory,
                        Collectors.counting()
                ));
    }

    // Getters and setters
    public String getSuiteName() {
        return suiteName;
    }

    public void setSuiteName(String suiteName) {
        this.suiteName = suiteName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTester() {
        return tester;
    }

    public void setTester(String tester) {
        this.tester = tester;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public LocalDateTime getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDateTime executionDate) {
        this.executionDate = executionDate;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
