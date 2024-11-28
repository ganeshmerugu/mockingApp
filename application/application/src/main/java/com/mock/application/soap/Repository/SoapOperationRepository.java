package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoapOperationRepository extends JpaRepository<SoapOperation, String> {
}
