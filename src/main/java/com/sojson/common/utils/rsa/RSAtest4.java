package com.sojson.common.utils.rsa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public final class RSAtest4 {
    //公钥私钥是2048的
    private static final String DEFAULT_PUBLIC_KEY=
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCi2ZVv4UmyMm2GHjbV55yTZ1Sm\n" +
                    "SO1qaRtrT0tTIpg2uTmQRt5hEmmPa9FtvuiO3sjjwPuLA/z4H2jLMlbqMvWS9Hbp\n" +
                    "VUSafzOg4xrY6K39QYIvayIkuzd+Oc6WqI0pLB0aXWObbq+SbnYQkAYmkOUNo95p\n" +
                    "QppyPQICekUYZhWYGQIDAQAB";

    private static final String DEFAULT_PRIVATE_KEY=
            "MIICXAIBAAKBgQCi2ZVv4UmyMm2GHjbV55yTZ1SmSO1qaRtrT0tTIpg2uTmQRt5h\n" +
                    "EmmPa9FtvuiO3sjjwPuLA/z4H2jLMlbqMvWS9HbpVUSafzOg4xrY6K39QYIvayIk\n" +
                    "uzd+Oc6WqI0pLB0aXWObbq+SbnYQkAYmkOUNo95pQppyPQICekUYZhWYGQIDAQAB\n" +
                    "AoGASG5YXguo+J4fQqDbqOY8KP+zkcYr+sFHFcKN8SsY4nYd2UL8E9bZppXA73wS\n" +
                    "+cCzP0GNX/ZlJILr3DzGlaIWwREh8y2jRUfJX0/I398NXKiZ/BAXjcdfWn/zNSb7\n" +
                    "SOnxJE3bRQZpoc4dJsD8TMwugt4dUmua+2ZGUUFlhvnYVWUCQQDXSWwrvNHh6M0G\n" +
                    "LkVqXmB2I135KKjdjoVaci4nowiWMjBUOjdy55qUEUirZbZ8eNFfSbnVbshbcONO\n" +
                    "GZfWLOnXAkEAwaWPXPThT2mXeTBYOuCPjz+v8H+BX4QxPFWuEVsbFfcnoJD40pyo\n" +
                    "8eVbdG4mvfKtSu3JlM1iQgSz/m7KR2OvjwJBALxZn0qwusszutQSfWavRSN2k8vd\n" +
                    "98dG3oKf7KN+7o9BP6kg+cwMtt+eM37I0RwbM8pzHSOO9bmSjTuSBpY52ycCQBEV\n" +
                    "oD5dRHuTH1AL2dzn7wJ+ZfFb7ngU25stG/xgA1W+xno+EBXUyHr+90Cgbz6W6H8R\n" +
                    "15UPQbn9XDv4XlRKETUCQH3zBo1pOqHiMEshx4seReRiErcx/G8Xcxd/bIQNHBHm\n" +
                    "scuqYMtL08L7y6IYY26tVmZxECXY2sv6RNPWaua4vWY=";

    /**
     * 私钥
     */
    private static RSAPrivateKey privateKey;

    /**
     * 公钥
     */
    private static RSAPublicKey publicKey;

    static{
        //rsaEncrypt.genKeyPair();
        //加载公钥
        try {
            //解决BC问题
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            loadPublicKey(RSAtest4.DEFAULT_PUBLIC_KEY);
            System.out.println("加载公钥成功");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("加载公钥失败");
        }
        //加载私钥
        try {
            loadPrivateKey(RSAtest4.DEFAULT_PRIVATE_KEY);
            System.out.println("加载私钥成功");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("加载私钥失败");
        }
    }
    /**
     * 字节数据转字符串专用集合
     */
    private static final char[] HEX_CHAR= {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    /**
     * 获取私钥
     * @return 当前的私钥对象
     */
    private static RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * 获取公钥
     * @return 当前的公钥对象
     */
    private static RSAPublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * 随机生成密钥对
     */
    private static void genKeyPair(){
        KeyPairGenerator keyPairGen= null;
        try {
            keyPairGen= KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyPairGen.initialize(1024, new SecureRandom());
        KeyPair keyPair= keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        byte[] privateBytes = privateKey.getEncoded();
        BASE64Encoder encoder = new BASE64Encoder();
        String privateStr = encoder.encode(privateBytes);
        byte[] publicBytes= publicKey.getEncoded();
        String publicStr = encoder.encode(publicBytes);
        System.out.println("privateKey:"+privateStr);
        System.out.println("publicKey:"+publicStr);
    }

    /**
     * 从文件中输入流中加载公钥
     * @param in 公钥输入流
     * @throws Exception 加载公钥时产生的异常
     */
    @SuppressWarnings("unused")
    private  static void  loadPublicKey(InputStream in) throws Exception{
        try {
            BufferedReader br= new BufferedReader(new InputStreamReader(in));
            String readLine= null;
            StringBuilder sb= new StringBuilder();
            while((readLine= br.readLine())!=null){
                if(readLine.charAt(0)=='-'){
                    continue;
                }else{
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            loadPublicKey(sb.toString());
        } catch (IOException e) {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空");
        }
    }


    /**
     * 从字符串中加载公钥
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    private static void loadPublicKey(String publicKeyStr) throws Exception{
        try {
            BASE64Decoder base64Decoder= new BASE64Decoder();
            byte[] buffer= base64Decoder.decodeBuffer(publicKeyStr);
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec= new X509EncodedKeySpec(buffer);
            publicKey= (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (IOException e) {
            throw new Exception("公钥数据内容读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    /**
     * 从文件中加载私钥
     * @param keyFileName 私钥文件名
     * @return 是否成功
     * @throws Exception
     */
    @SuppressWarnings("unused")
    private  static void loadPrivateKey(InputStream in) throws Exception{
        try {
            BufferedReader br= new BufferedReader(new InputStreamReader(in));
            String readLine= null;
            StringBuilder sb= new StringBuilder();
            while((readLine= br.readLine())!=null){
                if(readLine.charAt(0)=='-'){
                    continue;
                }else{
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            loadPrivateKey(sb.toString());
        } catch (IOException e) {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空");
        }
    }
    /**
     * 从字符串中加载私钥
     * @param privateKeyStr
     * @throws Exception
     */
    private static void loadPrivateKey(String privateKeyStr) throws Exception{
        try {
            BASE64Decoder base64Decoder= new BASE64Decoder();
            byte[] buffer= base64Decoder.decodeBuffer(privateKeyStr);
            PKCS8EncodedKeySpec keySpec= new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");
            privateKey= (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            throw new Exception("私钥非法");
        } catch (IOException e) {
            throw new Exception("私钥数据内容读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }

    /**
     * 公钥加密过程
     * @param publicKey 公钥
     * @param plainTextData 明文数据
     * @return
     * @throws Exception 加密过程中的异常信息
     */
    private static byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception{
        if(publicKey== null){
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher= null;
        try {
            cipher= Cipher.getInstance("RSA/None/PKCS1Padding","BC");//, new BouncyCastleProvider());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] output= cipher.doFinal(plainTextData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }catch (InvalidKeyException e) {
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
    }

    /**
     * 私钥加密过程
     * @param publicKey 公钥
     * @param plainTextData 明文数据
     * @return
     * @throws Exception 加密过程中的异常信息
     */
    private static byte[] encryptByPrivate(RSAPrivateKey privateKey, byte[] plainTextData) throws Exception{
        if(privateKey== null){
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher= null;
        try {
            cipher= Cipher.getInstance("RSA/None/PKCS1Padding","BC");//, new BouncyCastleProvider());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] output= cipher.doFinal(plainTextData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }catch (InvalidKeyException e) {
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
    }

    /**
     * 私钥解密过程
     * @param privateKey 私钥
     * @param cipherData 密文数据
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    private static byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception{
        if (privateKey== null){
            throw new Exception("解密私钥为空, 请设置");
        }
        Cipher cipher= null;
        try {
            cipher= Cipher.getInstance("RSA/None/PKCS1Padding","BC");//, new BouncyCastleProvider());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] output= cipher.doFinal(cipherData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }catch (InvalidKeyException e) {
            throw new Exception("解密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏");
        }
    }
    /**
     * 公钥解密过程
     * @param privateKey 私钥
     * @param cipherData 密文数据
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */

    private static byte[] decryptByPublicKey(RSAPublicKey publicKey, byte[] cipherData) throws Exception{
        if (publicKey== null){
            throw new Exception("解密私钥为空, 请设置");
        }
        Cipher cipher= null;
        try {
            cipher= Cipher.getInstance("RSA/None/PKCS1Padding","BC");//, new BouncyCastleProvider());
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] output= cipher.doFinal(cipherData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }catch (InvalidKeyException e) {
            throw new Exception("解密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏");
        }
    }

    /**
     * 字节数据转十六进制字符串
     * @param data 输入数据
     * @return 十六进制内容
     */
    public static String byteArrayToString(byte[] data){
        StringBuilder stringBuilder= new StringBuilder();
        for (int i=0; i<data.length; i++){
            //取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移
            stringBuilder.append(HEX_CHAR[(data[i] & 0xf0)>>> 4]);
            //取出字节的低四位 作为索引得到相应的十六进制标识符
            stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);
            if (i<data.length-1){
                stringBuilder.append(' ');
            }
        }
        return stringBuilder.toString();
    }

    /**
     * decrypt String by Ras(sub) by privateKey
     * @return success:return decode original
     */
    public static String decryptByPrivateKey(String ciphertext) throws Exception{
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] cipher =  decoder.decodeBuffer(ciphertext);
            byte[] plainText = decrypt(getPrivateKey(), cipher);
            return new String(plainText,"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * decrypt String by Ras(sub) by publicKey
     * @return success:return decode original
     */
    public static String decryptByPublicKey(String ciphertext) throws Exception{
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] cipher =  decoder.decodeBuffer(ciphertext);
            byte[] plainText = decryptByPublicKey(getPublicKey(), cipher);
            return new String(plainText,"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    /**
     * encrypt String by Ras(add)  by publicKey
     * @return success:return encrypt Base64 String  ;  fail:"fail"
     * @throws Exception
     */
    public static String encryptByPublicKey(String plaintext) throws Exception{
        try {
            BASE64Encoder encoder = new BASE64Encoder();
            //encrypt process
            byte[] cipher = encrypt(getPublicKey(), plaintext.getBytes("utf-8"));
            //byte[] to String by Base64
            return encoder.encode(cipher);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * encrypt String by Ras(add)  by PrivateKey
     * @return success:return encrypt Base64 String
     * @throws Exception
     */
    public static String encryptByPrivateKey(String plaintext) throws Exception{
        try {
            BASE64Encoder encoder = new BASE64Encoder();
            //encrypt process
            byte[] cipher = encryptByPrivate(getPrivateKey(), plaintext.getBytes("utf-8"));
            //byte[] to String by Base64
            return encoder.encode(cipher);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args){
        //测试字符串
        String encryptStr= "12345678";
        try {
            System.out.println("=============私钥加密，公钥解密！===============");
            //私钥加密
        	/*
            System.out.println("明文："+encryptStr);
            long encryptstart = System.currentTimeMillis();
           String cipherStr = encryptByPrivateKey(encryptStr);
            System.out.println("私钥加密密文："+cipherStr);

            long encryptend = System.currentTimeMillis();
            System.out.println("encrypt use time:"+(encryptend-encryptstart)+"ms");
         */   //公钥钥解密

            long decryptstart = System.currentTimeMillis();
            String a = "lRHus3FBpSCrTnScJJkRNq6pSz"
                    + "SnKpEe38NSdzwRIbWP6h+QjISf2"
                    + "PNEafptxdsSdqw3wqUbSvyAKpj/T"
                    + "G4Q7ulrD4P4eOU/icIyClpCP6jG"
                    + "C/EguhG5NG7VIGwvLGsx7t6KT4no"
                    + "Er19iX06Rk7Z1KyD5CbmpGLKjIuw"
                    + "6JZ+qzY=";
                   a = "lRHus3FBpSCrTnScJJkRNq6pSzSnKpEe38NSdzwRIbWP6h+QjISf2PNEafptxdsSdqw3wqUbSvyAKpj/TG4Q7ulrD4P4eOU/icIyClpCP6jGC/EguhG5NG7VIGwvLGsx7t6KT4noEr19iX06Rk7Z1KyD5CbmpGLKjIuw6JZ+qzY=";
            String plaineText = decryptByPublicKey(a);
            System.out.println("公钥解密："+plaineText);
            long decryptend = System.currentTimeMillis();
            System.out.println("decrypt use time:"+(decryptend-decryptstart)+"ms");


            String b = "GJYOo4kEntk2ayNr2gNL3gb6aW"
                    + "UDVoRn7GLaRMB2nUOSLzUX7wFDo"
                    + "6zv3nU0+n4zRhTA4W/FeNc42nel"
                    + "6q414hNC2tvdQ9VaqsE46mh3htpM"
                    + "7tO1YIBgj+cqMX9lFuEzklop6SZQQm"
                    + "NC0kmSAy/9sD1N7VsYNNrHBVNV1E5"
                    + "Ok6U=";


            String plaineText2 = decryptByPrivateKey(b);
            System.out.println("私钥解密："+plaineText2);

            /*
        	System.out.println("decrypt use time:"+(decryptend-decryptstart)+"ms");
        	System.out.println("=============公钥加密，私钥解密！===============");
        	//公钥加密
            System.out.println("明文："+encryptStr);
            encryptstart = System.currentTimeMillis();
            cipherStr = encryptByPublicKey(encryptStr);
            System.out.println("公钥加密密文："+cipherStr);
            encryptend = System.currentTimeMillis();
            System.out.println("encrypt use time:"+(encryptend-encryptstart)+"ms");
            //私钥解密
            decryptstart = System.currentTimeMillis();
            plaineText = decryptByPrivateKey(cipherStr);
        	System.out.println("解密："+plaineText);
        	decryptend = System.currentTimeMillis();
        	System.out.println("decrypt use time:"+(decryptend-decryptstart)+"ms");
	    //	System.out.println("===========获取私钥公钥============");
	    //    genKeyPair();//获取密钥对
	     */

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}  
