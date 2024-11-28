package com.mock.application.soap.Service;

import com.mock.application.soap.Model.SoapMockResponse;
import com.mock.application.soap.Repository.SoapMockResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SoapMockResponseService {

    private final SoapMockResponseRepository repository;

    @Autowired
    public SoapMockResponseService(SoapMockResponseRepository repository) {
        this.repository = repository;
    }

    public List<SoapMockResponse> getAllMockResponses() {
        return repository.findAll();
    }

    public Optional<SoapMockResponse> getMockResponseById(String id) {
        return repository.findById(id);
    }

    @Transactional
    public SoapMockResponse saveMockResponse(SoapMockResponse mockResponse) {
        return repository.save(mockResponse);
    }

    @Transactional
    public void deleteMockResponse(String id) {
        repository.deleteById(id);
    }
}
