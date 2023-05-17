package com.insignia.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customer_basic_details")
@Entity
public class CustomerBasicDetailsEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long sequence_number;
	
	
	private String application_id;
	
	
	private String tenant_id;
	
	
	private String customer_id;
	
	
	private String customer_password;
	
	
	private String customer_email;
	
	
}
