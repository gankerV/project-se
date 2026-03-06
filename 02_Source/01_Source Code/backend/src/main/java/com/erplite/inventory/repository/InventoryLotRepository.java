package com.erplite.inventory.repository;

import com.erplite.inventory.entity.InventoryLot;
import com.erplite.inventory.entity.InventoryLot.LotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
