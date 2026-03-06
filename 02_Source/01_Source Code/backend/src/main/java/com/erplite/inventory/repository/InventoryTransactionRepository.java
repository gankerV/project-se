package com.erplite.inventory.repository;

import com.erplite.inventory.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, String> {

    List<InventoryTransaction> findByLot_LotIdOrderByTransactionDateDesc(String lotId);
}
