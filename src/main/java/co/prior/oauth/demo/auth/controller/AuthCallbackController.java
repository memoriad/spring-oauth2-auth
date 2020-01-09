package co.prior.oauth.demo.auth.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.prior.oauth.demo.auth.model.UserInfo;
import co.prior.oauth.demo.auth.service.AuthService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/oauth")
public class AuthCallbackController {

	@Autowired
	private AuthService authService;
	
	@GetMapping("/code/wso2")
	public ResponseEntity<UserInfo> authorizeCallback(@RequestParam("code") String code) throws IOException, GeneralSecurityException {
		log.info("authorization code: {}", code);
		
		UserInfo userInfo = this.authService.authorizeCallback(code);
		
		return ResponseEntity.ok(userInfo);
	}
	
}
