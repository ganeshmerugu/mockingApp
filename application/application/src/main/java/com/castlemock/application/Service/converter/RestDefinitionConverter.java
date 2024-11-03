package com.castlemock.application.Service.converter;

import com.castlemock.application.Model.RestApplication;
import java.io.File;
import java.util.List;

public interface RestDefinitionConverter {
    List<RestApplication> convert(File file, String projectId, boolean generateResponse);
}
