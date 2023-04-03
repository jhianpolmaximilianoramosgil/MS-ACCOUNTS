package com.bootcamp.bankaccounts.service;

import com.bootcamp.bankaccounts.dto.YankiRequestDto;
import com.bootcamp.bankaccounts.dto.YankiResponseDto;
import com.bootcamp.bankaccounts.dto.Message;

import com.bootcamp.bankaccounts.dto.TransferRequestDto1;
import com.bootcamp.bankaccounts.dto.TransferResponseDto1;
import com.bootcamp.bankaccounts.entity.Yanki;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface YankiService {

	Flux<Yanki> getAll();

	Mono<Yanki> getYankiById(String yankiId);

	Mono<YankiResponseDto> createAccountPerson(YankiRequestDto yankiRequestDto);
	
	Mono<YankiResponseDto> createYankiCompany(YankiRequestDto yankiRequestDto);

	Mono<Yanki> updateYanki(YankiRequestDto yankiRequestDto);

	Mono<Message> deleteYanki(String yankiId);
	
	Mono<YankiResponseDto> depositYanki(YankiRequestDto yankiRequestDto);
	
	Mono<YankiResponseDto> withdrawalYanki(YankiRequestDto yankiRequestDto);
	
	Flux<Yanki> getAllYankiXCustomerId(String customerId);
	
	Mono<Message> restartTransactions();
	
	Mono<TransferResponseDto1> transferBetweenYanki(TransferRequestDto1 transferRequestDto1);
	
	Mono<TransferResponseDto1> transferThirdParty(TransferRequestDto1 transferRequestDto1);

}
