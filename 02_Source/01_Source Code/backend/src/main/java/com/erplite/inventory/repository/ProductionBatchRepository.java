package com.erplite.inventory.repository;

import com.erplite.inventory.entity.ProductionBatch;
import com.erplite.inventory.entity.ProductionBatch.BatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionBatchRepository extends JpaRepository<ProductionBatch, String> {

    boolean existsByBatchNumber(String batchNumber);

    List<ProductionBatch> findByStatus(BatchStatus status);

    List<ProductionBatch> findByProduct_MaterialId(String productId);

    List<ProductionBatch> findByStatusAndProduct_MaterialId(BatchStatus status, String productId);

    Page<ProductionBatch> findByStatus(BatchStatus status, Pageable pageable);

    Page<ProductionBatch> findByProduct_MaterialId(String productId, Pageable pageable);

    Page<ProductionBatch> findByStatusAndProduct_MaterialId(BatchStatus status, String productId, Pageable pageable);
}
