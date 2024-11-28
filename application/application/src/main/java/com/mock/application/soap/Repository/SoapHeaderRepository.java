package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoapHeaderRepository extends JpaRepository<SoapApplication, String> {
}
