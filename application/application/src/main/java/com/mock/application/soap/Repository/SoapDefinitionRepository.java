package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoapDefinitionRepository extends JpaRepository<SoapDefinition, String> {
}
