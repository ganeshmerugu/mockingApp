package com.mock.application.soap.Repository;

import com.mock.application.soap.Model.SoapApplication;
import com.mock.application.soap.Model.SoapMockResponse;
import com.mock.application.soap.Model.SoapMockResponseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SoapMockResponseRepository extends JpaRepository<SoapMockResponse, String> {
    List<SoapMockResponse> findByMethodId(String methodId);

    List<SoapMockResponse> findByApplicationIdAndProjectId(String applicationId, String projectId);

    List<SoapMockResponse> findByProjectId(String projectId);

    List<SoapMockResponse> findByStatus(SoapMockResponseStatus status);



    List<SoapMockResponse> findAllByProjectIdAndSoapAction(String projectId, String soapAction);

}