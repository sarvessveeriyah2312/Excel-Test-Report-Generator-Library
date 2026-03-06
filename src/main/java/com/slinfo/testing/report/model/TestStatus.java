package com.slinfo.testing.report.model;

/**
 * Enum representing the status of a test case.
 */
public enum TestStatus {
    PASS("PASS", "Passed"),
    FAIL("FAIL", "Failed"),
    ERROR("ERROR", "Error"),
    SKIP("SKIP", "Skipped"),
    PENDING("PENDING", "Pending");

    private final String code;
    private final String description;

    TestStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
