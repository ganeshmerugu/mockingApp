package com.mock.application.rest.Repository;

import com.mock.application.rest.Model.RestMockResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestMockResponseRepository extends JpaRepository<RestMockResponse, String> {
    Optional<RestMockResponse> findByProjectIdAndPathAndHttpMethod(String projectId, String path, String httpMethod);

    // Add the new method

    List<RestMockResponse> findAllByProjectIdAndPathAndHttpMethod(String projectId, String path, String httpMethod);

    Optional<RestMockResponse> findByProjectIdAndPathAndHttpMethodAndHttpStatusCode(String projectId, String path, String httpMethod, int httpStatusCode);

    List<RestMockResponse> findAllByProjectIdAndHttpMethodAndHttpStatusCode(String projectId, String httpMethod, int httpStatusCode);
    List<RestMockResponse> findAllByProjectIdAndHttpMethod(String projectId, String httpMethod);


    Optional<RestMockResponse> findByProjectIdAndPathAndHttpStatusCode(String projectId, String path, Integer httpStatusCode);

}
