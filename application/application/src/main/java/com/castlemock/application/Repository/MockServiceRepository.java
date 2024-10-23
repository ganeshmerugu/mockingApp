package com.castlemock.application.Repository;

import com.castlemock.application.Model.MockService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MockServiceRepository extends JpaRepository<MockService, Long> {

    Optional<MockService> findByEndpoint(String endpoint);

    Optional<MockService> findByOriginalEndpoint(String originalEndpoint);
}
