package com.erplite.inventory.repository;

import com.erplite.inventory.entity.Material;
import com.erplite.inventory.entity.Material.MaterialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, String> {

    Optional<Material> findByPartNumber(String partNumber);

    List<Material> findByMaterialType(MaterialType materialType);

    List<Material> findByMaterialNameContainingIgnoreCase(String keyword);

    List<Material> findByMaterialTypeAndMaterialNameContainingIgnoreCase(MaterialType type, String keyword);

    boolean existsByPartNumber(String partNumber);

    boolean existsByPartNumberAndMaterialIdNot(String partNumber, String materialId);
}
