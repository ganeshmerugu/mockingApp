package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapBinding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SoapBindingRepository extends JpaRepository<SoapBinding, String> {

    // Custom query methods if needed

}
