package mx.firmador;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.commons.ssl.Base64;
import org.apache.commons.ssl.PKCS8Key;
import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.bouncycastle.cert.ocsp.OCSPReq;



//import org.bouncycastle.ocsp.CertificateID;
//import org.bouncycastle.ocsp.OCSPException;
//import org.bouncycastle.ocsp.OCSPReq;
//import org.bouncycastle.ocsp.OCSPReqGenerator;
//import org.bouncycastle.ocsp.OCSPResp;

public class FirmaService {

	private static final String ALGORITMO = "SHA1withRSA";
	
	private static final String URL_SAT = "https://cfdit.sat.gob.mx/edofiel/";//SERVIDOR DE PRUEBAS Para que sea el real llamo a cfdi sin la t

	private byte[] firmar(String cadenaOriginal, byte[] archivoLlavePrivada,
			String password) {
		try {
			PrivateKey privateKey = getPrivateKey(archivoLlavePrivada, password);
			Signature sign = Signature.getInstance(ALGORITMO);
			sign.initSign(privateKey, new SecureRandom());
			sign.update(cadenaOriginal.getBytes());
			return sign.sign();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String firmarBase64(String cadenaOriginal,
			byte[] archivoLlavePrivada, String password) {
		return Base64.encodeBase64String(firmar(cadenaOriginal,
				archivoLlavePrivada, password));
	}

	private PrivateKey getPrivateKey(byte[] keyFile, String password)
			throws GeneralSecurityException, IOException {
		
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

	private boolean verificarDocumentoFirmadoConLlavePublica(
			String cadenaOriginal, byte[] documentoFirmado, byte[] llavePublica,byte[] llavePublicaSAT) {
		try {
			X509Certificate cert = (X509Certificate) CertificateFactory
					.getInstance("X509").generateCertificate(
							new ByteArrayInputStream(llavePublica));
			
			Signature sign = Signature.getInstance(ALGORITMO);
			sign.initVerify(cert.getPublicKey());
			sign.update(cadenaOriginal.getBytes());
			boolean verificacion = sign.verify(documentoFirmado);
			if (verificacion) {
				System.out.println("VERIFICACION EXITOSA DEL DOCUMENTO");
						//+ "PROCEDIENDO A VALIDAR CERTIFICADO CON EL SAT");
				return verificacion; //&& verificarCertificadoSAT(llavePublica,llavePublicaSAT);
			}
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			System.err.println("ERROR AL OBTENER CERTIFICADO DEL FIRMANTE");
			e.printStackTrace();
		}
		return false;
	}

	public boolean verificarCertificadoSAT(byte[] llavePublica,byte[] llavePublicaSAT) {
		verificarCertificado(llavePublica);
		verificarCertificado(llavePublicaSAT);
		X509Certificate certSAT = null;
		X509Certificate cert = null;
		try {
			certSAT = (X509Certificate) CertificateFactory
					.getInstance("X509").generateCertificate(
							new ByteArrayInputStream(llavePublicaSAT));
			
			cert = (X509Certificate) CertificateFactory
					.getInstance("X509").generateCertificate(
							new ByteArrayInputStream(llavePublica));
		} catch (CertificateException e1) {
			// TODO Auto-generated catch block
			System.err.println("ERROR AL OBTENER CERTIFICADO DEL SAT");
			e1.printStackTrace();
			return false;
		}
		
		
		try {
			int status = verificarOCSP(cert, certSAT);
			return status == OCSPResponseStatus.SUCCESSFUL;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("ERROR EN LA VERIFICACIÓN OCSP");
			e.printStackTrace();
		}
		return false;
	}

	public boolean verificarCertificado(byte[] certificado){
		try{
			X509Certificate cert = (X509Certificate) CertificateFactory
					.getInstance("X509").generateCertificate(
							new ByteArrayInputStream(certificado));
			cert.checkValidity();
		} catch(GeneralSecurityException ex){
			System.err.println("ERROR AL OBTENER CERTIFICADO DEL FIRMANTE");
			ex.printStackTrace();
		}
		return true;
	}
	
	public boolean verificarDocumentoFirmadoConLlavePublicaBase64(
			String cadenaOriginal, String documentoFirmado, byte[] llavePublica, byte[] llavePublicaSAT) {
		return verificarDocumentoFirmadoConLlavePublica(cadenaOriginal,
				Base64.decodeBase64(documentoFirmado), llavePublica,llavePublicaSAT);
	}
	
	//MANERA EJEMPLIFICADA
	private int verificarOCSP(X509Certificate cert,X509Certificate certSAT) throws OCSPException,IOException{
//		Security.addProvider(new BouncyCastleProvider());
//		OCSPReqGenerator ocspReqGen = new OCSPReqGenerator();
//		CertificateID certid = new CertificateID(CertificateID.HASH_SHA1,
//				certSAT, cert.getSerialNumber());
//		ocspReqGen.addRequest(certid);
//		OCSPReq ocspReq = ocspReqGen.generate();
//		byte[] certificadoOCSP = ocspReq.getEncoded();
//		
//		byte[] respuestaSAT = conectarSAT(certificadoOCSP);
//		System.out.println(respuestaSAT.length>0?"EL SAT HA RESPONDIDO":"EL SAT NO RESPONDIO");
//		OCSPResp ocspResponse = new OCSPResp(respuestaSAT);
//		return ocspResponse.getStatus();
		return 1;
	}
	
	private byte[] conectarSAT(byte[] certificadoOCSP){
		byte[] respuesta = null;
		try{
		URL url=new URL(URL_SAT);
		HttpURLConnection con=(HttpURLConnection)url.openConnection();
		con.setRequestProperty("Content-Type","application/ocsp-request");
		con.setRequestProperty("Accept","application/ocsp-response");
		//con.setRequestMethod("POST");
		con.setDoOutput(true);
		
		OutputStream out=con.getOutputStream();
		DataOutputStream dataOut=new DataOutputStream(new BufferedOutputStream(out));
		dataOut.write(certificadoOCSP);
		dataOut.flush();
		dataOut.close();
		out.close();
		
		InputStream in=con.getInputStream();
		respuesta = new byte[in.available()];
		in.read(respuesta);
		con.disconnect();
		
		}catch(IOException ex){
			System.err.println("ERROR AL CONECTAR AL SAT");
			ex.printStackTrace();
		}
		return respuesta;
		
		/*CloseableHttpClient cliente = HttpClients.createDefault();
		HttpPost post = new HttpPost(URL_SAT);
		HttpEntity entity = MultipartEntityBuilder.create()
				.addBinaryBody("certificado", certificadoOCSP).build();
		post.setEntity(entity);
		HttpResponse response = cliente.execute(post);
		System.out.println(response.getStatusLine());
		System.out.println(response.getEntity().getContentLength());*/
	}
}
