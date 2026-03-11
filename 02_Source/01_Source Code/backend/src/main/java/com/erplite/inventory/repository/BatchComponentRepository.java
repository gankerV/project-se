package com.erplite.inventory.repository;

import com.erplite.inventory.entity.BatchComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BatchComponentRepository extends JpaRepository<BatchComponent, String> {

    List<BatchComponent> findByBatch_BatchId(String batchId);

    List<BatchComponent> findByBatch_BatchIdOrderByCreatedDateAsc(String batchId);

    boolean existsByBatch_BatchIdAndLot_LotId(String batchId, String lotId);

    long countByBatch_BatchIdAndActualQuantityIsNull(String batchId);

    @Transactional
    void deleteByBatch_BatchId(String batchId);
}
