/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package router;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author J3rryCodes
 */
public class Router extends Thread implements Serializable {

    private int nxtRouter;
    private DatagramSocket receiverSocket, senderSocket;
    private InetAddress ipAddress;
    private int MAX_LEN = 544;
    private boolean isMark;

    public Router(int port, int nxtRouter, String ipAddress, boolean flag) throws UnknownHostException {
        this.nxtRouter = nxtRouter;
        this.isMark = flag;
        this.ipAddress = InetAddress.getByName(ipAddress);
        try {
            receiverSocket = new DatagramSocket(port);
        } catch (Exception ex) {
            System.err.println("UnknownHostException");
        }
        System.out.println("Router " + port + " Started...");
    }

    private void markPacket() throws IOException {
        BigInteger ip1 = new BigInteger(InetAddress.getLocalHost().getHostAddress().getBytes());

        int ip1_Len = ip1.toByteArray().length;
        senderSocket = new DatagramSocket();
        System.out.println("IS PACKET MARKING ENABLED :" + isMark);
        while (true) {
            byte inputData[] = new byte[MAX_LEN];
            DatagramPacket rdp = new DatagramPacket(inputData, inputData.length);
            receiverSocket.receive(rdp);

            System.out.println("[+] Data Comming from : " + InetAddress.getLocalHost().getHostAddress());

            System.out.println("[>] Received Packet length : " + rdp.getLength());
            
            //is Packet Market Enabled;
            
            if (isMark) {
                // Sourse IP address
                BigInteger ip2 = new BigInteger(rdp.getAddress().getHostAddress().getBytes());
                
                int ipLen;
                boolean marked = false;
                byte[] mark = new byte[1];
                byte[] xyz = new byte[1];
                int dataLen = 0;

                byte[] rData = new byte[rdp.getLength()];
                for (int i = 0; i < rData.length; i++) {
                    rData[i] = rdp.getData()[i];
                }
                byte[] dcp = CryptoUtils.decrypt(rData);
                byte[] data = (dcp != null) ? dcp : rData;

                System.out.print("[**]");
                for (byte b : data) {
                    System.out.print(b);
                }
                System.out.println("[**]");

                byte[] packet = new byte[data.length + ip1_Len + 1];

                for (int i = 0; i < data.length; i++) {
                    if (data[i] == (byte) -2) {
                        marked = true;
                        dataLen = i;
                        ipLen = data.length - (i + 1);
                        mark = new byte[ipLen];
                        continue;
                    }
                    if (marked) {
                        mark[i - (dataLen + 1)] = data[i];
                    }
                }
                //Pre-Marked Packet
                if (marked) {
                    System.out.println("[*] Marked Packet");

                    System.out.print("[[[");
                    for (byte b : mark) {
                        System.out.print(b);
                    }
                    System.out.print(" - ");
                    for (byte b : ip1.toByteArray()) {
                        System.out.print(b);
                    }
                    System.out.println("]]]");

                    byte[] xor_mark = new BigInteger(mark).xor(ip1).toByteArray();
                    packet = new byte[dataLen + xor_mark.length + 1];

                    //Fetching data to packet
                    int count = 0;
                    byte b;
                    while ((b = data[count]) != (byte) -2) {
                        packet[count] = b;
                        count++;
                    }
                    //adding end of Data falg
                    packet[count] = (byte) -2;
                    System.out.println("[D] Data length : " + count);

                    //Marking 
                    count++;
                    System.out.print("[#] New Mark : ");
                    for (int i = count, j = 0; i < packet.length; i++, j++) {
                        packet[i] = xor_mark[j];
                        System.out.print(packet[i]);
                    }
                    System.out.println();
                } //Fresh Packet
                else {
                    System.out.println("[*] Fresh Packet");
                //Fetching data to packet

                    //System.out.println(convertBigIntegerToString(ip1)+"   =   "+convertBigIntegerToString(ip2));
                    int count;
                    for (count = 0; count < data.length; count++) {
                        packet[count] = data[count];
                    }
                    //adding end of data flag
                    packet[count] = (byte) -2;

                    //Marking 
                    count++;
                    byte[] xor_mark = ip2.xor(ip1).toByteArray();
                    System.out.print("[#] New Mark : ");
                    for (int i = count, j = 0; i < packet.length; i++, j++) {
                        packet[i] = xor_mark[j];
                        System.out.print(packet[i]);
                    }
                    System.out.println();
                }
                //Encrypting data
                byte[] enc = CryptoUtils.encrypt(packet);
                //Sending marked encrypted Packets 
                System.out.println("[<] Sending Data length : " + enc.length);
                DatagramPacket sdp = new DatagramPacket(enc, enc.length, ipAddress, nxtRouter);
                senderSocket.send(sdp);
                System.out.println("------------------------------------------------------------");
            } else {
                //Sending un-marked un-encrypted Packets 
                System.out.println("[<] Sending Data length : " + rdp.getLength());
                DatagramPacket sdp = new DatagramPacket(rdp.getData(), rdp.getLength(), ipAddress, nxtRouter);
            }
        }
    }

    @Override
    public void run() {
        try {
            markPacket();
        } catch (Exception e) {
            System.err.println("Error while staring THREAD");
        }
    }

    public static String convertBigIntegerToString(BigInteger b) {
        String s = new String();
        while (b.compareTo(BigInteger.ZERO) == 1) {
            BigInteger c = new BigInteger("11111111", 2);
            int cb = (b.and(c)).intValue();
            Character cv = (char) cb;
            s = (cv.toString()).concat(s);
            b = b.shiftRight(8);
        }
        return s;
    }
}
