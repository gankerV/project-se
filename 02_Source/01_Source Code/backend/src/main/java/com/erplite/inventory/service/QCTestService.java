package com.erplite.inventory.service;

import com.erplite.inventory.dto.qc.QCTestRequest;
import com.erplite.inventory.dto.qc.QCTestResponse;
import com.erplite.inventory.dto.qc.QCTestSummaryResponse;
import com.erplite.inventory.entity.InventoryLot;
import com.erplite.inventory.entity.QCTest;
import com.erplite.inventory.entity.QCTest.ResultStatus;
import com.erplite.inventory.exception.ResourceNotFoundException;
import com.erplite.inventory.repository.InventoryLotRepository;
import com.erplite.inventory.repository.QCTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QCTestService {

    private final QCTestRepository qcTestRepository;
    private final InventoryLotRepository lotRepository;

    public List<QCTestResponse> getTestsForLot(String lotId) {
        return qcTestRepository.findByLot_LotIdOrderByTestDateDesc(lotId)
            .stream().map(QCTestResponse::from).collect(Collectors.toList());
    }

    public QCTestSummaryResponse getSummary(String lotId) {
        InventoryLot lot = lotRepository.findById(lotId)
            .orElseThrow(() -> new ResourceNotFoundException("InventoryLot", "id", lotId));
        List<QCTest> tests = qcTestRepository.findByLot_LotId(lotId);
        long passed = tests.stream().filter(t -> t.getResultStatus() == ResultStatus.Pass).count();
        long failed = tests.stream().filter(t -> t.getResultStatus() == ResultStatus.Fail).count();
        long pending = tests.stream().filter(t -> t.getResultStatus() == ResultStatus.Pending).count();
        return QCTestSummaryResponse.builder()
            .lotId(lotId)
            .lotStatus(lot.getStatus() != null ? lot.getStatus().name() : null)
            .totalTests(tests.size())
            .passed(passed)
            .failed(failed)
            .pending(pending)
            .build();
    }

    public QCTestResponse getTestById(String id) {
        return QCTestResponse.from(qcTestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("QCTest", "id", id)));
    }

    @Transactional
    public QCTestResponse createTest(QCTestRequest req, String callerUsername) {
        InventoryLot lot = lotRepository.findById(req.getLotId())
            .orElseThrow(() -> new ResourceNotFoundException("InventoryLot", "id", req.getLotId()));
        QCTest test = QCTest.builder()
            .lot(lot)
            .testType(req.getTestType())
            .testMethod(req.getTestMethod())
            .testDate(req.getTestDate())
            .testResult(req.getTestResult())
            .acceptanceCriteria(req.getAcceptanceCriteria())
            .resultStatus(req.getResultStatus())
            .performedBy(req.getPerformedBy() != null ? req.getPerformedBy() : callerUsername)
            .verifiedBy(req.getVerifiedBy())
            .build();
        return QCTestResponse.from(qcTestRepository.save(test));
    }

    @Transactional
    public QCTestResponse updateTest(String id, QCTestRequest req) {
        QCTest test = qcTestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("QCTest", "id", id));
        if (!test.getLot().getLotId().equals(req.getLotId())) {
            InventoryLot lot = lotRepository.findById(req.getLotId())
                .orElseThrow(() -> new ResourceNotFoundException("InventoryLot", "id", req.getLotId()));
            test.setLot(lot);
        }
        test.setTestType(req.getTestType());
        test.setTestMethod(req.getTestMethod());
        test.setTestDate(req.getTestDate());
        test.setTestResult(req.getTestResult());
        test.setAcceptanceCriteria(req.getAcceptanceCriteria());
        test.setResultStatus(req.getResultStatus());
        test.setPerformedBy(req.getPerformedBy());
        test.setVerifiedBy(req.getVerifiedBy());
        return QCTestResponse.from(qcTestRepository.save(test));
    }
}
