package com.insignia.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tokens_table")
public class TokensEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long sequence_number;

	private Long customer_sequence_number;

	private String token_type;

	private String token_details;

	private Date token_expires_at;

	private Date token_created_at;

	private Date token_revoked_at;

	private Boolean is_token_expired;

	private Boolean is_long_lived_token;

}
