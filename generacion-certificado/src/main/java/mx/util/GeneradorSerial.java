package mx.util;

import java.math.BigInteger;
import java.util.UUID;

public class GeneradorSerial {

	
	
	public BigInteger obtenerSerialNumber() {
		
		 //generate random UUIDs
	    UUID idOne = UUID.randomUUID();

	    String hexadecimal = idOne.toString().replace("-", "").toUpperCase(); 
	    
	    BigInteger decimalNumber = this.convertirToDecimal(hexadecimal);
	    
	    //SI NO EXISTIERON PROBLEMAS AL CONVETIR DE HEXADECIMAL A DECIMAL
	    if(decimalNumber!=null) {
	    	if(decimalNumber.compareTo(BigInteger.ZERO)>0){
	    		return decimalNumber;
	    	}
	    	//SI EL UUID GENERADO FUE MENOR O IGUAL A CERO, INTENTO CON LA GENERACION DE UN NUEVO UUID
	    	//AQUI SE PODRIA METER LA CONSULTA (A BD ) CON NUMEROS DE SERIE DE CERTIFCADOS YA CREADOS PARA EVITAR DUPLICIDAD
	    	else {
	    		obtenerSerialNumber();
	    	}
	    }
	    //SI EXISTIERON PROBLEMAS AL CONVERTIR DE HEXADECIMAL A DECIMAL INTENTO LA GENERACION DE UN NUEVO UUID
	    else {
	    	obtenerSerialNumber();
	    }
	    
	    return null;
	}
	
	
	private BigInteger convertirToDecimal(String numberHexadecimal) {
		BigInteger bigInt = null;
		try { 
			System.out.println("UUDI generado (hexa): "+ numberHexadecimal);
	    	 bigInt = new BigInteger(numberHexadecimal, 16);
	    	System.out.println("Conversion a decimal: " +bigInt);
	    	
	    
	    	}catch(NumberFormatException ne){
				// Printing a warning message if the input is not a valid hex number
	    		ne.printStackTrace();
	    		
	    		System.out.println(ne.getLocalizedMessage());
	    		
				System.out.println("PROBLEMAS AL CONVERTIR DE HEXADECIMAL A DECIMAL");
			}
			
		return bigInt;
	}
	
	
	
	
}
