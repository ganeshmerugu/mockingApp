package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface SoapServiceRepository extends JpaRepository<SoapService, UUID> {
    // Additional query methods if needed
}
