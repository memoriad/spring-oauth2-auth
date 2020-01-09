package co.prior.oauth.demo.common;

import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class X509CertificateUtils {

	public String decodeX509Certificate(String certificateBase64) throws GeneralSecurityException {
		log.info("certificate base64: {}", certificateBase64);
		byte encodedCert[] = Base64.getDecoder().decode(certificateBase64);
		ByteArrayInputStream inputStream  =  new ByteArrayInputStream(encodedCert);

		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		X509Certificate cert = (X509Certificate)certFactory.generateCertificate(inputStream);
		String subjectDN = cert.getSubjectDN().getName();
		log.info("subjectDN: {}", subjectDN);
		
		return subjectDN;
	}
	
}
