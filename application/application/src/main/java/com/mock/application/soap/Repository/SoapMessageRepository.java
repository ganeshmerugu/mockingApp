package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoapMessageRepository extends JpaRepository<SoapMessage, String> {
}
