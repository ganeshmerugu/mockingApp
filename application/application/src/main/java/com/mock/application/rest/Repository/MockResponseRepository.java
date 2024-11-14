package com.mock.application.rest.Repository;

import com.mock.application.rest.Model.RestMockResponse;
import com.mock.application.rest.Model.RestMockResponseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockResponseRepository extends JpaRepository<RestMockResponse, String> {
    List<RestMockResponse> findByMethodId(String  methodId);
    List<RestMockResponse> findByStatus(RestMockResponseStatus status);
    List<RestMockResponse> findByProjectIdAndApplicationIdAndResourceId(String projectId, String applicationId, String resourceId);

    // Custom query methods to find responses based on various IDs
    List<RestMockResponse> findByProjectId(String projectId);

    List<RestMockResponse> findByApplicationIdAndProjectId(String applicationId, String projectId);

    List<RestMockResponse> findByLinkedResourceIdAndApplicationIdAndProjectId(String resourceId, String applicationId, String projectId);
}
