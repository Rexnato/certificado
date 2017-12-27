package mx.firmador;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Firmador {
	public static void main(String[] args) {
		// CDI
//		WeldContainer weld = new Weld().initialize();
//		FirmaService firmaService = weld.instance().select(FirmaService.class)
//				.get();
		FirmaService firmaService = new FirmaService();
		
		byte[] llavePrivada = leerArchivo("I:/DESARROLLO_EXTERNO/recursos/FirmaDigital/Archivos_cer_key/AAA010101AAA.key");
		
		byte[] llavePublica = leerArchivo("I:/DESARROLLO_EXTERNO/recursos/FirmaDigital/Archivos_cer_key/AAA010101AAA.cer");//prueba: aacf670505np1 real:fiel

		byte[] llavePublicaSAT = null;//leerArchivo("AC_SAT1059.crt");//prueba: ac2_4096 real:AC_SAT1059
		
		String cadenaOriginal = "hola";
		String documentoFirmado = firmaService.firmarBase64(cadenaOriginal,
				llavePrivada, "12345678");
		System.out.println(documentoFirmado);
		boolean esValido = firmaService
				.verificarDocumentoFirmadoConLlavePublicaBase64(cadenaOriginal,
						documentoFirmado, llavePublica,llavePublicaSAT);
		System.out.println(esValido + "\t" + documentoFirmado.length());
		
		//byte[] llavePublicaPrueba = leerArchivo("aacf670505np1.cer");
		//byte[] llavePublicaSATPrueba = leerArchivo("ac2_4096.crt");
		
		//System.out.println(firmaService.verificarCertificadoSAT(llavePublicaPrueba, llavePublicaSATPrueba) ? "EXITO" : "FRACASO");
	}
	
	public static byte[] leerArchivo(String path){
		byte[] bytes = null;
		try{
			InputStream stream = new FileInputStream(path);
			bytes = new byte[stream.available()];
			stream.read(bytes);
			stream.close();
		} catch(IOException ex){
			System.err.println("ERROR AL LEER EL ARCHIVO\t"+path);
			ex.printStackTrace();
		}
		return bytes;
	}
}
