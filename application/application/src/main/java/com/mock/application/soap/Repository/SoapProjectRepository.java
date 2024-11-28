package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoapProjectRepository extends JpaRepository<SoapProject, String> {
}
