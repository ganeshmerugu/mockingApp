package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapMockResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SoapMockResponseRepository extends JpaRepository<SoapMockResponse, String> {


    /**
     * Finds a SoapMockResponse by operation name.
     *
     * @param operationName The name of the operation.
     * @return An Optional containing the found SoapMockResponse, or empty if not found.
     */
    Optional<SoapMockResponse> findByOperation_Name(String operationName);

    /**
     * Finds all SoapMockResponses associated with a specific project ID.
     *
     * @param projectId The project ID.
     * @return A list of SoapMockResponses.
     */
    List<SoapMockResponse> findAllByProjectId(String projectId);

    @Query("SELECT r FROM SoapMockResponse r WHERE r.soapAction = :soapAction AND r.operation.name = :operationName AND r.projectId = :projectId")
    List<SoapMockResponse> findAllBySoapActionAndOperationNameAndProjectId(
            @Param("soapAction") String soapAction,
            @Param("operationName") String operationName,
            @Param("projectId") String projectId);

}
