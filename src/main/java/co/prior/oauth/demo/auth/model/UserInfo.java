package co.prior.oauth.demo.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserInfo {

	@JsonProperty("sub")
	private String username;
	
	private String userCertificate;
	
	@JsonProperty("CN")
	private String cn;
}
