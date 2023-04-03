package com.bankaccounts.dto;

import lombok.Data;

@Data
public class AuthorizedDto {
	private String name;
	private String lastName;
	private String dni;
	private String email;
	private String telephone;
}
