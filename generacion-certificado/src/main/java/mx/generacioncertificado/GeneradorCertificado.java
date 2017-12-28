package mx.generacioncertificado;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
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

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.joda.time.DateTime;

import mx.zip.ZipConverter;

public class GeneradorCertificado {
	
	private static final String RFC="AAA010101AAA";//
	
	private static final String CURP = "HEGT761003MDFRNN09";
	
	private static final String NOMBRE_RAZON_SOCIAL = "NOMBRE RAZON SOCIAL";
	
	//private static final String PATH_CER="C:/Users/Renato-PC/Desktop/llave/"+RFC+".cer";
	private static final String PATH_CER="I:/DESARROLLO_EXTERNO/recursos/FirmaDigital/Archivos_cer_key/"+RFC+".cer";
	
	//private static final String PATH_KEY="C:/Users/Renato-PC/Desktop/llave/"+RFC+".key";
	private static final String PATH_KEY="I:/DESARROLLO_EXTERNO/recursos/FirmaDigital/Archivos_cer_key/"+RFC+".key";
	
	
	//private static final String PATH_PASS="I:/DESARROLLO_EXTERNO/recursos/FirmaDigital/Archivos_cer_key/"+"password.txt";
	private static final String PATH_ZIP="C:/Users/israe/Desktop/ZIP/"+RFC+".zip";
	
    private static final String SOURCE_FOLDER = "I:/DESARROLLO_EXTERNO/recursos/FirmaDigital/Archivos_cer_key";
	
    private static final String PATH_PASS ="I:/DESARROLLO_EXTERNO/recursos/FirmaDigital/Archivos_cer_key/password.txt";
	private static final String PROVIDER="BC";//
	
	private static final String PASS_KEY="12345678";//
	
	public static void main(String[] args) throws Exception{
		
		 //generate random UUIDs
//	    UUID idOne = UUID.randomUUID();
//	    UUID idTwo = UUID.randomUUID();
//
//	    System.out.println( String.valueOf("UUID One: " + idOne));
//	    System.out.println( String.valueOf("UUID Two: " + idTwo));
		
		//saveToFiletxt(PATH_PASS);
		
	
//		ZipConverter zip = new ZipConverter();
//		zip.generateFileList(new File(SOURCE_FOLDER), SOURCE_FOLDER);
//		zip.zipIt(PATH_ZIP, SOURCE_FOLDER);
		
		//generar();
	}

	private static void  generar() throws Exception{
		Security.addProvider(new BouncyCastleProvider());//para tomar el algoritmo de encriptamiento de BC
		
		KeyPair rootCAKeyPair = generateKeyPair();  
	     X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(  
	         obtenerEmisor(), //emisor 
	         BigInteger.valueOf(new Random().nextInt()), //serial number of certificate  
	         DateTime.now().toDate(), // start of validity  
	         new DateTime(2025, 12, 31, 0, 0, 0, 0).toDate(), //end of certificate validity  
	         obtenerSujeto(), // subject name of certificate  
	         rootCAKeyPair.getPublic()); // public key of certificate  
	     
	     // key usage restrictions  
	     builder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.keyCertSign));  
	     builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));  
	     X509Certificate rootCA = new JcaX509CertificateConverter().getCertificate(builder  
	         .build(new JcaContentSignerBuilder("SHA1withRSA").setProvider(PROVIDER).  
	             build(rootCAKeyPair.getPrivate()))); // private key of signing authority , here it is self signed  
	     
	     
	    
	     
	     saveToFile(rootCA.getEncoded(), PATH_CER);  
	     System.out.println("*** Guardo Cer ****");
	     saveToFile(rootCAKeyPair.getPrivate().getEncoded(), PATH_KEY);  
	     System.out.println("*** Guardo Key ****");
	     
	     
	     saveToFiletxt(PATH_PASS);
	     
	     System.out.println("*** Verificar pass key valida ****");
	     ValidadorCertificado validador = new ValidadorCertificado(rootCAKeyPair.getPrivate().getEncoded(), "");
	     System.out.println("*** Fin ****");
	}
	
	
	private static X500Name obtenerEmisor(){
		X500NameBuilder issuerBuilder = new X500NameBuilder();
		issuerBuilder.addRDN(BCStyle.L,"Manhattan");
		issuerBuilder.addRDN(BCStyle.ST,"New York");
		issuerBuilder.addRDN(BCStyle.C, "EU");
		issuerBuilder.addRDN(BCStyle.POSTAL_CODE, "20000");
		issuerBuilder.addRDN(BCStyle.STREET, "The Avengers");
		issuerBuilder.addRDN(BCStyle.EmailAddress, "tony@starkindustries.com");
		issuerBuilder.addRDN(BCStyle.OU, "Developer Team");
		issuerBuilder.addRDN(BCStyle.O, "Stark Industries");
		issuerBuilder.addRDN(BCStyle.CN, "Certificado de Pruebas");
		issuerBuilder.addRDN(new ASN1ObjectIdentifier("2.5.4.45"), "ASAT200490H21");
        return issuerBuilder.build();
	}
	
	private static X500Name obtenerSujeto(){
		X500NameBuilder issuerBuilder = new X500NameBuilder();
		issuerBuilder.addRDN(BCStyle.CN, NOMBRE_RAZON_SOCIAL);
		issuerBuilder.addRDN(new ASN1ObjectIdentifier("2.5.4.41"), NOMBRE_RAZON_SOCIAL);
		issuerBuilder.addRDN(BCStyle.O, "Organizacion del Portador");
		issuerBuilder.addRDN(new ASN1ObjectIdentifier("2.5.4.45"), RFC);
		issuerBuilder.addRDN(BCStyle.SERIALNUMBER, "/"+CURP);
		issuerBuilder.addRDN(BCStyle.OU, "Nombre Unidad Organizacion");
        return issuerBuilder.build();
	}

	private static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {  
	     KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA",PROVIDER); 
	     kpGen.initialize(2048, new SecureRandom(PASS_KEY.getBytes()));  //aqui se genera el pass aleatorio ?? no estoy seguro
	     return kpGen.generateKeyPair();  
	}  
	
	private static void saveToFile(byte encoded[] , String filePath) throws IOException, CertificateEncodingException {  
	     FileOutputStream fileOutputStream = new FileOutputStream(filePath);  
	     fileOutputStream.write(encoded);  
	     fileOutputStream.flush();  
	     fileOutputStream.close();  
	}
	
	private static void  saveToFiletxt(String ruta) {
		//String ruta = "/home/mario/archivo.txt";
        File archivo = new File(ruta);
        BufferedWriter bw = null;
        if(!archivo.exists()) {
        	 try {
				bw = new BufferedWriter(new FileWriter(archivo));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             try {
				bw.write("12345678");
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }else {
        	System.out.println("El archivo ya se encuentra creado");
        }
        
	}
}
