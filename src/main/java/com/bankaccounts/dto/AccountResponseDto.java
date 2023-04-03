package com.bankaccounts.dto;

import com.bankaccounts.model.Account;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class AccountResponseDto {
	private String message;
	private Account account;
}
