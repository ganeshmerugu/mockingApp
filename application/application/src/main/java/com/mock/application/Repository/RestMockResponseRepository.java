package com.mock.application.Repository;

import com.mock.application.Model.RestMockResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestMockResponseRepository extends JpaRepository<RestMockResponse, String> {
    Optional<RestMockResponse> findByProjectIdAndPathAndHttpMethod(String projectId, String path, String httpMethod);

    // Add the new method
    List<RestMockResponse> findByProjectIdAndApplicationIdAndResourceId(String projectId, String applicationId, String resourceId);
}
