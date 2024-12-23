package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapBindingOperation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoapBindingOperationRepository extends JpaRepository<SoapBindingOperation, String> {
}
