package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapMessagePart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SoapMessagePartRepository extends JpaRepository<SoapMessagePart, UUID> {
}
