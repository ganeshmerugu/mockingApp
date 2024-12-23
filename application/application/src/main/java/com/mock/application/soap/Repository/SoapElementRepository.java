package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapElement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoapElementRepository extends JpaRepository<SoapElement, String> {
}
