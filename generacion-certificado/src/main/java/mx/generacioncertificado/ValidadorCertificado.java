package mx.generacioncertificado;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.commons.ssl.PKCS8Key;

public class ValidadorCertificado {
	
	private PrivateKey privateKey;

	private byte[] key;

	private String pass;

	

	public ValidadorCertificado(byte[] key, String pass) {
		this.key = key;
		this.pass = pass;
		try {
			privateKey = getPrivateKey(key, pass);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException(
					"No se puede usar el archivo de llave, posiblemente est\u00E9 da\u00F1ado o la contrase\u00F1a sea incorrecta",
					e);
		}
		if (privateKey == null) {
			throw new IllegalStateException(
					"No se puede usar el archivo de llave, posiblemente est\u00E9 da\u00F1ado o la contrase\u00F1a sea incorrecta");
		}
	}

	private PrivateKey getPrivateKey(byte[] keyFile, String password)throws GeneralSecurityException, IOException {
		
		PKCS8Key pkcs = new PKCS8Key(keyFile, password.toCharArray());
		byte[] decryptor = pkcs.getDecryptedBytes();
		PKCS8EncodedKeySpec apec = new PKCS8EncodedKeySpec(decryptor);
		PrivateKey pk = null;
		if (pkcs.isDSA()) {
			pk = KeyFactory.getInstance("DSA").generatePrivate(apec);
		} else {
			pk = KeyFactory.getInstance("RSA").generatePrivate(apec);
		}
		return pk;
	}

	

	private String obtenerAlgoritmoYValidarCer(X509Certificate certificado) {
		// TODO Auto-generated method stub
		String algoritmo = privateKey.getAlgorithm();
		// int longitudKey = privateKey.getEncoded().length*1024;
		
		RSAPrivateKey rsaKey = (RSAPrivateKey) privateKey;
		
		int longitudKey = algoritmo.equals("RSA") ? rsaKey.getModulus().bitLength() : 0;// Si es DSA No se aun como
												// obtener el size
		if (!(longitudKey == 1024 || longitudKey == 2048)) {
			throw new IllegalStateException(
					"Solamente se aceptan longitudes de clave de 1024 y 2048 bits");
		}
		
		RSAPublicKey rsaCer = (RSAPublicKey) certificado.getPublicKey();
		
		if(!rsaKey.getModulus().equals(rsaCer.getModulus())||!(((RSAPrivateCrtKey) rsaKey).getPublicExponent().equals(rsaCer.getPublicExponent()))){
			throw new IllegalStateException("La llave no corresponde al certificado");
		}
		
		int longitudAlgoritmo = longitudKey / 1024;

		if (longitudAlgoritmo == 2) {
			longitudAlgoritmo = 256;
		}
		return "SHA" + longitudAlgoritmo + "With" + algoritmo;
	}

	
}
