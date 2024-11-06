package com.mock.application.Service.converter;

import com.mock.application.Model.RestMockResponse;
import com.mock.application.Model.RestMockResponseStatus;
import com.mock.application.Model.core.HttpHeader;
import com.mock.application.Model.core.utility.IdUtility;

import java.util.Collections;
import java.util.List;

public abstract class AbstractRestDefinitionConverter implements RestDefinitionConverter {

    protected static final String AUTO_GENERATED_MOCK_RESPONSE_DEFAULT_NAME = "Auto-generated mocked response";
    protected static final int DEFAULT_RESPONSE_CODE = 200;

    protected RestMockResponse generateResponse(final String methodId) {
        return RestMockResponse.builder()
                .id(IdUtility.generateId())
                .methodId(methodId)
                .name(AUTO_GENERATED_MOCK_RESPONSE_DEFAULT_NAME)
                .httpStatusCode(DEFAULT_RESPONSE_CODE)
                .status(RestMockResponseStatus.ENABLED)
                .httpHeaders(Collections.emptyList()) // Provide an empty list for httpHeaders if null is not allowed
                .build();
    }
}
