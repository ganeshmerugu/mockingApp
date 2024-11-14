package com.mock.application.rest.Controller;

import com.mock.application.rest.Model.RestMockResponse;
import com.mock.application.rest.Service.MockResponseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/static")
public class StaticApiController {

    private final MockResponseService mockResponseService;

    @Autowired
    public StaticApiController(MockResponseService mockResponseService) {
        this.mockResponseService = mockResponseService;
    }

    @RequestMapping(value = "/{projectId}/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> handleStaticRequest(
            @PathVariable String projectId,
            HttpServletRequest request) {

        String path = request.getRequestURI().substring(("/static/" + projectId).length()).trim();
        String httpMethod = request.getMethod();

        Optional<RestMockResponse> responseOpt = mockResponseService.findMockResponse(projectId, path, httpMethod);

        return responseOpt
                .map(response -> ResponseEntity.status(response.getHttpStatusCode())
                        .headers(headers -> response.getHttpHeaders().forEach(header -> headers.add(header.getName(), header.getValue())))
                        .body(mockResponseService.generateCompleteJsonResponse(response)))  // Return the full JSON response
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Endpoint not found.\"}"));
    }
}
