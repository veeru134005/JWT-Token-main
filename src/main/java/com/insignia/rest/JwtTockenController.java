package com.insignia.rest;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.insignia.models.AuthenticationRequest;
import com.insignia.models.AuthenticationResponse;
import com.insignia.security.MyUserDetailsService;
import com.insignia.serviceInter.ICustomerDetails;
import com.insignia.util.JwtUtil;

@RestController
public class JwtTockenController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@Autowired
	private MyUserDetailsService userDetailsService;
	
	@Autowired
	private ICustomerDetails service;

	@Value("${errorCodes.500}")
	private String internalServerError;
	
	@Value("${errorCodes.403}")
	private String forbidden;
	
	@Autowired	
	private ICustomerDetails repo;

	
	
	
	@RequestMapping({ "/hello" })
	public String firstPage() {
		return "Hello World";
	}

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
			throws Exception {

		try {
			AuthenticationResponse authResp = repo.createAndUpdateJwtToken(authenticationRequest,new AuthenticationResponse());
			
			System.err.println(authResp);
			
			if(authResp.getTokenStatus()!=null && authResp.getTokenStatus().equalsIgnoreCase("403")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationResponse("403",forbidden));
			}else if(authResp.getTokenStatus()!=null && (authResp.getTokenStatus().equalsIgnoreCase("Token Is Long Lived") || authResp.getTokenStatus().equalsIgnoreCase("TokenNotExpired"))) {
				return ResponseEntity.ok(authResp);
			}
			
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getCustomeUserName(), authenticationRequest.getPassword()));

			//final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getCustomeUserName());
			 
			 User userDetails=new User(authenticationRequest.getCustomeUserName(),
			 authenticationRequest.getPassword(), new ArrayList<>());

			final String jwt = jwtTokenUtil.generateToken(userDetails, authenticationRequest.getExpirationTime());
			
			repo.updateUserJwtToken(jwt, authenticationRequest, authResp);

			return ResponseEntity.ok(new AuthenticationResponse(jwt, "JWT", jwtTokenUtil.extractExpiration(jwt)));

		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationResponse("403",forbidden));
		}catch (Exception e) {
			return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthenticationResponse("500",internalServerError));
		}

	}

}
