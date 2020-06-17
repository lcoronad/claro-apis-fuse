package com.claro;

import static org.junit.Assert.assertEquals;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.claro.utils.AESEncryption;
import com.claro.utils.FuncionesUtileria;

public class AESEncryptionTest {
	
	private static final String KEY = "AA47455B133A53CC";
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Test
	public void encryptTest() throws Exception { 
		
		String cuenta = "3127800091";
		String resultEncrypt = AESEncryption.encrypt(cuenta, KEY);
		logger.info("Resultado:{}", resultEncrypt);
		assertEquals("aU3ztfUFgT2Bqzz5052ifA==", resultEncrypt);
	}
	
	@Test
	public void decryptTest() throws Exception {
		String cuentaEncrypt = "P87rbNm3InQyobio2+B/ZQ==";
		String cuentaOrigin = AESEncryption.decrypt(cuentaEncrypt, KEY);
		logger.info("Cuenta original: {}", cuentaOrigin);
		assertEquals("1.00095113", cuentaOrigin);
	}
	
	@Test
	public void validateDate() {
		String fechaT = FuncionesUtileria.fechaWithT();
		logger.info("Fecha actual:{}", fechaT);
		assertEquals(true, fechaT.contains("T")); 
	}
	
	@Test
	public void completIdReference() {
		String idReferencia = FuncionesUtileria.completeIdReference("1352956834");
		logger.info("Id Referencia:{}",idReferencia);
		assertEquals(24, idReferencia.length());
	}

}
