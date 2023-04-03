package com.bootcamp.bankaccounts.repository;

import com.bootcamp.bankaccounts.entity.Yanki;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface YankiRepository extends ReactiveMongoRepository<Yanki, String> {

}
