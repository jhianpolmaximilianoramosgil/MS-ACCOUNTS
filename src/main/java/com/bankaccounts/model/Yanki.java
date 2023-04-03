package com.bankaccounts.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="yanki")
public class Yanki {
	@Id
	private String id;
	@NotEmpty
	private String customerId;
	@NotEmpty
	@JsonIgnore
	private Integer typeYanki;
	private String descripTypeYanki;
	@NotEmpty
	private Double amount;

	private Double startAmount;
	@NotEmpty
	private Double maintenance;
	@NotEmpty
	private Integer transaction;
	@NotEmpty
	private Integer operationDay;
	@NotEmpty
	private LocalDateTime dateYanki;
	@NotEmpty
	private String telephoneYanki;

	private String typeCustomer;

	private Double commission;

}
