package com.testgenerator.testing.report.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single test case with all its details.
 */
public class TestCase {
    private String testId;
    private String testName;
    private String description;
    private String category;
    private TestStatus status;
    private String expectedResult;
    private String actualResult;
    private String errorMessage;
    private String stackTrace;
    private long durationMs;
    private Map<String, String> customFields;

    public TestCase() {
        this.customFields = new HashMap<>();
    }

    public TestCase(String testId, String testName, TestStatus status) {
        this();
        this.testId = testId;
        this.testName = testName;
        this.status = status;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final TestCase testCase;

        public Builder() {
            this.testCase = new TestCase();
        }

        public Builder testId(String testId) {
            testCase.testId = testId;
            return this;
        }

        public Builder testName(String testName) {
            testCase.testName = testName;
            return this;
        }

        public Builder description(String description) {
            testCase.description = description;
            return this;
        }

        public Builder category(String category) {
            testCase.category = category;
            return this;
        }

        public Builder status(TestStatus status) {
            testCase.status = status;
            return this;
        }

        public Builder expectedResult(String expectedResult) {
            testCase.expectedResult = expectedResult;
            return this;
        }

        public Builder actualResult(String actualResult) {
            testCase.actualResult = actualResult;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            testCase.errorMessage = errorMessage;
            return this;
        }

        public Builder stackTrace(String stackTrace) {
            testCase.stackTrace = stackTrace;
            return this;
        }

        public Builder durationMs(long durationMs) {
            testCase.durationMs = durationMs;
            return this;
        }

        public Builder addCustomField(String key, String value) {
            testCase.customFields.put(key, value);
            return this;
        }

        public TestCase build() {
            if (testCase.testName == null) {
                throw new IllegalStateException("testName is required");
            }
            if (testCase.status == null) {
                testCase.status = TestStatus.PENDING;
            }
            return testCase;
        }
    }

    // Getters and setters
    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public TestStatus getStatus() {
        return status;
    }

    public void setStatus(TestStatus status) {
        this.status = status;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public String getActualResult() {
        return actualResult;
    }

    public void setActualResult(String actualResult) {
        this.actualResult = actualResult;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public Map<String, String> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
    }

    public String getDurationFormatted() {
        if (durationMs < 1000) {
            return durationMs + "ms";
        } else if (durationMs < 60000) {
            return String.format("%.2fs", durationMs / 1000.0);
        } else {
            long minutes = durationMs / 60000;
            long seconds = (durationMs % 60000) / 1000;
            return minutes + "m " + seconds + "s";
        }
    }
}
