package co.prior.oauth.demo.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserCertificate {

	@JsonProperty("CN")
	private String cn;
	
	@JsonProperty("C")
	private String c;
	
	@JsonProperty("ST")
	private String st;
	
	@JsonProperty("L")
	private String l;
	
	@JsonProperty("O")
	private String o;
	
	@JsonProperty("OU")
	private String ou;
	
	@JsonProperty("EMAILADDRESS")
	private String email;
}
