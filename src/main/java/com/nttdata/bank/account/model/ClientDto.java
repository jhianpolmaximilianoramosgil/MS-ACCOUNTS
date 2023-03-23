package com.nttdata.bank.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDto {

  private Integer clientId;
  private String clientName;
  private String clientType; //Personal & Empresarial (P-E)
  private String codProfile;//Personal(VIP) - Empresarial(PYME)
  private String clientDocument;

}
