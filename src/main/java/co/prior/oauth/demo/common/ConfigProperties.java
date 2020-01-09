package co.prior.oauth.demo.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("wso2.tls")
public class ConfigProperties {

	public String trustStorePath;
    public String trustStorePassword;
    public String keyStorePath;
    public String keyStorePassword;
    public String defaultType;
    
}
