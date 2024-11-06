package com.castlemock.application.Repository;

import com.castlemock.application.Model.RestMockResponse;
import com.castlemock.application.Model.RestMockResponseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockResponseRepository extends JpaRepository<RestMockResponse, String> {
    List<RestMockResponse> findByMethodId(String  methodId);
    List<RestMockResponse> findByStatus(RestMockResponseStatus status);

    // Custom query methods to find responses based on various IDs
    List<RestMockResponse> findByProjectId(String projectId);

    List<RestMockResponse> findByApplicationIdAndProjectId(String applicationId, String projectId);

    List<RestMockResponse> findByLinkedResourceIdAndApplicationIdAndProjectId(String resourceId, String applicationId, String projectId);
}
