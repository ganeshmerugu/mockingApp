package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoapMethodRepository extends JpaRepository<SoapMethod, String> {
}
