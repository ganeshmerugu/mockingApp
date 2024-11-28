package com.mock.application.rest.Repository;

import com.mock.application.rest.Model.JsonKeyDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JsonKeyDefinitionRepository extends JpaRepository<JsonKeyDefinition, Long> {
    List<JsonKeyDefinition> findByRestMockResponseId(String responseId);
}