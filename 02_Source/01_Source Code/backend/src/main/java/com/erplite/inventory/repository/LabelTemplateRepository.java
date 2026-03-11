package com.erplite.inventory.repository;

import com.erplite.inventory.entity.LabelTemplate;
import com.erplite.inventory.entity.LabelTemplate.LabelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelTemplateRepository extends JpaRepository<LabelTemplate, String> {

    List<LabelTemplate> findByLabelType(LabelType labelType);

    boolean existsByTemplateId(String templateId);
}
