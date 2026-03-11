package com.erplite.inventory.dto.qc;

import com.erplite.inventory.entity.QCTest;
import com.erplite.inventory.entity.QCTest.ResultStatus;
import com.erplite.inventory.entity.QCTest.TestType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class QCTestResponse {

    private String testId;
    private String lotId;
    private String lotManufacturerLot;
    private TestType testType;
    private String testMethod;
    private LocalDate testDate;
    private String testResult;
    private String acceptanceCriteria;
    private ResultStatus resultStatus;
    private String performedBy;
    private String verifiedBy;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static QCTestResponse from(QCTest test) {
        QCTestResponse dto = new QCTestResponse();
        dto.setTestId(test.getTestId());
        dto.setTestType(test.getTestType());
        dto.setTestMethod(test.getTestMethod());
        dto.setTestDate(test.getTestDate());
        dto.setTestResult(test.getTestResult());
        dto.setAcceptanceCriteria(test.getAcceptanceCriteria());
        dto.setResultStatus(test.getResultStatus());
        dto.setPerformedBy(test.getPerformedBy());
        dto.setVerifiedBy(test.getVerifiedBy());
        dto.setCreatedDate(test.getCreatedDate());
        dto.setModifiedDate(test.getModifiedDate());
        if (test.getLot() != null) {
            dto.setLotId(test.getLot().getLotId());
            dto.setLotManufacturerLot(test.getLot().getManufacturerLot());
        }
        return dto;
    }
}
