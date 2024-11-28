package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoapPortRepository extends JpaRepository<SoapPort, String> {
}
