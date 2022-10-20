package com.mindex.challenge.service.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Reading employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

	@Override
	public ReportingStructure getReportingStructure(String id) {
		Employee employee = employeeRepository.findByEmployeeId(id);
		
		 if (employee == null) {
	            throw new RuntimeException("Invalid employeeId: " + id);
	        }
		
		ReportingStructure reportingStructure = new ReportingStructure();
		reportingStructure.setEmployee(employee);
		reportingStructure.setNumberOfReports(getReports(employeeRepository.findByEmployeeId(id)).size());
		
		return reportingStructure;
	}
	
	/**
	 * This method looks at all direct reports and recursively looks up all of their direct reports.
	 * I decided to return a Set containing employee ids rather than just a number of reports 
	 * to prevent counting the same employee twice (a person can belong to multiple teams). 
	 * @param employee
	 * @return
	 */
	private Set<String> getReports(Employee employee) {
		Set<String> employeeReports = new HashSet<>();
		
		//if the employee doesn't have any direct reports then return an empty set
		if(employee.getDirectReports()==null) {
			return employeeReports;
		}
		
		//iterate over all direct reports and find all other reports they have
		for(Employee directReport: employee.getDirectReports()) {
			 directReport = employeeRepository.findByEmployeeId(directReport.getEmployeeId());
			
			 /*if we can't locate the direct report id in the DB then skip counting them. Depending on business requirements
				we might decide to throw an exception here (inconsistent data). */
			 if(directReport==null) {
				continue;
			}else {
				//add the direct report employee
				employeeReports.add(directReport.getEmployeeId());
				//add all report employees of the direct report employee
				employeeReports.addAll(getReports(directReport));
			}
		}
		
		return employeeReports;
	}
}