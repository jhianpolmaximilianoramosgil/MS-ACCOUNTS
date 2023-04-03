package com.bankaccounts.dto;

import lombok.Data;

@Data
public class TransferRequestDto1 {
	private String originYanki;
	private String destinationYanki;
	private Double amount;
}
