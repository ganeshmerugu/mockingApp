package com.mock.application.soap.Service;

import com.mock.application.soap.Model.SoapMockResponse;
import com.mock.application.soap.Model.SoapMockResponseStatus;
import com.mock.application.soap.Repository.SoapMockResponseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// Import other necessary packages
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing SoapMockResponse entities.
 */
@Service
public class SoapMockResponseService {

    private static final Logger logger = LoggerFactory.getLogger(SoapMockResponseService.class);

    private final SoapMockResponseRepository responseRepository;

    @Autowired
    public SoapMockResponseService(SoapMockResponseRepository responseRepository) {
        this.responseRepository = responseRepository;
    }

    /**
     * Saves a new SoapMockResponse to the repository.
     * Defaults the status to ENABLED if not already set.
     *
     * @param mockResponse The SoapMockResponse to save.
     * @return The saved SoapMockResponse.
     */
    @Transactional
    public SoapMockResponse saveMockResponse(SoapMockResponse mockResponse) {
        if (mockResponse.getSoapMockResponseStatus() == null) {
            mockResponse.setSoapMockResponseStatus(SoapMockResponseStatus.ENABLED);  // Default to ENABLED
        }
        logger.debug("Saving SoapMockResponse: {}", mockResponse.getResponseName());
        return responseRepository.save(mockResponse);
    }

    /**
     * Finds a SoapMockResponse by response name and SOAP action.
     *
     * @param responseName The name of the response.
     * @param soapAction   The SOAP action.
     * @return An Optional containing the found SoapMockResponse, or empty if not found.
     */
    public Optional<SoapMockResponse> findMockResponse(String responseName, String soapAction) {
        logger.debug("Finding SoapMockResponse by responseName='{}' and soapAction='{}'.", responseName, soapAction);
        return responseRepository.findByResponseNameAndSoapAction(responseName, soapAction);
    }

    /**
     * Finds a SoapMockResponse by operation name.
     *
     * @param operationName The name of the operation.
     * @return An Optional containing the found SoapMockResponse, or empty if not found.
     */
    public Optional<SoapMockResponse> findMockResponseByOperation(String operationName) {
        logger.debug("Finding SoapMockResponse by operationName='{}'.", operationName);
        return responseRepository.findByOperation_Name(operationName);
    }

    /**
     * Retrieves all SoapMockResponses associated with a specific project ID.
     *
     * @param projectId The project ID.
     * @return A list of SoapMockResponses.
     */
    public List<SoapMockResponse> findAllByProjectId(String projectId) {
        logger.debug("Retrieving all SoapMockResponses for projectId='{}'.", projectId);
        return responseRepository.findAllByProjectId(projectId);
    }

    /**
     * Deletes a SoapMockResponse by its ID.
     *
     * @param id The ID of the SoapMockResponse to delete.
     * @return True if deletion was successful, false otherwise.
     */
    @Transactional
    public boolean deleteMockResponse(String id) {
        if (responseRepository.existsById(id)) {
            logger.debug("Deleting SoapMockResponse with id='{}'.", id);
            responseRepository.deleteById(id);
            return true;
        }
        logger.warn("Attempted to delete non-existent SoapMockResponse with id='{}'.", id);
        return false;
    }

    /**
     * Updates an existing SoapMockResponse with new data.
     *
     * @param id              The ID of the SoapMockResponse to update.
     * @param updatedResponse The SoapMockResponse containing updated data.
     * @return An Optional containing the updated SoapMockResponse, or empty if not found.
     */
    @Transactional
    public Optional<SoapMockResponse> updateMockResponse(String id, SoapMockResponse updatedResponse) {
        logger.debug("Updating SoapMockResponse with id='{}'.", id);
        return responseRepository.findById(id).map(existingResponse -> {
            // Update fields if they are not null (optional: add more validation)
            if (updatedResponse.getResponseName() != null) {
                existingResponse.setResponseName(updatedResponse.getResponseName());
            }
            if (updatedResponse.getResponseBody() != null) {
                existingResponse.setResponseBody(updatedResponse.getResponseBody());
            }
            if (updatedResponse.getHttpStatusCode() != 0) { // Assuming 0 is not a valid status code
                existingResponse.setHttpStatusCode(updatedResponse.getHttpStatusCode());
            }
            if (updatedResponse.getSoapAction() != null) {
                existingResponse.setSoapAction(updatedResponse.getSoapAction());
            }
            if (updatedResponse.getSoapMockResponseStatus() != null) {
                existingResponse.setSoapMockResponseStatus(updatedResponse.getSoapMockResponseStatus());
            }
            // Add more fields as necessary

            logger.debug("Saving updated SoapMockResponse with id='{}'.", id);
            return responseRepository.save(existingResponse);
        });
    }
}
