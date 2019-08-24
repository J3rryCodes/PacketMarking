package router;


import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;



public class CryptoUtils  {

    private static final String KEY="12@AB2124$124*#!";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    public static byte[] encrypt(byte[] data) {
        try {
            System.out.println("[Enc] Encrypting Data size : "+data.length);
            byte[] enc = doCrypto(Cipher.ENCRYPT_MODE, data);
            System.out.println("[Enc] Encrypting Completed  size : "+enc.length);
            return enc;
        } catch (Exception e) {
            System.out.println("[Enc] Non-Encrypted Data!!");
            return null;
        }
    }

    public static byte[] decrypt(byte[] data) {
        try {
            System.out.println("[Dec] Decrypting Data size : "+data.length);
            byte[] dec = doCrypto(Cipher.DECRYPT_MODE, data);
            System.out.println("[Dec] Decrypting Completed : "+dec.length);
            return dec;
        } catch (Exception e) {
            System.out.println("[Dec] Decryption Faild");
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
