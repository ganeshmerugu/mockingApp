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
@RequestMapping("/dynamic")
public class DynamicApiController {

    private final MockResponseService mockResponseService;

    @Autowired
    public DynamicApiController(MockResponseService mockResponseService) {
        this.mockResponseService = mockResponseService;
    }

    @RequestMapping(value = "/{projectId}/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> handleDynamicRequest(@PathVariable String projectId, HttpServletRequest request,@RequestBody(required = false)  String requestBody) {
        String path = request.getRequestURI().substring(("/dynamic/" + projectId).length()).trim();
        String httpMethod = request.getMethod();

        Optional<RestMockResponse> responseOpt = mockResponseService.findMockResponse(projectId, path, httpMethod,requestBody);
        return responseOpt
                .map(response -> ResponseEntity.status(response.getHttpStatusCode())
                        .headers(headers -> response.getHttpHeaders().forEach(header -> headers.add(header.getName(), header.getValue())))
                        .body(response.getBody()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Endpoint not found."));
    }

    @GetMapping("/list-mocked-urls")
    public ResponseEntity<?> listMockedUrls() {
        return ResponseEntity.ok(mockResponseService.listAllEndpoints());
    }
}
