package com.bankaccounts.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.bankaccounts.clients.TransactionsRestClient;
import com.bankaccounts.repository.AccountRepository;
import com.bankaccounts.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankaccounts.clients.CustomerRestClient;
import com.bankaccounts.dto.AccountRequestDto;
import com.bankaccounts.dto.AccountResponseDto;
import com.bankaccounts.dto.Message;
import com.bankaccounts.dto.Transaction;
import com.bankaccounts.dto.TransferRequestDto;
import com.bankaccounts.dto.TransferResponseDto;
import com.bankaccounts.dto.TypeAccountDto;
import com.bankaccounts.model.Account;
import com.bankaccounts.util.TypeAccount;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
	
	private final TypeAccount typeAccount = new TypeAccount();

	@Autowired
    private AccountRepository accountRepository;
	
	@Autowired
    CustomerRestClient customerRestClient;
	
	@Autowired
	TransactionsRestClient transactionRestClient;
	

	@Override
	public Flux<Account> getAll() {
		return accountRepository.findAll();
	}


	@Override
	public Mono<Account> getAccountById(String accountId) {
		return accountRepository.findById(accountId);
	}


	@Override
	public Mono<AccountResponseDto> createAccountPerson(AccountRequestDto accountRequestDto) {
		TypeAccountDto newAccount = getTypeAccount(accountRequestDto.getTypeAccount());
		Account account = new Account(null,accountRequestDto.getCustomerId(),accountRequestDto.getTypeAccount(),newAccount.getType(), accountRequestDto.getAmount()
				, accountRequestDto.getAmount(), newAccount.getMaintenance(), newAccount.getTransactions(), newAccount.getDayOperation()
				, LocalDateTime.now(), accountRequestDto.getNumberAccount(), accountRequestDto.getTypeCustomer(), newAccount.getCommission());
		return customerRestClient.getPersonById(accountRequestDto.getCustomerId()).flatMap(c ->{
			account.setTypeCustomer(c.getTypeCustomer());
			return getAccountByIdCustomerPerson(accountRequestDto.getCustomerId(),newAccount.getType()).flatMap(v -> {
				return Mono.just(new AccountResponseDto("Personal client already has a bank account: "+newAccount.getType(), null));
			}).switchIfEmpty(saveNewAccount(account));
		}).defaultIfEmpty(new AccountResponseDto("Client does not exist", null));
	}
	

	@Override
	public Mono<AccountResponseDto> createAccountCompany(AccountRequestDto accountRequestDto) {
		TypeAccountDto newAccount = getTypeAccount(accountRequestDto.getTypeAccount());
		
		Account account = new Account(null,accountRequestDto.getCustomerId(),accountRequestDto.getTypeAccount(),newAccount.getType(), accountRequestDto.getAmount()
				, accountRequestDto.getAmount(), newAccount.getMaintenance(), newAccount.getTransactions(), newAccount.getDayOperation()
				, LocalDateTime.now(), accountRequestDto.getNumberAccount(), accountRequestDto.getTypeCustomer(), newAccount.getCommission());	
		return customerRestClient.getCompanyById(accountRequestDto.getCustomerId()).flatMap(c ->{
			account.setTypeCustomer(c.getTypeCustomer());
			if(newAccount.getType().equals("C_CORRIENTE")) {
				return saveNewAccount(account);
			}
			return Mono.just(new AccountResponseDto("For company only type of account: C_CORRIENTE", null));
		}).defaultIfEmpty(new AccountResponseDto("Client does not exist", null));
	}


	@Override
	public Mono<Account> updateAccount(AccountRequestDto accountRequestDto) {
		return accountRepository.findById(accountRequestDto.getId())
                .flatMap(uAccount -> {
                	uAccount.setCustomerId(accountRequestDto.getCustomerId());
                	uAccount.setTypeAccount(accountRequestDto.getTypeAccount());
                	uAccount.setDescripTypeAccount(getTypeAccount(accountRequestDto.getTypeAccount()).getType());
                	uAccount.setAmount(accountRequestDto.getAmount());
                	uAccount.setMaintenance(accountRequestDto.getMaintenance());
                	uAccount.setTransaction(accountRequestDto.getTransaction());
                	uAccount.setOperationDay(accountRequestDto.getOperationDay());
                	uAccount.setDateAccount(accountRequestDto.getDateAccount());
                	uAccount.setNumberAccount(accountRequestDto.getNumberAccount());
                	uAccount.setTypeCustomer(accountRequestDto.getTypeCustomer());
                    return accountRepository.save(uAccount);
        });
	}


	@Override
	public Mono<Message> deleteAccount(String accountId) {
		Message message = new Message("Account does not exist");
		return accountRepository.findById(accountId)
                .flatMap(dAccount -> {
                	message.setMessage("Account deleted successfully");
                	return accountRepository.deleteById(dAccount.getId()).thenReturn(message);
        }).defaultIfEmpty(message);
	}
	

	@Override
	public Mono<AccountResponseDto> depositAccount(AccountRequestDto accountRequestDto) {
		LocalDateTime myDateObj = LocalDateTime.now();
		return accountRepository.findById(accountRequestDto.getId()).flatMap(uAccount -> {
			Double amount = uAccount.getAmount() + accountRequestDto.getAmount();
			Double commission = uAccount.getTransaction() > 0 ? (0.0):(uAccount.getCommission());
			if(amount < uAccount.getCommission()) return Mono.just(new AccountResponseDto("The operation cannot be carried out for an account amount less than the commission", null));
			if(uAccount.getDescripTypeAccount().equals("PLAZO_FIJO") && uAccount.getOperationDay()!=myDateObj.getDayOfMonth()) {
				return Mono.just(new AccountResponseDto("Day of the month not allowed for PLAZO_FIJO", null));
			}
			uAccount.setAmount(amount-commission);
			uAccount.setTransaction(uAccount.getTransaction()>0?(uAccount.getTransaction() - 1):(0));
            return accountRepository.save(uAccount).flatMap(account -> {
            	return registerTransaction(uAccount, accountRequestDto.getAmount(),"DEPOSITO", uAccount.getTransaction() > 0 ? 0:uAccount.getCommission());
            });
        }).defaultIfEmpty(new AccountResponseDto("Account does not exist", null));
	}


	@Override
	public Mono<AccountResponseDto> withdrawalAccount(AccountRequestDto accountRequestDto) {
		LocalDateTime myDateObj = LocalDateTime.now();
		return accountRepository.findById(accountRequestDto.getId()).flatMap(uAccount -> {
			Double amount = uAccount.getAmount() - accountRequestDto.getAmount();
			Double commission = uAccount.getTransaction() > 0 ? (0.0):(uAccount.getCommission());
			if(amount >= 0) {
				if(uAccount.getDescripTypeAccount().equals("PLAZO_FIJO") && uAccount.getOperationDay()!=myDateObj.getDayOfMonth()) {
					return Mono.just(new AccountResponseDto("Day of the month not allowed for PLAZO_FIJO", null));
				}
				if(amount < uAccount.getCommission()) return Mono.just(new AccountResponseDto("The operation cannot be carried out for an account amount less than the commission", null));
				uAccount.setAmount(amount-commission);
				uAccount.setTransaction(uAccount.getTransaction()>0?(uAccount.getTransaction() - 1):(0));
	            return accountRepository.save(uAccount).flatMap(account -> {
	            	return registerTransaction(uAccount, accountRequestDto.getAmount(),"RETIRO", uAccount.getTransaction()>0?0:uAccount.getCommission());
	            });
			}
			return Mono.just(new AccountResponseDto("You don't have enough balance", null));
        }).defaultIfEmpty(new AccountResponseDto("Account does not exist", null));
	}


	@Override
	public Flux<Account> getAllAccountXCustomerId(String customerId) {
		return accountRepository.findAll()
				.filter(c -> c.getCustomerId().equals(customerId));
	}
	

	@Override
	public Mono<Message> restartTransactions() {
		return updateTransaction().collectList().flatMap(c -> {
			return Mono.just(new Message("The number of transactions of the accounts was satisfactorily restarted"));
		});
	}
	

	private TypeAccountDto getTypeAccount(Integer idType) {
		Predicate<TypeAccountDto> p = f -> f.getId()==idType;
		TypeAccountDto type = typeAccount.getAccounts().filter(p).collect(Collectors.toList()).get(0);
		return type;
    }


	private Mono<Account> getAccountByIdCustomerPerson(String customerId, String type) {
		return  accountRepository.findAll()
				.filter(c -> c.getCustomerId().equals(customerId))
				.filter(c -> c.getDescripTypeAccount().equals(type))
				.next();
	}
	

	private Mono<AccountResponseDto> saveNewAccount(Account account) {
		return accountRepository.save(account).flatMap(x -> {
			return registerTransaction(account, account.getAmount(),"APERTURA", 0.0).flatMap(t1 -> {
				return Mono.just(new AccountResponseDto("Account created successfully", x));
			});
		});
	}
	

	private Mono<AccountResponseDto> registerTransaction(Account uAccount, Double amount, String typeTransaction, Double commission){
		Transaction transaction = new Transaction();
		transaction.setCustomerId(uAccount.getCustomerId());
		transaction.setProductId(uAccount.getId());
		transaction.setProductType(uAccount.getDescripTypeAccount());
		transaction.setTransactionType(typeTransaction);
		transaction.setAmount(amount);
		transaction.setTransactionDate(LocalDateTime.now());
		transaction.setCustomerType(uAccount.getTypeCustomer());
		transaction.setBalance(uAccount.getAmount());
		return transactionRestClient.createTransaction(transaction).flatMap(t -> {
			if(commission>0) {
				transaction.setAmount(commission);
				transaction.setTransactionType("COMISION");
				return transactionRestClient.createTransaction(transaction).flatMap(d -> {
					return Mono.just(new AccountResponseDto("Successful transaction", uAccount));
				});
			}
			return Mono.just(new AccountResponseDto("Successful transaction", uAccount));
        });
	}
	

	private Flux<Account> updateTransaction(){
		return  accountRepository.findAll()
				.flatMap(c -> {
					c.setTransaction(getTypeAccount(c.getTypeAccount()).getTransactions());
					return accountRepository.save(c);
				});
	}

	@Override
	public Mono<TransferResponseDto> transferBetweenAccounts(TransferRequestDto transferRequestDto) {
		return accountRepository.findById(transferRequestDto.getOriginAccount()).flatMap(a -> {
			return accountRepository.findById(transferRequestDto.getDestinationAccount()).filter(c -> !c.getId().equals(a.getId())).flatMap(b -> {
				if(a.getCustomerId().equals(b.getCustomerId())) {
					if(a.getAmount() - transferRequestDto.getAmount() < 0)
						return Mono.just(new TransferResponseDto("You do not have a sufficient balance in your origin account", null));
					a.setAmount(a.getAmount() - transferRequestDto.getAmount());
					b.setAmount(b.getAmount() + transferRequestDto.getAmount());
		            return accountRepository.save(a).flatMap(account1 -> {
		            	return accountRepository.save(b).flatMap(account2 -> {
		            		return registerTransaction(account1, transferRequestDto.getAmount(),"TRANSFERENCIA ENTRE CUENTAS - RETIRO", 0.0).flatMap(t1 -> {
		            			return registerTransaction(account2, transferRequestDto.getAmount(),"TRANSFERENCIA ENTRE CUENTAS - DEPOSITO", 0.0).flatMap(t2 -> {
		            				return Mono.just(new TransferResponseDto("Successful transaction between accounts", Arrays.asList(a,b)));
		            			});
		            		});
		            	});
		            });
				}else {
					return Mono.just(new TransferResponseDto("Accounts are not from the same client", null));
				}
			}).defaultIfEmpty(new TransferResponseDto("Destination Account does not exist, enter another account", null));
		}).defaultIfEmpty(new TransferResponseDto("Origin Account does not exist", null));
	}

	@Override
	public Mono<TransferResponseDto> transferThirdParty(TransferRequestDto transferRequestDto) {
		return accountRepository.findById(transferRequestDto.getOriginAccount()).flatMap(a -> {
			return accountRepository.findById(transferRequestDto.getDestinationAccount()).filter(c -> !(c.getCustomerId().equals(a.getCustomerId()))).flatMap(b -> {
				if(a.getAmount() - transferRequestDto.getAmount() < 0)
					return Mono.just(new TransferResponseDto("You do not have a sufficient balance in your origin account", null));
				a.setAmount(a.getAmount() - transferRequestDto.getAmount());
				b.setAmount(b.getAmount() + transferRequestDto.getAmount());
	            return accountRepository.save(a).flatMap(account1 -> {
	            	return accountRepository.save(b).flatMap(account2 -> {
	            		return registerTransaction(account1, transferRequestDto.getAmount(),"TRANSFERENCIA A TERCEROS - RETIRO", 0.0).flatMap(t1 -> {
	            			return registerTransaction(account2, transferRequestDto.getAmount(),"TRANSFERENCIA A TERCEROS - DEPOSITO", 0.0).flatMap(t2 -> {
	            				return Mono.just(new TransferResponseDto("successful third-party account transaction", Arrays.asList(a,b)));
	            			});
	            		});
	            	});
	            });
			}).defaultIfEmpty(new TransferResponseDto("Destination Account does not exist, enter another account", null));
		}).defaultIfEmpty(new TransferResponseDto("Origin Account does not exist", null));
	}
	
}
