package co.prior.oauth.demo.auth.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.prior.oauth.demo.auth.model.AuthorizeResponse;
import co.prior.oauth.demo.auth.model.UserCertificate;
import co.prior.oauth.demo.auth.model.UserInfo;
import co.prior.oauth.demo.common.X509CertificateUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {

	@Value("${wso2.oauth2.client.registration.client-id}")
	private String clientId;

	@Value("${wso2.oauth2.client.registration.client-secret}")
	private String clientSecret;
	
	@Value("${wso2.oauth2.client.registration.redirect-uri}")
	private String redirectUri;
	
	@Value("${wso2.oauth2.client.provider.token-uri}")
	private String tokenUri;
	
	@Value("${wso2.oauth2.client.provider.user-info-uri}")
	private String userInfoUri;
	
	@Value("${jasperserver.rest.login.endpoint}")
	private String jasperserverLoginEndpoint;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private X509CertificateUtils x509CertificateUtils;
	
	public UserInfo authorizeCallback(String code) throws IOException, GeneralSecurityException {
		log.info("authorization code: {}", code);
		
		String accessToken = this.getWso2AccessToken(code);
		return this.getWso2UserInfo(accessToken);
	}
	
	public Optional<String> getJasperServerSession(String username, String password) {
    	log.info("jasper server login username: {}", username);
    	
    	HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("j_username", username);
		params.add("j_password", password);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

		ResponseEntity<Void> response = this.restTemplate.exchange(jasperserverLoginEndpoint, HttpMethod.POST, request, Void.class);
		return response.getHeaders().get(HttpHeaders.SET_COOKIE).stream().filter(v -> v.startsWith("JSESSIONID")).findFirst();
    }
	
	private String getWso2AccessToken(String code) throws IOException {
		String credentials = clientId + ":" + clientSecret;
		String encodedCredentials = Base64Utils.encodeToString(credentials.getBytes());

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", "Basic " + encodedCredentials);

		HttpEntity<String> request = new HttpEntity<String>(headers);

		String access_token_url = tokenUri;
		access_token_url += "?code=" + code;
		access_token_url += "&grant_type=authorization_code";
		access_token_url += "&redirect_uri=" + redirectUri;

		ResponseEntity<AuthorizeResponse> response = restTemplate.exchange(access_token_url, HttpMethod.POST, request, AuthorizeResponse.class);
		AuthorizeResponse authorizeResponse = response.getBody();
		log.info("access token response: {}", authorizeResponse);
		
		return authorizeResponse.getAccessToken();
	}
	
	private UserInfo getWso2UserInfo(String accessToken) throws GeneralSecurityException {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<UserInfo> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, entity, UserInfo.class);
		UserInfo userInfo = response.getBody();
		log.info("user info: {}", userInfo);
		
		String subjectDN = this.x509CertificateUtils.decodeX509Certificate(userInfo.getUserCertificate());
		HashMap<String, String> certAttr = (HashMap<String, String>) Arrays.asList(subjectDN.split(", ")).stream().map(s -> s.split("=")).collect(Collectors.toMap(e -> e[0], e -> e[1]));
		log.info("x509 certificate attributes: {}", certAttr);
		
		UserCertificate userCertificate = new ObjectMapper().convertValue(certAttr, UserCertificate.class);
		log.info("user certificate: {}", userCertificate);
		
		return userInfo;
	}
	
}
