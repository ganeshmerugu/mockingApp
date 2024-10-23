package com.castlemock.application.Service;

import com.castlemock.application.Model.MockService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MockServiceManager {

    private List<MockService> mocks = new ArrayList<>(); // In-memory storage for mock services
    private long currentId = 1; // To simulate auto-increment ID

    // Retrieve all mock services
    public List<MockService> getAllMocks() {
        return mocks;
    }

    // Create a new mock service
    public MockService createMock(MockService mockService) {
        mockService.setId(currentId++);
        mocks.add(mockService);
        return mockService;
    }

    // Get a mock service by ID
    public MockService getMockById(Long id) {
        return mocks.stream()
                .filter(mock -> mock.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Delete a mock service by ID
    public void deleteMock(Long id) {
        mocks.removeIf(mock -> mock.getId().equals(id));
    }
}
