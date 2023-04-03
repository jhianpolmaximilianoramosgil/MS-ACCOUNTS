package com.bankaccounts.service;

import com.bankaccounts.dto.AccountRequestDto;
import com.bankaccounts.dto.AccountResponseDto;
import com.bankaccounts.dto.Message;
import com.bankaccounts.dto.TransferRequestDto;
import com.bankaccounts.dto.TransferResponseDto;
import com.bankaccounts.model.Account;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {

	Flux<Account> getAll();

	Mono<Account> getAccountById(String accountId);

	Mono<AccountResponseDto> createAccountPerson(AccountRequestDto accountRequestDto);
	
	Mono<AccountResponseDto> createAccountCompany(AccountRequestDto accountRequestDto);

	Mono<Account> updateAccount(AccountRequestDto accountRequestDto);

	Mono<Message> deleteAccount(String accountId);
	
	Mono<AccountResponseDto> depositAccount(AccountRequestDto accountRequestDto);
	
	Mono<AccountResponseDto> withdrawalAccount(AccountRequestDto accountRequestDto);
	
	Flux<Account> getAllAccountXCustomerId(String customerId);
	
	Mono<Message> restartTransactions();
	
	Mono<TransferResponseDto> transferBetweenAccounts(TransferRequestDto transferRequestDto);
	
	Mono<TransferResponseDto> transferThirdParty(TransferRequestDto transferRequestDto);

}
