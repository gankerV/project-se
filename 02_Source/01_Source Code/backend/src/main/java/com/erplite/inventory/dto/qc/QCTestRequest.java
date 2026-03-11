package com.erplite.inventory.dto.qc;

import com.erplite.inventory.entity.QCTest.ResultStatus;
import com.erplite.inventory.entity.QCTest.TestType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class QCTestRequest {

    @NotBlank(message = "Lot ID is required")
    private String lotId;

    @NotNull(message = "Test type is required")
    private TestType testType;

    @NotBlank(message = "Test method is required")
    @Size(max = 100)
    private String testMethod;

    @NotNull(message = "Test date is required")
    @PastOrPresent(message = "Test date must be in the past or present")
    private LocalDate testDate;

    @NotBlank(message = "Test result is required")
    @Size(max = 500)
    private String testResult;

    @Size(max = 500)
    private String acceptanceCriteria;

    @NotNull(message = "Result status is required")
    private ResultStatus resultStatus;

    @NotBlank(message = "Performed by is required")
    @Size(max = 50)
    private String performedBy;

    @Size(max = 50)
    private String verifiedBy;
}
