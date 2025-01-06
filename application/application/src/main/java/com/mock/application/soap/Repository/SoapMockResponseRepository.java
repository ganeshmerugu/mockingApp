package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapMockResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SoapMockResponseRepository extends JpaRepository<SoapMockResponse, String> {

    /**
     * Finds a SoapMockResponse by response name and SOAP action.
     *
     * @param responseName The name of the response.
     * @param soapAction   The SOAP action.
     * @return An Optional containing the found SoapMockResponse, or empty if not found.
     */
    Optional<SoapMockResponse> findByResponseNameAndSoapAction(String responseName, String soapAction);

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
}
