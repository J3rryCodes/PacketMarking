/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.math.BigInteger;
import java.net.InetAddress;

/**
 *
 * @author J3rryCodes
 */
public class XORConverter {
    public static byte[] XOR(byte[] p1,byte[] mark) {
        try{
        BigInteger b1 = new BigInteger(p1);
        
        BigInteger b2 = new BigInteger(mark);

        BigInteger o = b1.xor(b2);
        System.out.print("  :  "+convertBigIntegerToString(o));
        
        return o.toByteArray();
        }catch(Exception e){}
        return null;
    }

    public static String convertBigIntegerToString(BigInteger b) {
        String s = new String();
        while (b.compareTo(BigInteger.ZERO) == 1) {
            BigInteger c = new BigInteger("11111111", 2);
            int cb = (b.and(c)).intValue();
            Character cv = new Character((char) cb);
            s = (cv.toString()).concat(s);
            b = b.shiftRight(8);
        }
        return s;
    }
}
