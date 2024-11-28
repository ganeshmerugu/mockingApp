package com.mock.application.soap.Service;

import com.mock.application.soap.Model.SoapOperation;
import com.mock.application.soap.Repository.SoapOperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SoapOperationService {

    private final SoapOperationRepository repository;

    @Autowired
    public SoapOperationService(SoapOperationRepository repository) {
        this.repository = repository;
    }

    public List<SoapOperation> getAllOperations() {
        return repository.findAll();
    }

    public Optional<SoapOperation> getOperationById(String id) {
        return repository.findById(id);
    }

    @Transactional
    public SoapOperation saveOperation(SoapOperation operation) {
        return repository.save(operation);
    }

    @Transactional
    public void deleteOperation(String id) {
        repository.deleteById(id);
    }
}
