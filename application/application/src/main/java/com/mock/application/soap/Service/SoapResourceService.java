package com.mock.application.soap.Service;

import com.mock.application.soap.Model.SoapResource;
import com.mock.application.soap.Repository.SoapResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SoapResourceService {

    private final SoapResourceRepository repository;

    @Autowired
    public SoapResourceService(SoapResourceRepository repository) {
        this.repository = repository;
    }

    public List<SoapResource> getAllResources() {
        return repository.findAll();
    }

    public Optional<SoapResource> getResourceById(String id) {
        return repository.findById(id);
    }

    @Transactional
    public SoapResource saveResource(SoapResource resource) {
        return repository.save(resource);
    }

    @Transactional
    public void deleteResource(String id) {
        repository.deleteById(id);
    }
}
