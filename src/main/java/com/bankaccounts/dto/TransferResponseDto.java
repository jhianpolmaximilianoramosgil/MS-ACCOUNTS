package com.bankaccounts.dto;

import java.util.List;

import com.bankaccounts.model.Account;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TransferResponseDto {
	private String message;
	private List<Account> account;
}
