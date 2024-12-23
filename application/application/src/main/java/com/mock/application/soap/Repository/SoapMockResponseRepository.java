package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapMockResponse;
import com.mock.application.soap.Model.SoapMockResponseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SoapMockResponseRepository extends JpaRepository<SoapMockResponse, String> {

    List<SoapMockResponse> findAllByProjectId(String projectId);



    Optional<SoapMockResponse> findByResponseNameAndSoapAction(String operationName, String soapAction);

    @Query("SELECT smr FROM SoapMockResponse smr WHERE smr.operation.name = :operationName")
    Optional<SoapMockResponse> findMockResponseByOperation(@Param("operationName") String operationName);
}
