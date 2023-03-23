package com.nttdata.bank.account.service;


import com.nttdata.bank.account.model.Account;
import com.nttdata.bank.account.model.AccountDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {

  Flux<Account> getAll();

  Mono<Account> save(AccountDto accountDto);

  Mono<Account> update(AccountDto accountDto);

  Mono<Void> delete(Integer accountId);

  Mono<Account> findById(Integer accountId);

  Flux<Account> findByClientId(Integer clientId);

  Mono<Account> findByAccountNumber(String accountNumber);

  Flux<Object> getBalanceByClientId (Integer clientId);

  Mono<Account> UpdBalance(Integer accountId, Float amount);

}