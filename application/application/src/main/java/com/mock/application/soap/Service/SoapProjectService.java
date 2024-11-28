package com.mock.application.soap.Service;

import com.mock.application.soap.Model.SoapProject;
import com.mock.application.soap.Repository.SoapProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SoapProjectService {

    private final SoapProjectRepository repository;

    @Autowired
    public SoapProjectService(SoapProjectRepository repository) {
        this.repository = repository;
    }

    public List<SoapProject> getAllProjects() {
        return repository.findAll();
    }

    public Optional<SoapProject> getProjectById(String id) {
        return repository.findById(id);
    }

    @Transactional
    public SoapProject saveProject(SoapProject project) {
        return repository.save(project);
    }

    @Transactional
    public void deleteProject(String id) {
        repository.deleteById(id);
    }
}
