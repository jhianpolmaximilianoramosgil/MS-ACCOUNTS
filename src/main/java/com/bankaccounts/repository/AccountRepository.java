package com.bankaccounts.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.bankaccounts.model.Account;

public interface AccountRepository extends ReactiveMongoRepository<Account, String> {

}
