package com.mock.application.Model.mock.rest.domain;

import com.mock.application.Model.RestMockResponseStatus;
import com.mock.application.Model.core.HttpHeader;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true) // Required by JAXB for XML binding
public class RestMockResponse {

    private String id;
    private String name;
    private String body;
    private Integer httpStatusCode;
    private RestMockResponseStatus status;
    private List<HttpHeader> httpHeaders;

    public Optional<String> getBody() {
        return Optional.ofNullable(body);
    }

    public List<HttpHeader> getHttpHeaders() {
        return httpHeaders == null ? new ArrayList<>() : httpHeaders;
    }
}
