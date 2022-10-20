package com.mindex.challenge.data;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class Compensation {
	private String compensationId;
	@NotNull
	private String employeeId;
	private double salary;
	private LocalDate effectiveDate;
}
