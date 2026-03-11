package com.erplite.inventory.repository;

import com.erplite.inventory.entity.InventoryLot;
import com.erplite.inventory.entity.InventoryLot.LotStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryLotRepository extends JpaRepository<InventoryLot, String> {

    List<InventoryLot> findByMaterial_MaterialId(String materialId);

    List<InventoryLot> findByStatus(LotStatus status);

    List<InventoryLot> findByMaterial_MaterialIdAndStatus(String materialId, LotStatus status);

    List<InventoryLot> findByExpirationDateBefore(LocalDate date);

    boolean existsByMaterial_MaterialId(String materialId);

    @Query("SELECT l FROM InventoryLot l WHERE l.expirationDate BETWEEN :today AND :cutoff AND l.status = 'Accepted'")
    List<InventoryLot> findNearExpiry(@Param("today") LocalDate today, @Param("cutoff") LocalDate cutoff);

    @Query("SELECT l FROM InventoryLot l WHERE l.expirationDate BETWEEN :today AND :cutoff AND l.status = 'Accepted'")
    Page<InventoryLot> findNearExpiry(@Param("today") LocalDate today, @Param("cutoff") LocalDate cutoff, Pageable pageable);

    List<InventoryLot> findByIsSample(Boolean isSample);

    List<InventoryLot> findByParentLot_LotId(String parentLotId);

    Page<InventoryLot> findByStatus(LotStatus status, Pageable pageable);

    Page<InventoryLot> findByMaterial_MaterialId(String materialId, Pageable pageable);

    Page<InventoryLot> findByMaterial_MaterialIdAndStatus(String materialId, LotStatus status, Pageable pageable);

    Page<InventoryLot> findByIsSample(Boolean isSample, Pageable pageable);
}
