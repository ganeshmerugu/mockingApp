package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapResource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoapResourceRepository extends JpaRepository<SoapResource, String> {
}
