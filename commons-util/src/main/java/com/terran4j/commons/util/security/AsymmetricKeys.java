package com.terran4j.commons.util.security;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import com.terran4j.commons.util.Encoding;
import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.CommonErrorCode;

/**
 * 非对称密钥。
 * 
 * @author wei.jiang
 *
 */
public class AsymmetricKeys {
	
	private static final int KEY_LENGTH = 512;
	
	private static final String ALGORITHM_RSA = "RSA";
	
	private static volatile KeyFactory keyFactory = null;
	
	private static volatile KeyPairGenerator keyPairGen = null;
	
	private static final KeyFactory getKeyFactory() throws BusinessException {
		if (keyFactory != null) {
			return keyFactory;
		}
		synchronized (AsymmetricKeys.class) {
			if (keyFactory != null) {
				return keyFactory;
			}
			try {
				keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
			} catch (NoSuchAlgorithmException e) {
				throw new BusinessException(CommonErrorCode.INTERNAL_ERROR, e)
						.put("algorithm", ALGORITHM_RSA)
						.setMessage("No Such Algorithm: ${algorithm}");
			}
			return keyFactory;
		}
	}
	
	private static final KeyPairGenerator getKeyPairGenerator() throws BusinessException {
		if (keyPairGen != null) {
			return keyPairGen;
		}
		synchronized (AsymmetricKeys.class) {
			if (keyPairGen != null) {
				return keyPairGen;
			}
			try {
				keyPairGen = KeyPairGenerator.getInstance(ALGORITHM_RSA);
			} catch (NoSuchAlgorithmException e) {
				throw new BusinessException(CommonErrorCode.INTERNAL_ERROR, e)
						.put("algorithm", ALGORITHM_RSA)
						.setMessage("No Such Algorithm: ${algorithm}");
			}
			keyPairGen.initialize(KEY_LENGTH, new SecureRandom());
			return keyPairGen;
		}
	}
	
	/**
	 * 公钥
	 */
	private RSAPublicKey publicKey;
	
	/**
	 * 私钥
	 */
	private RSAPrivateKey privateKey;

	/**
	 * 公钥加密器。
	 */
	private Cipher publicCipher;
	
	/**
	 * 私钥解密器。
	 */
	private Cipher privateCipher;
	
	/**
	 * 随机生成密钥对
	 * @throws BusinessException 
	 */
	public AsymmetricKeys() throws BusinessException {
		super();
		KeyPair keyPair = getKeyPairGenerator().generateKeyPair();
		this.publicKey = (RSAPublicKey) keyPair.getPublic();
		this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
		this.publicCipher = initCipher(publicKey);
		this.privateCipher = initCipher(privateKey);
	}
	
	public AsymmetricKeys(String publicKeyText, String privateKeyText) throws BusinessException {
		super();
		this.publicKey = loadPublicKey(publicKeyText);
		this.privateKey = loadPrivateKey(privateKeyText);
		this.publicCipher = initCipher(publicKey);
		this.privateCipher = initCipher(privateKey);
	}
	
	private String encode(byte[] key) {
		return Base64.toBase64String(key);
	}
	
	private byte[] decode(String key) {
		return Base64.decode(key);
	}
	
	public String getPublicKey() {
		return encode(publicKey.getEncoded());
	}
	
	public String getPrivateKey() {
		return encode(privateKey.getEncoded());
	}
	
