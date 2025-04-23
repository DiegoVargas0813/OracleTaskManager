package com.springboot.MyTodoList;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import com.springboot.MyTodoList.model.Manager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ManagerControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // Tests for GET /api/managers endpoint
    @Test
    public void testGetAllManagers() {
        ResponseEntity<Manager[]> response = restTemplate.getForEntity("/api/managers", Manager[].class);

        // Check if the response status is OK (200)
        assert(response.getStatusCode().is2xxSuccessful());

        // Check if the response body is not null
        assert(response.getBody() != null);

        // Check if the response body is an array
        assert(response.getBody() instanceof Manager[]);

        // Check if the array is not empty
        assert(((Manager[]) response.getBody()).length > 0);
    }

    // Tests for GET /api/managers/{id} endpoint
    
    @Test
    public void testGetManagerById() {
        // Assuming you have a manager with ID 1 in your database
        int managerId = 1;

        ResponseEntity<Manager> response = restTemplate.getForEntity("/api/managers/" + managerId, Manager.class);

        // Check if the response status is OK (200)
        assert(response.getStatusCode().is2xxSuccessful());

        // Check if the response body is not null
        assert(response.getBody() != null);

        // Check if the manager ID in the response matches the requested ID
        assert(response.getBody().getId() == managerId);
    }

    // Test for getting a manager by ID that does not exist
    @Test
    public void testGetManagerByIdNotFound() {
        // Assuming you do not have a manager with ID 999 in your database
        int managerId = 999;

        ResponseEntity<Manager> response = restTemplate.getForEntity("/api/managers/" + managerId, Manager.class);

        // Check if the response status is NOT FOUND (404)
        assert(response.getStatusCode().is4xxClientError());

        // Check if the response body is null
        assert(response.getBody() == null);
    }

}
