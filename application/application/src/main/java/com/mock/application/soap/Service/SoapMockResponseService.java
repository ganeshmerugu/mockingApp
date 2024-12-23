package com.mock.application.soap.Service;

import com.mock.application.soap.Model.SoapMockResponse;
import com.mock.application.soap.Model.SoapMockResponseStatus;
import com.mock.application.soap.Repository.SoapMockResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SoapMockResponseService {

    private final SoapMockResponseRepository responseRepository;

    @Autowired
    public SoapMockResponseService(SoapMockResponseRepository responseRepository) {
        this.responseRepository = responseRepository;
    }

    public SoapMockResponse saveMockResponse(SoapMockResponse mockResponse) {
        mockResponse.setSoapMockResponseStatus(SoapMockResponseStatus.ENABLED);  // Default to ENABLED
        return responseRepository.save(mockResponse);
    }

    public Optional<SoapMockResponse> findMockResponse(String operationName, String soapAction) {
        return responseRepository.findByResponseNameAndSoapAction(operationName, soapAction);
    }

    public Optional<SoapMockResponse> findMockResponseByOperation(String operationName) {
        return responseRepository.findMockResponseByOperation(operationName);
    }

    public List<SoapMockResponse> findAllByProjectId(String projectId) {
        return responseRepository.findAllByProjectId(projectId);
    }

    public boolean deleteMockResponse(String id) {
        if (responseRepository.existsById(id)) {
            responseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<SoapMockResponse> updateMockResponse(String id, SoapMockResponse updatedResponse) {
        return responseRepository.findById(id).map(existingResponse -> {
            existingResponse.setResponseName(updatedResponse.getResponseName());
            existingResponse.setResponseBody(updatedResponse.getResponseBody());
            existingResponse.setHttpStatusCode(updatedResponse.getHttpStatusCode());
            existingResponse.setSoapAction(updatedResponse.getSoapAction());
            existingResponse.setSoapMockResponseStatus(updatedResponse.getSoapMockResponseStatus());
            return responseRepository.save(existingResponse);
        });
    }
}
