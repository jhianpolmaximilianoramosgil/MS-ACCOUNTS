package com.bankaccounts.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
public class YankiRequestDto {
	private String id;
	private String customerId;
	@Min(value = 0,message = "0:AHORRO, 1:C_CORRIENTE, 2:PLAZO_FIJO")
	@Max(value = 2,message = "0:YANKI, 1:C_CORRIENTE, 2:PLAZO_FIJO")
	private Integer typeYanki;
	private String descripTypeYanki;
	private Double amount;

	private Double maintenance;
	private Integer transaction;
	private Integer operationDay;
	private LocalDateTime dateAccount;
	private String telephoneYanki;
	private String typeCustomer;


}
