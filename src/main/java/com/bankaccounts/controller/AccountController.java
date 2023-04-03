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
	
	/**
	 * Obtiene todas las cuentas
	 * @return Flux<Account>
	 */
	@GetMapping
    public Flux<Account> getAll(){
		return accountService.getAll();
    }
	
	/**
	 * Obtiene la cuenta por su id
	 * @param accountId
	 * @return Mono<Account>
	 */
	@GetMapping("/{accountId}")
    public Mono<Account> getAccountById(@PathVariable String accountId){
		return accountService.getAccountById(accountId);
    }
	
	/**
	 * Registro de una cuenta para una persona
	 * @param accountRequestDto
	 * @return Mono<AccountResponseDto>
	 */
	@PostMapping("/person")
    public Mono<AccountResponseDto> createAccountPerson(@RequestBody @Valid AccountRequestDto accountRequestDto) {
		return (accountService.createAccountPerson(accountRequestDto));
    }
	
	/**
	 * Registro de una cuenta para una empresa
	 * @param accountRequestDto
	 * @return Mono<AccountResponseDto>
	 */
	@PostMapping("/company")
    public Mono<AccountResponseDto> createAccountCompany(@RequestBody @Valid AccountRequestDto accountRequestDto) {
		return (accountService.createAccountCompany(accountRequestDto));
    }
	
	/**
	 * Actualización de una cuenta
	 * @param accountRequestDto
	 * @return Mono<Account>
	 */
	@PutMapping
	public Mono<Account> updateAccount(@RequestBody AccountRequestDto accountRequestDto){
		return accountService.updateAccount(accountRequestDto);
    }
	
	/**
	 * Eliminación de una cuenta
	 * @param accountId
	 * @return Mono<Message>
	 */
	@DeleteMapping("/{accountId}")
	public Mono<Message> deleteAccount(@PathVariable String accountId){
		return accountService.deleteAccount(accountId);
    }
	
	/**
	 * Depósito de una cuenta
	 * @param accountRequestDto
	 * @return Mono<AccountResponseDto>
	 */
	@PostMapping("/deposit")
    public Mono<AccountResponseDto> depositAccount(@RequestBody @Valid AccountRequestDto accountRequestDto) {
		return accountService.depositAccount(accountRequestDto);
    }
	
	/**
	 * Retiro de una cuenta
	 * @param accountRequestDto
	 * @return Mono<AccountResponseDto>
	 */
	@PostMapping("/withdrawal")
    public Mono<AccountResponseDto> withdrawalAccount(@RequestBody @Valid AccountRequestDto accountRequestDto) {
		return accountService.withdrawalAccount(accountRequestDto);
    }
	
	/**
	 * Obtiene todas la cuentas por el id del cliente
	 * @param customerId
	 * @return Flux<Account>
	 */
	@GetMapping("/consult/{customerId}")
    public Flux<Account> getAllAccountXCustomerId(@PathVariable String customerId){
		return accountService.getAllAccountXCustomerId(customerId);
    }
	
	/**
	 * Reiniciar el numero de movimientos de las cuentas
	 * @return Mono<Message>
	 */
	@PutMapping("/restartTransactions")
    public Mono<Message> restartTransactions(){
		return accountService.restartTransactions();
    }

	/**
	 * Transferencia entre propias cuentas
	 * @param transferRequestDto
	 * @return Mono<TransferResponseDto>
	 */
	@PostMapping("/transfer")
    public Mono<TransferResponseDto> transferBetweenAccounts(@RequestBody @Valid TransferRequestDto transferRequestDto) {
		return accountService.transferBetweenAccounts(transferRequestDto);
    }
	
	/**
	 * Transferencia entre cuenta de terceros
	 * @param transferRequestDto
	 * @return Mono<TransferResponseDto>
	 */
	@PostMapping("/transferthird")
    public Mono<TransferResponseDto> transferThirdParty(@RequestBody @Valid TransferRequestDto transferRequestDto) {
		return accountService.transferThirdParty(transferRequestDto);
    }
}
