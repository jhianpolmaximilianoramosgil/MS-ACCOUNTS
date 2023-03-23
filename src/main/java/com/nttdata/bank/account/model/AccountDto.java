package com.nttdata.bank.account.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AccountDto {
  private Integer accountId;
  private String accountNumber;
  private Integer clientId;
  private Integer productId;
  private Float accountBalance;
  private List<String> accountHeadlines = new ArrayList<>();
  private List<String> accountSignatories = new ArrayList<>();
}