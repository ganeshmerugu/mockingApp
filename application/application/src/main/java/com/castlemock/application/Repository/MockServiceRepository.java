package com.castlemock.application.Repository;

import com.castlemock.application.Model.MockService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MockServiceRepository extends JpaRepository<MockService, Long> {
    Optional<MockService> findByEndpointAndMethod(String endpoint, String method);
}