	private Cipher initCipher(RSAPrivateKey privateKey) throws BusinessException {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM_RSA, new BouncyCastleProvider());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return cipher;
		} catch (NoSuchAlgorithmException e) {
			throw new BusinessException(CommonErrorCode.INTERNAL_ERROR, e)
					.put("algorithm", ALGORITHM_RSA)
					.setMessage("No Such Algorithm: ${algorithm}");
		} catch (NoSuchPaddingException e) {
			throw new BusinessException(CommonErrorCode.INTERNAL_ERROR, e)
					.setMessage("No Such Padding");
		} catch (InvalidKeyException e) {
			throw new BusinessException(CommonErrorCode.INTERNAL_ERROR, e)
					.setMessage("解密私钥非法,请检查");
		}
	}
	
	private Cipher initCipher(RSAPublicKey publicKey) throws BusinessException {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM_RSA, new BouncyCastleProvider());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return cipher;
		} catch (NoSuchAlgorithmException e) {
			throw new BusinessException(CommonErrorCode.INTERNAL_ERROR, e)
					.put("algorithm", ALGORITHM_RSA)
					.setMessage("No Such Algorithm: ${algorithm}");
		} catch (NoSuchPaddingException e) {
			throw new BusinessException(CommonErrorCode.INTERNAL_ERROR, e)
					.setMessage("No Such Padding");
		} catch (InvalidKeyException e) {
			throw new BusinessException(CommonErrorCode.INTERNAL_ERROR, e)
					.setMessage("加密公私非法,请检查");
		}
	}
	
	/**
	 * 从字符串中加载公钥
	 * 
	 * @param publicKeyText
	 *            公钥数据字符串
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
	private RSAPublicKey loadPublicKey(String publicKeyText) throws BusinessException {
		try {
			byte[] data = decode(publicKeyText);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(data);
			return (RSAPublicKey) getKeyFactory().generatePublic(keySpec);
		} catch (InvalidKeySpecException e) {
			throw new BusinessException(CommonErrorCode.INVALID_PARAM, e)
					.put("publicKey", publicKeyText)
					.setMessage("公钥非法");
		}
	}

	private RSAPrivateKey loadPrivateKey(String privateKeyText) throws BusinessException {
		try {
			byte[] data = decode(privateKeyText);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(data);
			return (RSAPrivateKey) getKeyFactory().generatePrivate(keySpec);
		} catch (InvalidKeySpecException e) {
			throw new BusinessException(CommonErrorCode.INVALID_PARAM, e)
					.put("privateKey", privateKeyText)
					.setMessage("私钥非法");
		}
	}

	private byte[] encrypt(byte[] data) throws Exception {
		try {
			byte[] output = publicCipher.doFinal(data);
			return output;
		} catch (IllegalBlockSizeException e) {
			throw new Exception("明文长度非法", e);
		} catch (BadPaddingException e) {
			throw new Exception("明文数据已损坏", e);
		}
	}
	
	/**
	 * 用公钥加密
	 * @param plainText 明文
	 * @return 密文
	 * @throws BusinessException 加密过程中的异常信息
	 */
	public String encrypt(String plainText) throws BusinessException {
		try {
			byte[] data = plainText.getBytes(Encoding.UTF8.getName());
			byte[] encryptedData = encrypt(data);
			String result = Strings.toHexString(encryptedData);
			return result;
		} catch (Exception e) {
			throw new BusinessException(CommonErrorCode.INVALID_PARAM, e)
					.put("plainText", plainText)
					.setMessage("非法的明文数据");
		}
	}
	
	/**
	 * 用私钥解密
	 * @param cipherText 密文
	 * @return 明文
	 * @throws BusinessException 解密过程中的异常信息
	 */
	public String decrypt(String cipherText) throws BusinessException {
		try {
			byte[] data = Strings.fromHexString(cipherText);
			byte[] decryptedData = decrypt(data);
			String result = new String(decryptedData, Encoding.UTF8.getName());
			return result;
		} catch (Exception e) {
			throw new BusinessException(CommonErrorCode.INVALID_PARAM, e)
					.put("cipherText", cipherText)
					.setMessage("非法的密文");
		}
	}

	private byte[] decrypt(byte[] data) throws Exception {
		if (privateKey == null) {
			throw new Exception("解密私钥为空, 请设置");
		}
		try {
			byte[] output = privateCipher.doFinal(data);
			return output;
		} catch (IllegalBlockSizeException e) {
			throw new Exception("密文长度非法", e);
		} catch (BadPaddingException e) {
			throw new Exception("密文数据已损坏", e);
		}
	}

}
