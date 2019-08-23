/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 *
 * @author MadEye
 */
public class PackageAnalizer {

    private ArrayList<byte[]> ip;
    private ArrayList<byte[]> data;

    public PackageAnalizer(ArrayList<byte[]> ip, ArrayList<byte[]> data) {
        this.ip = ip;
        this.data = data;
    }

    public void dataInfo() {
        ArrayList<byte[]> temp = new ArrayList<>();
        for (byte[] t1 : ip) {
            for (byte cb : t1) {
                System.out.print(cb);
            }
            System.out.println("  :  " + XOR(t1));
        }
        for (byte[] mark : temp) {
            System.out.println(new String(mark));
        }
    }

    public static String XOR(byte[] p1) {
        try{
        BigInteger b1 = new BigInteger(p1);
        
        BigInteger b2 = new BigInteger("192.168.1.11".getBytes());

        BigInteger o = b1.xor(b2);
        System.out.print("  :  "+convertBigIntegerToString(o));
        //-----------------------------
        BigInteger b3 = new BigInteger(InetAddress.getByName("192.168.1.11").getHostAddress().getBytes());
        //BigInteger b3 = new BigInteger("192.168.1.11".getBytes());
        o = o.xor(b3);
        return convertBigIntegerToString(o);
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
