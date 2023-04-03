package com.bankaccounts.controller;

import com.bankaccounts.dto.*;
import com.bankaccounts.model.Yanki;
import com.bootcamp.bankaccounts.dto.*;
import com.bankaccounts.service.YankiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/yanki")
@RequiredArgsConstructor
public class YankiController {
	
	@Autowired
    private YankiService yankiService;
	
	/**
	 * Obtiene todas los yanki
	 * @return Flux<Yanki>
	 */
	@GetMapping
    public Flux<Yanki> getAll(){
		return yankiService.getAll();
    }
	
	/**
	 * Obtiene yanki por su id
	 * @param yankiId
	 * @return Mono<Yanki>
	 */
	@GetMapping("/{accountId}")
    public Mono<Yanki> getAccountById(@PathVariable String yankiId){
		return yankiService.getAccountById(yankiId);
    }
	
	/**
	 * Registro de un yanki para una persona
	 * @param yankiRequestDto
	 * @return Mono<YankiResponseDto>
	 */
	@PostMapping("/person")
    public Mono<YankiResponseDto> createYankiPerson(@RequestBody @Valid YankiRequestDto yankiRequestDto) {
		return (yankiService.createYankiPerson(yankiRequestDto));
    }
	
	/**
	 * Registro de una cuenta para una empresa
	 * @param accountRequestDto
	 * @return Mono<AccountResponseDto>
	 */
	@PostMapping("/company")
    public Mono<AccountResponseDto> createAccountCompany(@RequestBody @Valid AccountRequestDto accountRequestDto) {
		return (yankiService.createAccountCompany(accountRequestDto));
    }
	
	/**
	 * Actualización de una cuenta
	 * @param accountRequestDto
	 * @return Mono<Account>
	 */
	@PutMapping
	public Mono<Yanki> updateAccount(@RequestBody AccountRequestDto accountRequestDto){
		return yankiService.updateAccount(accountRequestDto);
    }
	
	/**
	 * Eliminación de una cuenta
	 * @param accountId
	 * @return Mono<Message>
	 */
	@DeleteMapping("/{accountId}")
	public Mono<Message> deleteAccount(@PathVariable String accountId){
		return yankiService.deleteAccount(accountId);
    }
	
	/**
	 * Depósito de una cuenta
	 * @param accountRequestDto
	 * @return Mono<AccountResponseDto>
	 */
	@PostMapping("/deposit")
    public Mono<AccountResponseDto> depositAccount(@RequestBody @Valid AccountRequestDto accountRequestDto) {
		return yankiService.depositAccount(accountRequestDto);
    }
	
	/**
	 * Retiro de una cuenta
	 * @param accountRequestDto
	 * @return Mono<AccountResponseDto>
	 */
	@PostMapping("/withdrawal")
    public Mono<AccountResponseDto> withdrawalAccount(@RequestBody @Valid AccountRequestDto accountRequestDto) {
		return yankiService.withdrawalAccount(accountRequestDto);
    }
	
	/**
	 * Obtiene todas la cuentas por el id del cliente
	 * @param customerId
	 * @return Flux<Account>
	 */
	@GetMapping("/consult/{customerId}")
    public Flux<Yanki> getAllAccountXCustomerId(@PathVariable String customerId){
		return yankiService.getAllAccountXCustomerId(customerId);
    }
	
	/**
	 * Reiniciar el numero de movimientos de las cuentas
	 * @return Mono<Message>
	 */
	@PutMapping("/restartTransactions")
    public Mono<Message> restartTransactions(){
		return yankiService.restartTransactions();
    }

	/**
	 * Transferencia entre propias cuentas
	 * @param transferRequestDto
	 * @return Mono<TransferResponseDto>
	 */
	@PostMapping("/transfer")
    public Mono<TransferResponseDto> transferBetweenAccounts(@RequestBody @Valid TransferRequestDto transferRequestDto) {
		return yankiService.transferBetweenAccounts(transferRequestDto);
    }
	
	/**
	 * Transferencia entre cuenta de terceros
	 * @param transferRequestDto
	 * @return Mono<TransferResponseDto>
	 */
	@PostMapping("/transferthird")
    public Mono<TransferResponseDto> transferThirdParty(@RequestBody @Valid TransferRequestDto transferRequestDto) {
		return yankiService.transferThirdParty(transferRequestDto);
    }
}
