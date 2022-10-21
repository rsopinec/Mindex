package com.mindex.challenge.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController()
public class CompensationController {
	
	@Autowired
	CompensationService compensationService;
	
	@GetMapping("/compensation/{employeeId}")
	public List<Compensation> readCompensation(@PathVariable String employeeId) {
		log.debug("Received compensation get request for employee id {}",employeeId);
		return compensationService.readByEmployeeId(employeeId);
	}

	@PostMapping("/compensation")
	public Compensation createCompensation(@Valid @RequestBody Compensation compensation) {
		log.debug("Received compensation create request for [{}]",compensation);
		return compensationService.create(compensation);
	}
}
