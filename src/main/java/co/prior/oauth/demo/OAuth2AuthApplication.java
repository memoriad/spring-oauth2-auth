package co.prior.oauth.demo;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import co.prior.oauth.demo.common.ConfigProperties;

@SpringBootApplication
public class OAuth2AuthApplication {

	@Autowired
	private ConfigProperties configProperties;
    
	public static void main(String[] args) {
		SpringApplication.run(OAuth2AuthApplication.class, args);
	}
	
	@PostConstruct
    void postConstruct() throws IOException {
        // set TrustStoreParams
        File trustStoreFilePath = new File(configProperties.trustStorePath);
        String tsp = trustStoreFilePath.getAbsolutePath();
        System.setProperty("javax.net.ssl.trustStore", tsp);
        System.setProperty("javax.net.ssl.trustStorePassword", configProperties.trustStorePassword);
        System.setProperty("javax.net.ssl.keyStoreType", configProperties.defaultType);
        // set KeyStoreParams
        File keyStoreFilePath = new File(configProperties.keyStorePath);
        String ksp = keyStoreFilePath.getAbsolutePath();
        System.setProperty("Security.KeyStore.Location", ksp);
        System.setProperty("Security.KeyStore.Password", configProperties.keyStorePassword);
    }
	
}
