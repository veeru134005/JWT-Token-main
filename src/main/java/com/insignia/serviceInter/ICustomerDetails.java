package com.insignia.serviceInter;

import com.insignia.models.AuthenticationRequest;
import com.insignia.models.AuthenticationResponse;

public interface ICustomerDetails {

	public AuthenticationResponse createAndUpdateJwtToken(AuthenticationRequest authRequest,AuthenticationResponse authRes);
	public void updateUserJwtToken(String jwt,AuthenticationRequest authRequest, AuthenticationResponse authRes);
	
}