package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapPortType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoapPortTypeRepository extends JpaRepository<SoapPortType, String> {
}
