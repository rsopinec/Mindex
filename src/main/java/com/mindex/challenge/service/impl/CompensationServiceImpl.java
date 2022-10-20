package com.mindex.challenge.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CompensationServiceImpl implements CompensationService{

	@Autowired
	private CompensationRepository compensationRepo;
	
	@Override
	public List<Compensation> readByEmployeeId(String employeeId) {
		log.debug("Reading compensation for employee with id [{}]",employeeId);
		List<Compensation> compensation = compensationRepo.findByEmployeeId(employeeId);
		
		if(compensation==null || compensation.isEmpty()) {
			throw new RuntimeException("Missing compensation for employee id: "+employeeId);
		}
		return compensation;
	}

	@Override
	public Compensation create(Compensation compensation) {
		log.debug("Creating compensation for employee with id [{}]",compensation.getEmployeeId());
		compensation.setCompensationId(UUID.randomUUID().toString());

		return compensationRepo.save(compensation);
	}

}
