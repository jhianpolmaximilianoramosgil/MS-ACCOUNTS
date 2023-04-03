package com.bankaccounts.service.impl;

import com.bankaccounts.clients.CustomerRestClient;
import com.bankaccounts.clients.TransactionsRestClient;
import com.bankaccounts.dto.*;
import com.bankaccounts.model.Yanki;
import com.bootcamp.bankaccounts.dto.*;
import com.bankaccounts.repository.YankiRepository;
import com.bankaccounts.service.YankiService;
import com.bankaccounts.util.TypeYanki;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class YankiServiceImpl implements YankiService{
	
	private final TypeYanki typeYanki = new TypeYanki();

	@Autowired
    private YankiRepository yankiRepository;
	
	@Autowired
	CustomerRestClient customerRestClient;
	
	@Autowired
	TransactionsRestClient transactionRestClient;
	
	/**
	 * Obtiene todas los monederos Yanki
	 * @return Flux<Yanki>
	 */
	@Override
	public Flux<Yanki> getAll() {
		return yankiRepository.findAll();
	}

	/**
	 * Obtiene la yanki por su id
	 * @param yankiId
	 * @return Mono<Yanki>
	 */
	@Override
	public Mono<Yanki> getAccountById(String yankiId) {
		return yankiRepository.findById(yankiId);
	}

	/**
	 * Registro de una yanki para una persona
	 * Se obtiene los movimientos y mantenimioento según el tipo de cuenta en getTypeAccount()
	 * Se busca al cliente getPersonById() y se valida si ya tiene una cuenta en getAccountByIdCustomerPerson()
	 * Si no tiene cuenta se crea la cuenta saveNewYanki()
	 * @param yankiRequestDto
	 * @return Mono<YankiResponseDto>
	 */
	@Override
	public Mono<YankiResponseDto> createYankiPerson(YankiRequestDto yankiRequestDto) {
		TypeYankiDto newYanki = getTypeYanki(yankiRequestDto.getTypeYanki());
		Yanki yanki = new Yanki(null,yankiRequestDto.getCustomerId(),yankiRequestDto.getTypeYanki(),newYanki.getType(), yankiRequestDto.getAmount()
				, yankiRequestDto.getAmount(), newYanki.getMaintenance(), newYanki.getTransactions(), newYanki.getDayOperation()
				, LocalDateTime.now(), yankiRequestDto.getTelephoneYanki(), yankiRequestDto.getTypeCustomer(), newYanki.getCommission());
		return customerRestClient.getPersonById(yankiRequestDto.getCustomerId()).flatMap(c ->{
			yanki.setTypeCustomer(c.getTypeCustomer());
			return getAccountByIdCustomerPerson(yankiRequestDto.getCustomerId(),newYanki.getType()).flatMap(v -> {
				return Mono.just(new YankiResponseDto("Personal client already has a bank yanki: "+newYanki.getType(), null));
			}).switchIfEmpty(saveNewYanki(yanki));
		}).defaultIfEmpty(new YankiResponseDto("Client does not exist", null));
	}
	
	/**
	 * Registro de una cuenta para una empresa
	 * Se busca al cliente getCompanyById()
	 * Se valida el tipo de cuenta y se crea la cuenta saveNewAccount()
	 * @param yankiRequestDto
	 * @return Mono<AccountResponseDto>
	 */
	@Override
	public Mono<YankiResponseDto> createYankiCompany(YankiRequestDto yankiRequestDto) {
		TypeYankiDto newYanki = getTypeYanki(yankiRequestDto.getTypeYanki());
		
		Yanki yanki = new Yanki(null,yankiRequestDto.getCustomerId(),yankiRequestDto.getTypeYanki(),newYanki.getType(), yankiRequestDto.getAmount()
				, yankiRequestDto.getAmount(), newYanki.getMaintenance(), newYanki.getTransactions(), newYanki.getDayOperation()
				, LocalDateTime.now(), yankiRequestDto.getTelephoneYanki(), yankiRequestDto.getTypeCustomer(), newYanki.getCommission());
		return customerRestClient.getCompanyById(yankiRequestDto.getCustomerId()).flatMap(c ->{
			yanki.setTypeCustomer(c.getTypeCustomer());
			if(newYanki.getType().equals("YANKI")) {
				return saveNewYanki(yanki);
			}
			return Mono.just(new YankiResponseDto("For company only type of yanki: YANKI", null));
		}).defaultIfEmpty(new YankiResponseDto("Client does not exist", null));
	}

	/**
	 * Actualización de una cuenta
	 * Se obtiene una cuenta por el id findById()
	 * Se guarda la cuenta save()
	 * @param yankiRequestDto
	 * @return Mono<Account>
	 */
	@Override
	public Mono<Yanki> updateYanki(YankiRequestDto yankiRequestDto) {
		return yankiRepository.findById(yankiRequestDto.getId())
                .flatMap(uAccount -> {
                	uAccount.setCustomerId(yankiRequestDto.getCustomerId());
                	uAccount.setTypeYanki(yankiRequestDto.getTypeYanki());
                	uAccount.setDescripTypeYanki(getTypeYanki(yankiRequestDto.getTypeYanki()).getType());
                	uAccount.setAmount(yankiRequestDto.getAmount());
                	uAccount.setMaintenance(yankiRequestDto.getMaintenance());
                	uAccount.setTransaction(yankiRequestDto.getTransaction());
                	uAccount.setOperationDay(yankiRequestDto.getOperationDay());
                	uAccount.setDateYanki(yankiRequestDto.getDateAccount());
                	uAccount.setTelephoneYanki(yankiRequestDto.getTelephoneYanki());
                	uAccount.setTypeCustomer(yankiRequestDto.getTypeCustomer());
                    return yankiRepository.save(uAccount);
        });
	}

	/**
	 * Eliminación de una cuenta
	 * Se obtiene una cuenta por el id findById()
	 * Se elimina la cuenta deleteById()
	 * @param yankiId
	 * @return Mono<Message>
	 */
	@Override
	public Mono<Message> deleteYanki(String yankiId) {
		Message message = new Message("Yanki does not exist");
		return yankiRepository.findById(yankiId)
                .flatMap(dAccount -> {
                	message.setMessage("Yanki deleted successfully");
                	return yankiRepository.deleteById(dAccount.getId()).thenReturn(message);
        }).defaultIfEmpty(message);
	}

	/**
	 * Depósito de una cuenta
	 * Se obtiene una cuenta por su id findById() y valida el numero de movimientos y comision
	 * Se actualiza la cuenta con el deposito realizado save()
	 * Se registra la transacción y si hay comusion se registra también registerTransaction() 
	 * @param yankiRequestDto
	 * @return Mono<YankiRequestDto>
	 */
	@Override
	public Mono<YankiResponseDto> depositYanki(YankiRequestDto yankiRequestDto) {
		LocalDateTime myDateObj = LocalDateTime.now();
		return yankiRepository.findById(yankiRequestDto.getId()).flatMap(uYanki -> {
			Double amount = uYanki.getAmount() + yankiRequestDto.getAmount();
			Double commission = uYanki.getTransaction() > 0 ? (0.0):(uYanki.getCommission());
			if(amount < uYanki.getCommission()) return Mono.just(new YankiResponseDto("The operation cannot be carried out for an account amount less than the commission", null));
			if(uYanki.getDescripTypeYanki().equals("YANKI") && uYanki.getOperationDay()!=myDateObj.getDayOfMonth()) {
				return Mono.just(new YankiResponseDto("Day of the month not allowed for PLAZO_FIJO", null));
			}
			uYanki.setAmount(amount-commission);
			uYanki.setTransaction(uYanki.getTransaction()>0?(uYanki.getTransaction() - 1):(0));
            return yankiRepository.save(uYanki).flatMap(yanki -> {
            	return registerTransaction(uYanki, yankiRequestDto.getAmount(),"DEPOSITO", uYanki.getTransaction() > 0 ? 0:uYanki.getCommission());
            });
        }).defaultIfEmpty(new YankiResponseDto("Yanki does not exist", null));
	}

	/**
	 * Retiro de dinero en Yanki
	 * Se obtiene una cuenta por su id findById() y valida el numero de movimientos y comision
	 * Se valida el monto a retirar y actualiza la cuenta con el retiro realizado save()
	 * Se registra la transacción y si hay comusion se registra también registerTransaction() 
	 * @param yankiRequestDto
	 * @return Mono<YankiResponseDto>
	 */
	@Override
	public Mono<YankiResponseDto> withdrawalYanki(YankiRequestDto yankiRequestDto) {
		LocalDateTime myDateObj = LocalDateTime.now();
		return yankiRepository.findById(yankiRequestDto.getId()).flatMap(uYanki -> {
			Double amount = uYanki.getAmount() - yankiRequestDto.getAmount();
			Double commission = uYanki.getTransaction() > 0 ? (0.0):(uYanki.getCommission());
			if(amount >= 0) {
				if(uYanki.getDescripTypeYanki().equals("PLAZO_FIJO") && uYanki.getOperationDay()!=myDateObj.getDayOfMonth()) {
					return Mono.just(new YankiResponseDto("Day of the month not allowed for PLAZO_FIJO", null));
				}
				if(amount < uYanki.getCommission()) return Mono.just(new YankiResponseDto("The operation cannot be carried out for an account amount less than the commission", null));
				uYanki.setAmount(amount-commission);
				uYanki.setTransaction(uYanki.getTransaction()>0?(uYanki.getTransaction() - 1):(0));
	            return yankiRepository.save(uYanki).flatMap(account -> {
	            	return registerTransaction(uYanki, yankiRequestDto.getAmount(),"RETIRO", uYanki.getTransaction()>0?0:uYanki.getCommission());
	            });
			}
			return Mono.just(new YankiResponseDto("You don't have enough balance", null));
        }).defaultIfEmpty(new YankiResponseDto("Account does not exist", null));
	}

	/**
	 * Obtiene todas la cuentas por el id del cliente
	 * @param customerId
	 * @return Flux<Account>
	 */
	@Override
	public Flux<Yanki> getAllYankiXCustomerId(String customerId) {
		return yankiRepository.findAll()
				.filter(c -> c.getCustomerId().equals(customerId));
	}

	/**
	 * Reiniciar el numero de movimientos de las cuentas
	 * Actualiza los movimientos permitidos a todas las cuentas en updateTransaction(); 
	 * @return Mono<Message>
	 */
	@Override
	public Mono<Message> restartTransactionsYanki() {
		return updateTransaction().collectList().flatMap(c -> {
			return Mono.just(new Message("The number of transactions of the accounts was satisfactorily restarted"));
		});
	}
	
	/**
	 * Obtiene nombre, movimientos y mantenimiento según el tipo de cuenta
	 * @param idType
	 * @return TypeAccountDto
	 */
	private TypeYankiDto getTypeYanki(Integer idType) {
		Predicate<TypeYankiDto> p = f -> f.getId()==idType;
		TypeYankiDto type = typeYanki.getAccounts().filter(p).collect(Collectors.toList()).get(0);
		return type;
    }

	/**
	 * Obtiene las cuentas según el id del cliente y tipo de cuenta
	 * @param customerId
	 * @param type
	 * @return Mono<Yanki>
	 */
	private Mono<Yanki> getYankitByIdCustomerPerson(String customerId, String type) {
		return  yankiRepository.findAll()
				.filter(c -> c.getCustomerId().equals(customerId))
				.filter(c -> c.getDescripTypeYanki().equals(type))
				.next();
	}
	
	/**
	 * Guarda una cuenta y registra el monto de apertura
	 * @param yanki
	 * @return Mono<YankiResponseDto>
	 */
	private Mono<YankiResponseDto> saveNewYanki(Yanki yanki) {
		return yankiRepository.save(yanki).flatMap(x -> {
			return registerTransaction(yanki, yanki.getAmount(),"APERTURA", 0.0).flatMap(t1 -> {
				return Mono.just(new YankiResponseDto("Yanki created successfully", x));
			});
		});
	}
	
	/**
	 * Registra una transacción
	 * @param uYanki
	 * @param amount
	 * @param typeTransaction
	 * @return Mono<AccountResponseDto>
	 */
	private Mono<YankiResponseDto> registerTransaction(Yanki uYanki, Double amount, String typeTransaction, Double commission){
		Transaction transaction = new Transaction();
		transaction.setCustomerId(uYanki.getCustomerId());
		transaction.setProductId(uYanki.getId());
		transaction.setProductType(uYanki.getDescripTypeYanki());
		transaction.setTransactionType(typeTransaction);
		transaction.setAmount(amount);
		transaction.setTransactionDate(LocalDateTime.now());
		transaction.setCustomerType(uYanki.getTypeCustomer());
		transaction.setBalance(uYanki.getAmount());
		return transactionRestClient.createTransaction(transaction).flatMap(t -> {
			if(commission>0) {
				transaction.setAmount(commission);
				transaction.setTransactionType("COMISION");
				return transactionRestClient.createTransaction(transaction).flatMap(d -> {
					return Mono.just(new YankiResponseDto("Successful transaction", uYanki));
				});
			}
			return Mono.just(new YankiResponseDto("Successful transaction", uYanki));
        });
	}
	
	/**
	 * Obtiene todas las transacciónes y actualiza el numero de transacciones permitidas
	 * @return Flux<Account>
	 */
	private Flux<Yanki> updateTransaction(){
		return  yankiRepository.findAll()
				.flatMap(c -> {
					c.setTransaction(getTypeYanki(c.getTypeYanki()).getTransactions());
					return yankiRepository.save(c);
				});
	}

	@Override
	public Mono<TransferResponseDto1> transferBetweenYanki(TransferRequestDto1 transferRequestDto1) {
		return yankiRepository.findById(TransferRequestDto1.getOriginYanki()).flatMap(a -> {
			return yankiRepository.findById(transferRequestDto1.getDestinationYanki()).filter(c -> !c.getId().equals(a.getId())).flatMap(b -> {
				if(a.getCustomerId().equals(b.getCustomerId())) {
					if(a.getAmount() - transferRequestDto1.getAmount() < 0)
						return Mono.just(new TransferResponseDto1("You do not have a sufficient balance in your origin account", null));
					a.setAmount(a.getAmount() - transferRequestDto1.getAmount());
					b.setAmount(b.getAmount() + transferRequestDto1.getAmount());
		            return yankiRepository.save(a).flatMap(account1 -> {
		            	return yankiRepository.save(b).flatMap(account2 -> {
		            		return registerTransaction(account1, transferRequestDto1.getAmount(),"TRANSFERENCIA ENTRE CUENTAS - RETIRO", 0.0).flatMap(t1 -> {
		            			return registerTransaction(account2, transferRequestDto1.getAmount(),"TRANSFERENCIA ENTRE CUENTAS - DEPOSITO", 0.0).flatMap(t2 -> {
		            				return Mono.just(new TransferResponseDto1("Successful transaction between accounts", Arrays.asList(a,b)));
		            			});
		            		});
		            	});
		            });
				}else {
					return Mono.just(new TransferResponseDto1("Accounts are not from the same client", null));
				}
			}).defaultIfEmpty(new TransferResponseDto1("Destination Account does not exist, enter another account", null));
		}).defaultIfEmpty(new TransferResponseDto1("Origin Account does not exist", null));
	}

//	@Override
//	public Mono<TransferResponseDto1> transferThirdPartyYanki(TransferRequestDto1 transferRequestDto1) {
//		return yankiRepository.findById(transferRequestDto1.getOriginYanki()).flatMap(a -> {
//			return yankiRepository.findById(transferRequestDto1.getDestinationYanki()).filter(c -> !(c.getCustomerId().equals(a.getCustomerId()))).flatMap(b -> {
//				if(a.getAmount() - transferRequestDto1.getAmount() < 0)
//					return Mono.just(new TransferResponseDto("You do not have a sufficient balance in your origin account", null));
//				a.setAmount(a.getAmount() - transferRequestDto1.getAmount());
//				b.setAmount(b.getAmount() + transferRequestDto1.getAmount());
//	            return yankiRepository.save(a).flatMap(account1 -> {
//	            	return yankiRepository.save(b).flatMap(account2 -> {
//	            		return registerTransaction(account1, transferRequestDto1.getAmount(),"TRANSFERENCIA A TERCEROS - RETIRO", 0.0).flatMap(t1 -> {
//	            			return registerTransaction(account2, transferRequestDto1.getAmount(),"TRANSFERENCIA A TERCEROS - DEPOSITO", 0.0).flatMap(t2 -> {
//	            				return Mono.just(new TransferResponseDto1("successful third-party account transaction", Arrays.asList(a,b)));
//	            			});
//	            		});
//	            	});
//	            });
//			}).defaultIfEmpty(new TransferResponseDto1("Destination Account does not exist, enter another account", null));
//		}).defaultIfEmpty(new TransferResponseDto1("Origin Account does not exist", null));
//	}
	
}
