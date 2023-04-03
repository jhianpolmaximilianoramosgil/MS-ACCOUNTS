package com.bootcamp.bankaccounts.dto;

import com.bootcamp.bankaccounts.entity.Yanki;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TransferResponseDto1 {
	private String message;
	private List<Yanki> yanki;
}
