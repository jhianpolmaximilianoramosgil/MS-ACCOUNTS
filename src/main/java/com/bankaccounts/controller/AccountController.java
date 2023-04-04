package com.bankaccounts.controller;

import com.bankaccounts.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankaccounts.dto.AccountRequestDto;
import com.bankaccounts.dto.AccountResponseDto;
import com.bankaccounts.dto.Message;
import com.bankaccounts.dto.TransferRequestDto;
import com.bankaccounts.dto.TransferResponseDto;
import com.bankaccounts.model.Account;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
	
	@Autowired
    private AccountService accountService;
	

	@GetMapping
    public Flux<Account> getAll(){
		return accountService.getAll();
    }
	

	@GetMapping("/{accountId}")
    public Mono<Account> getAccountById(@PathVariable String accountId){
		return accountService.getAccountById(accountId);
    }
	

	@PostMapping("/person")
    public Mono<AccountResponseDto> createAccountPerson(@RequestBody @Valid AccountRequestDto accountRequestDto) {
		return (accountService.createAccountPerson(accountRequestDto));
    }
	

	@PostMapping("/company")
    public Mono<AccountResponseDto> createAccountCompany(@RequestBody @Valid AccountRequestDto accountRequestDto) {
		return (accountService.createAccountCompany(accountRequestDto));
    }
	

	@PutMapping
	public Mono<Account> updateAccount(@RequestBody AccountRequestDto accountRequestDto){
		return accountService.updateAccount(accountRequestDto);
    }
	

	@DeleteMapping("/{accountId}")
	public Mono<Message> deleteAccount(@PathVariable String accountId){
		return accountService.deleteAccount(accountId);
    }
	

	@PostMapping("/deposit")
    public Mono<AccountResponseDto> depositAccount(@RequestBody @Valid AccountRequestDto accountRequestDto) {
		return accountService.depositAccount(accountRequestDto);
    }
	

	@PostMapping("/withdrawal")
    public Mono<AccountResponseDto> withdrawalAccount(@RequestBody @Valid AccountRequestDto accountRequestDto) {
		return accountService.withdrawalAccount(accountRequestDto);
    }
	

	@GetMapping("/consult/{customerId}")
    public Flux<Account> getAllAccountXCustomerId(@PathVariable String customerId){
		return accountService.getAllAccountXCustomerId(customerId);
    }
	

	@PutMapping("/restartTransactions")
    public Mono<Message> restartTransactions(){
		return accountService.restartTransactions();
    }


	@PostMapping("/transfer")
    public Mono<TransferResponseDto> transferBetweenAccounts(@RequestBody @Valid TransferRequestDto transferRequestDto) {
		return accountService.transferBetweenAccounts(transferRequestDto);
    }
	

	@PostMapping("/transferthird")
    public Mono<TransferResponseDto> transferThirdParty(@RequestBody @Valid TransferRequestDto transferRequestDto) {
		return accountService.transferThirdParty(transferRequestDto);
    }
}
