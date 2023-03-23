package com.nttdata.bank.account.service.impl;


import com.nttdata.bank.account.model.ClientDto;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@NoArgsConstructor
@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ExternalService {

  @Autowired
  public WebClient.Builder webClientBuilder;

    public Mono<ClientDto> externalFindByClientId(Integer clientId) {
      return webClientBuilder.baseUrl("http://localhost:8084")
        .build()
        .get()
        .uri("/clients/{clientId}", clientId)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(ClientDto.class);
  }

  }

