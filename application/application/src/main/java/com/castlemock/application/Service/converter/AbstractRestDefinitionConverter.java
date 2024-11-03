package com.castlemock.application.Service.converter;

import com.castlemock.application.Model.RestMockResponse;
import com.castlemock.application.Model.RestMockResponseStatus;
import com.castlemock.application.Model.core.utility.IdUtility;

public abstract class AbstractRestDefinitionConverter implements RestDefinitionConverter {

    protected static final String AUTO_GENERATED_MOCK_RESPONSE_DEFAULT_NAME = "Auto-generated mocked response";
    protected static final int DEFAULT_RESPONSE_CODE = 200;

    protected RestMockResponse generateResponse(final String methodId) {
        return new RestMockResponse(
                IdUtility.generateId(),
                methodId,
                AUTO_GENERATED_MOCK_RESPONSE_DEFAULT_NAME,
                DEFAULT_RESPONSE_CODE,
                RestMockResponseStatus.ENABLED,
                null
        );
    }
}
