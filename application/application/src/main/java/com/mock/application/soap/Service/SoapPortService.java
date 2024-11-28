package com.mock.application.soap.Service;

import com.mock.application.soap.Model.SoapPort;
import com.mock.application.soap.Repository.SoapPortRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SoapPortService {

    private final SoapPortRepository repository;

    @Autowired
    public SoapPortService(SoapPortRepository repository) {
        this.repository = repository;
    }

    public List<SoapPort> getAllPorts() {
        return repository.findAll();
    }

    public Optional<SoapPort> getPortById(String id) {
        return repository.findById(id);
    }

    @Transactional
    public SoapPort savePort(SoapPort port) {
        return repository.save(port);
    }

    @Transactional
    public void deletePort(String id) {
        repository.deleteById(id);
    }
}
