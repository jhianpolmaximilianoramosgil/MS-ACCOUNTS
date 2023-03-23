package com.nttdata.bank.account.service.impl;

import com.nttdata.bank.account.model.ClientDto;
import com.nttdata.bank.account.repository.AccountRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Setter
@Getter
@NoArgsConstructor
@Component
@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ValidateClient {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ExternalService externalService;
    private static long numberOfAccounts = 0;

    public Mono<Boolean> validateTypeClient(Integer clientId){
      Mono<ClientDto> clientDto = externalService.externalFindByClientId(clientId);

      return clientDto
          .map(cliente->{
            if (cliente.getClientType().equalsIgnoreCase("P")){
              return validateNumberOfAccountsPersonal(cliente.getClientId());
            } else {
              return validateNumberOfAccountsbusiness(cliente.getClientId());
            }
          });
    }

    private boolean validateNumberOfAccountsPersonal(Integer clientId){
      accountRepository.findAll()
          .filter(p -> p.getClientId().equals(clientId))
          .count()
          .subscribe(count-> numberOfAccounts = count);
      return (numberOfAccounts == 0 );
    }

  private  boolean validateNumberOfAccountsbusiness(Integer clientId){
    accountRepository.findAll()
        .filter(p -> p.getClientId().equals(clientId))
        .count()
        .subscribe(count-> numberOfAccounts = count);
    return (numberOfAccounts == 0 );
  }
}
