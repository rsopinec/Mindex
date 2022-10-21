package com.mindex.challenge.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

	private String employeeUrl;
	private String compensationUrl;
	private String compensationUrlEmployeeId;
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Before
	public void setup() {
		employeeUrl = "http://localhost:" + port + "/employee";
		compensationUrl = "http://localhost:"+port+"/compensation";
		compensationUrlEmployeeId = "http://localhost:"+port+"/compensation/{employeeId}";
	}
	
	@Test
	public void testCreateRead() {
		Employee developer = new Employee();
        developer.setFirstName("John");
        developer.setLastName("Doe");
        developer.setDepartment("Engineering");
        developer.setPosition("Developer");
        
        developer = restTemplate.postForEntity(employeeUrl, developer, Employee.class).getBody();
        String developerEmployeeId = developer.getEmployeeId();
        
		Compensation compensation = new Compensation();
		compensation.setEffectiveDate(LocalDate.of(2022, 10, 10));
		compensation.setEmployeeId(developerEmployeeId);
		compensation.setSalary(123456.90);
		
		Compensation persistedCompensation = restTemplate.postForEntity(compensationUrl, compensation, Compensation.class).getBody();
		
		assertEquals(persistedCompensation.getEffectiveDate(),compensation.getEffectiveDate());
		assertEquals(persistedCompensation.getEmployeeId(), compensation.getEmployeeId());
		assertEquals(persistedCompensation.getSalary(), compensation.getSalary());
	
		Compensation compensation2 = new Compensation();
		compensation2.setEffectiveDate(LocalDate.of(2022, 10, 21));
		compensation2.setEmployeeId(developerEmployeeId);
		compensation2.setSalary(123999.90);
		
		compensation2 = restTemplate.postForEntity(compensationUrl, compensation2, Compensation.class).getBody();
		
		List<Compensation> compensations = Arrays.stream(restTemplate.getForEntity(compensationUrlEmployeeId, Compensation[].class,developerEmployeeId).getBody()).collect(Collectors.toList());
		
		assertEquals(compensations.size(), 2);
		
	}
}
