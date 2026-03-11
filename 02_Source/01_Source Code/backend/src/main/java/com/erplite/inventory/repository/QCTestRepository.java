package com.erplite.inventory.repository;

import com.erplite.inventory.entity.QCTest;
import com.erplite.inventory.entity.QCTest.ResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface QCTestRepository extends JpaRepository<QCTest, String> {

    List<QCTest> findByLot_LotId(String lotId);

    List<QCTest> findByLot_LotIdOrderByTestDateDesc(String lotId);

    List<QCTest> findByLot_LotIdAndResultStatus(String lotId, ResultStatus status);

    boolean existsByLot_LotIdAndResultStatus(String lotId, ResultStatus status);

    long countByLot_LotId(String lotId);

    List<QCTest> findByResultStatus(ResultStatus status);

    List<QCTest> findByTestDateBetween(LocalDate from, LocalDate to);
}
