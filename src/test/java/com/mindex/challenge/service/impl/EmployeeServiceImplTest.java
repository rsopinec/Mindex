package com.mindex.challenge.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = "http://localhost:" + port + "/employee/{id}/reporting_structure";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }
    
    @Test
    public void testReportingStructure() {
    	Employee developer = new Employee();
        developer.setFirstName("John");
        developer.setLastName("Doe");
        developer.setDepartment("Engineering");
        developer.setPosition("Developer");
        
        developer = restTemplate.postForEntity(employeeUrl, developer, Employee.class).getBody();
        
        Employee developerIntern = new Employee();
        developerIntern.setFirstName("Donald");
        developerIntern.setLastName("Duck");
        developerIntern.setDepartment("Engineering");
        developerIntern.setPosition("Developer Intern");
        
        developerIntern = restTemplate.postForEntity(employeeUrl, developerIntern, Employee.class).getBody();
        
        Employee developer2 = new Employee();
        developer2.setFirstName("Goofy");
        developer2.setLastName("NONE");
        developer2.setDepartment("Engineering");
        developer2.setPosition("Developer");
        developer2.setDirectReports(Stream.of(developerIntern).collect(Collectors.toList()));
        
        developer2 = restTemplate.postForEntity(employeeUrl, developer2, Employee.class).getBody();
        
        Employee manager = new Employee();
        manager.setFirstName("Mickey");
        manager.setLastName("Mouse");
        manager.setDepartment("Engineering");
        manager.setPosition("Team Leader");
        manager.setDirectReports(Stream.of(developer,developer2).collect(Collectors.toList()));
        
        manager = restTemplate.postForEntity(employeeUrl, manager, Employee.class).getBody();
        
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class,manager.getEmployeeId()).getBody();
        
        assertEquals(reportingStructure.getNumberOfReports(), 3);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
