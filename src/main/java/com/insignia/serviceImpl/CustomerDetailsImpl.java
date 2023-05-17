package com.insignia.serviceImpl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insignia.entity.TokensEntity;
import com.insignia.models.AuthenticationRequest;
import com.insignia.models.AuthenticationResponse;
import com.insignia.serviceInter.ICustomerDetails;

@Service
public class CustomerDetailsImpl implements ICustomerDetails {

	@Autowired
	EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public AuthenticationResponse createAndUpdateJwtToken(AuthenticationRequest authRequest,
			AuthenticationResponse authRes) {

		Optional<TokensEntity> isLongLivedToken = null;

		String fetchUserDetailsQuery = "SELECT sequence_number FROM customer_basic_details WHERE application_id=:applicationId AND tenant_id=:tenentId AND customer_id=:custId and customer_password=:custPassword";

		List<Long> resultList = entityManager.createNativeQuery(fetchUserDetailsQuery)
				.setParameter("applicationId", authRequest.getApplicationId())
				.setParameter("tenentId", authRequest.getTenantId()).setParameter("custId", authRequest.getUserId())
				.setParameter("custPassword", authRequest.getPassword()).getResultList();
		System.err.println(resultList.get(0));
		if (resultList.isEmpty()) {
			authRes.setTokenStatus("403");
			return authRes;
		}

		String fetQueryForTOken = "select * from tokens_table where customer_sequence_number=:seqNo";

		List<TokensEntity> resultList1 = entityManager.createNativeQuery(fetQueryForTOken)
				.setParameter("seqNo", resultList.get(0)).getResultList();

		if (!resultList1.isEmpty()) {
			isLongLivedToken = resultList1.stream()
					.filter(p -> p.getIs_long_lived_token() && p.getToken_revoked_at() == null).findFirst();

			if (isLongLivedToken.isPresent()) {
				authRes.setExpirationTime(isLongLivedToken.get().getToken_expires_at());
				authRes.setType(isLongLivedToken.get().getToken_type());
				authRes.setToken(isLongLivedToken.get().getToken_details());
				authRes.setTokenStatus("Token Is Long Lived");
				return authRes;
			} else {
				isLongLivedToken = resultList1.stream().filter(p -> !p.getIs_token_expired()
						&& p.getToken_revoked_at() == null && p.getToken_expires_at().compareTo(new Date()) == 1)
						.findFirst();
				if (isLongLivedToken.isPresent()) {
					authRes.setExpirationTime(isLongLivedToken.get().getToken_expires_at());
					authRes.setType(isLongLivedToken.get().getToken_type());
					authRes.setToken(isLongLivedToken.get().getToken_details());
					authRes.setTokenStatus("TokenNotExpired");
					return authRes;
				} else {
					authRes.setCustomerSeqNumber((Long) resultList.get(0));
					authRes.setTokenStatus("Update");
					return authRes;
				}
			}

		} else {
			storeUserJwtToken((Long) resultList.get(0), authRequest, authRes);
		}

		return authRes;
	}
	@Transactional
	public void updateUserJwtToken(String jwt, AuthenticationRequest authRequest, AuthenticationResponse authRes) {

		String updateToken = "UPDATE tokens_table SET is_token_expired=:is_token_expired,token_created_at=:token_created_at,token_details=:token_details,token_revoked_at=:token_revoked_at WHERE customer_sequence_number=:customer_sequence_number";

		entityManager.createNativeQuery(updateToken)
				.setParameter("customer_sequence_number", authRes.getCustomerSeqNumber())
				.setParameter("token_details", jwt)
				.setParameter("token_expires_at",
						new Date(System.currentTimeMillis() + 1000 * 60 * authRequest.getExpirationTime()))
				.setParameter("token_created_at", new Date()).setParameter("token_revoked_at", null)
				.setParameter("is_token_expired", false)
				.setParameter("is_long_lived_token", authRequest.isRememberMeSelected()).executeUpdate();

	}
	@Transactional
	public void storeUserJwtToken(Long seqNo, AuthenticationRequest authRequest, AuthenticationResponse authRes) {

		String storeToken = "Insert into tokens_table(customer_sequence_number,token_type,token_details,token_expires_at,token_created_at,token_revoked_at,is_token_expired,is_long_lived_token) values(:customer_sequence_number,:token_type,:token_details,:token_expires_at,:token_created_at,:token_revoked_at,:is_token_expired,:is_long_lived_token)";

		entityManager.createNativeQuery(storeToken).setParameter("customer_sequence_number", seqNo)
				.setParameter("token_type", "JWT").setParameter("token_details", "HS256")
				.setParameter("token_expires_at",
						new Date(System.currentTimeMillis() + 1000 * 60 * authRequest.getExpirationTime()))
				.setParameter("token_created_at", new Date()).setParameter("token_revoked_at", null)
				.setParameter("is_token_expired", false)
				.setParameter("is_long_lived_token", authRequest.isRememberMeSelected()).executeUpdate();
		authRes.setCustomerSeqNumber(seqNo);
		authRes.setTokenStatus("Created");
	}

}
