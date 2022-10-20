package com.mindex.challenge.service;

import java.util.List;

import com.mindex.challenge.data.Compensation;

public interface CompensationService {

	List<Compensation> readByEmployeeId(String employeeId);
	Compensation create(Compensation compensation);
}
