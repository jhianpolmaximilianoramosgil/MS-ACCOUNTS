package com.bankaccounts.repository;

import com.bankaccounts.model.Yanki;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface YankiRepository extends ReactiveMongoRepository<Yanki, String> {

}
