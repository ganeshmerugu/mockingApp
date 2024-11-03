package com.castlemock.application.Repository;

import com.castlemock.application.Model.RestMockResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockResponseRepository extends JpaRepository<RestMockResponse, String> {

    // Custom query methods to find responses based on various IDs
    List<RestMockResponse> findByProjectId(String projectId);

    List<RestMockResponse> findByApplicationIdAndProjectId(String applicationId, String projectId);

    List<RestMockResponse> findByResourceIdAndApplicationIdAndProjectId(String resourceId, String applicationId, String projectId);
}
