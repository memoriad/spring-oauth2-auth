package co.prior.oauth.demo.auth.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import co.prior.oauth.demo.auth.service.AuthService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/oauth")
public class AuthController {

	@Value("${wso2.oauth2.client.endpoint}")
	private String wso2AuthorizeEndpoint;
	
	@Autowired
	private AuthService authService;
	
	@GetMapping("/wso2/authorize")
	public ModelAndView authorize() {
		return new ModelAndView("redirect:" + wso2AuthorizeEndpoint);
	}
	
	@GetMapping("/jasperserver/login")
	public void loginJasperServer(@RequestParam String username, @RequestParam String password, HttpServletResponse httpServletResponse) throws IOException {
		log.info("jasper server login username: {}", username);
		
		Optional<String> jsessionid = this.authService.getJasperServerSession(username, password);
//		ex. JSESSIONID=C9F0986654005B262706324CD77BE13C; Path=/jasperserver; HttpOnly
		
		if (jsessionid.isPresent()) {
			HashMap<String, String> jsessionidAttr = (HashMap<String, String>) Arrays.asList(jsessionid.get().split("; ")).stream().map(s -> s.split("=")).filter(e -> e.length > 1).collect(Collectors.toMap(e -> e[0], e -> e[1]));
			Cookie cookie = new Cookie("JSESSIONID", jsessionidAttr.get("JSESSIONID"));
			cookie.setVersion(0);
			cookie.setPath(jsessionidAttr.get("Path"));
			
			httpServletResponse.addCookie(cookie);
			httpServletResponse.sendRedirect("http://localhost:8080/jasperserver/flow.html?_flowId=searchFlow");
		} else {
			httpServletResponse.sendRedirect("http://localhost:8080/jasperserver/login.html");
		}
	}

}
