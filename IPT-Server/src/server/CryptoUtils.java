package server;


import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;



public class CryptoUtils  {

    private static final String KEY="12@AB2124$124*#!";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    public static byte[] encrypt(byte[] data) {
        try {
            return doCrypto(Cipher.ENCRYPT_MODE, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte[] data) {
        try {
            return doCrypto(Cipher.DECRYPT_MODE,data);
        } catch (Exception e) {
            return null;
        }
    }

      private static byte[] doCrypto(int cipherMode,byte[] input) throws Exception {
        //String key=keyBuilder(KEY);
        Key secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        cipher.init(cipherMode, secretKey);
        
        byte[] outputBytes = cipher.doFinal(input);

        return outputBytes;
    }
}
