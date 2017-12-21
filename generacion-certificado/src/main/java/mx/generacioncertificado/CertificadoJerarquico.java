package mx.generacioncertificado;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Random;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.joda.time.DateTime;

public class CertificadoJerarquico {

	 public static void main(String[] args) throws Exception {  
	     Security.addProvider(new BouncyCastleProvider());  
	   
	     // Create self signed Root CA certificate  
	     KeyPair rootCAKeyPair = generateKeyPair();  
	     X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(  
	         new X500Name("CN=rootCA"), // issuer authority  
	         BigInteger.valueOf(new Random().nextInt()), //serial number of certificate  
	         DateTime.now().toDate(), // start of validity  
	         new DateTime(2025, 12, 31, 0, 0, 0, 0).toDate(), //end of certificate validity  
	         new X500Name("CN=rootCA"), // subject name of certificate  
	         rootCAKeyPair.getPublic()); // public key of certificate  
	     // key usage restrictions  
	     builder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.keyCertSign));  
	     builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));  
	     X509Certificate rootCA = new JcaX509CertificateConverter().getCertificate(builder  
	         .build(new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").  
	             build(rootCAKeyPair.getPrivate()))); // private key of signing authority , here it is self signed  
	     
	     saveToFile(rootCA, "C:/Users/Renato-PC/Desktop/llave/rootCA.cer");  
	   
	   
	     //create Intermediate CA cert signed by Root CA  
	     KeyPair intermedCAKeyPair = generateKeyPair();  
	     builder = new JcaX509v3CertificateBuilder(  
	         rootCA, // here rootCA is issuer authority  
	         BigInteger.valueOf(new Random().nextInt()), DateTime.now().toDate(),  
	         new DateTime(2025, 12, 31, 0, 0, 0, 0).toDate(),  
	         new X500Name("CN=IntermedCA"), intermedCAKeyPair.getPublic());  
	     builder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.keyCertSign));  
	     builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));  
	     
	     X509Certificate intermedCA = new JcaX509CertificateConverter().getCertificate(builder  
	         .build(new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").  
	             build(rootCAKeyPair.getPrivate())));// private key of signing authority , here it is signed by rootCA  
	     saveToFile(intermedCA, "C:/Users/Renato-PC/Desktop/llave/intermedCA.cer");  
	     
	     //create end user cert signed by Intermediate CA  
	     KeyPair endUserCertKeyPair = generateKeyPair();  
	     builder = new JcaX509v3CertificateBuilder(  
	         intermedCA, //here intermedCA is issuer authority  
	         BigInteger.valueOf(new Random().nextInt()), DateTime.now().toDate(),  
	         new DateTime(2025, 12, 31, 0, 0, 0, 0).toDate(),  
	         new X500Name("CN=endUserCert"), endUserCertKeyPair.getPublic());  
	     builder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));  
	     builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));  
	     X509Certificate endUserCert = new JcaX509CertificateConverter().getCertificate(builder  
	         .build(new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").  
	             build(intermedCAKeyPair.getPrivate())));// private key of signing authority , here it is signed by intermedCA  
	     
	     saveToFile(endUserCert, "C:/Users/Renato-PC/Desktop/llave/endUserCert.cer");  
	     System.out.println("Termino :)");
	   }  
	   
	   private static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {  
	     KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");  
	     kpGen.initialize(2048, new SecureRandom());  
	     return kpGen.generateKeyPair();  
	   }  
	   
	   private static void saveToFile(X509Certificate certificate, String filePath) throws IOException, CertificateEncodingException {  
	     FileOutputStream fileOutputStream = new FileOutputStream(filePath);  
	     fileOutputStream.write(certificate.getEncoded());  
	     fileOutputStream.flush();  
	     fileOutputStream.close();  
	   }  

}
