package com.bankaccounts.controller;

import com.bankaccounts.dto.*;
import com.bankaccounts.model.Yanki;
import com.bankaccounts.service.YankiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/yanki")
@RequiredArgsConstructor
public class YankiController {
	
	@Autowired
    private YankiService yankiService;
	

	@GetMapping
    public Flux<Yanki> getAll(){
		return yankiService.getAll();
    }
	

	@GetMapping("/{yankiId}")
    public Mono<Yanki> getYankiById(@PathVariable String yankiId){
		return yankiService.getYankiById(yankiId);
    }
	

	@PostMapping("/person")
    public Mono<YankiResponseDto> createYankiPerson(@RequestBody @Valid YankiRequestDto yankiRequestDto) {
		return (yankiService.createYankiPerson(yankiRequestDto));
    }
	

	@PostMapping("/company")
    public Mono<YankiResponseDto> createYankiCompany(@RequestBody @Valid YankiRequestDto yankiRequestDto) {
		return (yankiService.createYankiCompany(yankiRequestDto));
    }
	

	@PutMapping
	public Mono<Yanki> updateYanki(@RequestBody YankiRequestDto yankiRequestDto){
		return yankiService.updateYanki(yankiRequestDto);
    }
	

	@DeleteMapping("/{yankiId}")
	public Mono<Message> deleteYanki(@PathVariable String yankiId){
		return yankiService.deleteYanki(yankiId);
    }
	

	@PostMapping("/deposit")
    public Mono<YankiResponseDto> depositYanki(@RequestBody @Valid YankiRequestDto yankiRequestDto) {
		return yankiService.depositYanki(yankiRequestDto);
    }
	

	@PostMapping("/withdrawal")
    public Mono<YankiResponseDto> withdrawalYanki(@RequestBody @Valid YankiRequestDto yankiRequestDto) {
		return yankiService.withdrawalYanki(yankiRequestDto);
    }
	

	@GetMapping("/consult/{customerId}")
    public Flux<Yanki> getAllYankiXCustomerId(@PathVariable String customerId){
		return yankiService.getAllYankiXCustomerId(customerId);
    }



	@PostMapping("/transfer")
    public Mono<TransferResponseDto1> transferBetweenYanki(@RequestBody @Valid TransferRequestDto1 transferRequestDto1) {
		return yankiService.transferBetweenYanki(transferRequestDto1);
    }
	

	@PostMapping("/transferthird")
    public Mono<TransferResponseDto1> transferThirdParty(@RequestBody @Valid TransferRequestDto1 transferRequestDto1) {
		return yankiService.transferThirdParty(transferRequestDto1);
    }
}
