package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoapTypeRepository extends JpaRepository<SoapType, String> {
}
