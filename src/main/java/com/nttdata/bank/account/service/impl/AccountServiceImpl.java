package com.nttdata.bank.account.service.impl;

import com.nttdata.bank.account.service.AccountService;
import com.nttdata.bank.account.model.Account;
import com.nttdata.bank.account.model.AccountDto;
import com.nttdata.bank.account.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
  @Autowired
  private AccountRepository accountRepository;
  @Autowired
  private Mapper mapper;
  @Autowired
  private ValidateClient validateClient;

  @Override
  public Flux<Account> getAll() {
     return accountRepository
         .findAll()
         .switchIfEmpty(Flux.empty());
  }

  @Override
  @Transactional()
  public Mono<Account> save(AccountDto accountDto) {
    return accountRepository
           .existsById(accountDto.getClientId())
           .flatMap(isExist->{
              if (!isExist) {
                return validateClient.validateTypeClient(accountDto.getClientId());
              } else {
                return Mono.just(false);
              }
           })
          .flatMap(isTrue->{
            if (isTrue) {
              return accountRepository.save(mapper.map(accountDto, Account.class));
            }
            return Mono.empty();
          });
  }

  @Override
  public Mono<Account> update(AccountDto accountDto) {
    return accountRepository.findById(accountDto.getAccountId())
        .map(c -> mapper.map(accountDto, Account.class))
        .flatMap(accountRepository::save)
        .switchIfEmpty(Mono.empty());
  }

  @Override
  public Mono<Void> delete(Integer accountId) {
    return accountRepository
        .findById(accountId)
        .flatMap(a -> accountRepository.deleteById(a.getAccountId()))
        .switchIfEmpty(Mono.empty());
  }

  @Override
  public Mono<Account> findById(Integer accountId) {
    return accountRepository
        .findById(accountId);
  }

  @Override
  public Flux<Account> findByClientId(Integer clientId) {
    return accountRepository.findAll()
        .filter(p -> p.getClientId().equals(clientId))
        .switchIfEmpty(Mono.empty());
  }

  @Override
  public Mono<Account> findByAccountNumber(String accountNumber) {
    return this.accountRepository.findAll()
        .filter(t -> t.getAccountNumber().equals(accountNumber))
        .elementAt(0);
  }

  @Override
  public Flux<Object> getBalanceByClientId(Integer clientId) {
    return this.accountRepository
        .findAll()
        .filter(p -> p.getClientId().equals(clientId))
        .flatMap(x -> {
          Map<String, String> map = new HashMap<>();
          map.put("accountNumber", x.getAccountNumber());
          map.put("accountBalance", String.valueOf(x.getAccountBalance()));
          return Mono.just(map);
        });
  }

  @Override
  public Mono<Account> UpdBalance(Integer accountId, Float amount) {
    return accountRepository
        .findById(accountId)
        .doOnNext(e->e.setAccountBalance(e.getAccountBalance() + amount))
        .flatMap(accountRepository::save)
        .switchIfEmpty(Mono.empty());
  }





}
