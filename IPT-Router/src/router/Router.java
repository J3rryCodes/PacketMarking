/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package router;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author MadEye
 */
public class Router extends Thread {

    private int nxtRouter;
    private DatagramSocket receiverSocket, senderSocket;
    private InetAddress ipAddress;
    private int MAX_LEN = 528;

    public Router(int port, int nxtRouter, String ipAddress, boolean flag) throws UnknownHostException {
        this.nxtRouter = nxtRouter;
        this.ipAddress = InetAddress.getByName(ipAddress);
        try {
            receiverSocket = new DatagramSocket(port);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Router " + port + " Started...");
    }

    private void markPacket() throws IOException {
        BigInteger ip1 = new BigInteger(InetAddress.getLocalHost().getHostAddress().getBytes());
        senderSocket = new DatagramSocket();
        while (true) {
            byte b[] = new byte[MAX_LEN];
            DatagramPacket rdp = new DatagramPacket(b, b.length);
            receiverSocket.receive(rdp);
            System.out.println("[>] Received Packet [Size: " + rdp.getLength() + "]");

            byte[] packet = new byte[rdp.getLength() + ip1.toByteArray().length+1];
            byte[] ip = new byte[ip1.toByteArray().length];
            int markStart = -1;
            for (int i = 0; i < rdp.getLength(); i++) {
                if (rdp.getData()[i] == (byte) -2) {
                    markStart = i;
                    ip = new byte[rdp.getLength()-markStart-1];
                    packet = new byte[rdp.getLength()];
                    continue;
                }
                if(markStart>0){
                    ip[i-(markStart+1)]=rdp.getData()[i];
                }
            }
            //adding full data
            for (int i = 0; i < rdp.getLength(); i++) {
                packet[i] = rdp.getData()[i];
                
            }

            //Starting of Mark
            if (markStart > 0) {
                System.out.println("[#] Marking alrady Marked Packet[Source:Router]");
                
                
                BigInteger ip2 = new BigInteger(ip);
                // XORing
                byte[] xor_ip = ip2.xor(ip1).toByteArray();
                //adding Mark
                System.out.print("{$} Mark ["+convertBigIntegerToString(ip1)+"] + [");
                for(byte c:ip){
                    System.out.print(c);
                }
                System.out.print("] = [");
                for (int i = markStart+1, j = 0; i < packet.length; i++, j++) {
                    packet[i] = xor_ip[j];
                    System.out.print(xor_ip[j]);
                }
                System.out.println("]");
            } else {
                System.out.println("[*] Marking fress Packet[Source:Client]");

                //flag
                packet[rdp.getLength()] = (byte) -2;
                //adding Mark
                // XORing
                BigInteger ip2 = new BigInteger(rdp.getAddress().getHostAddress().getBytes());
                // XORing
                byte[] xor_ip = ip2.xor(ip1).toByteArray();
                
                System.out.print("{$} Mark ["+convertBigIntegerToString(ip1)+"] + ["+convertBigIntegerToString(ip2)+"] = [");
                for (int i = 0, j = rdp.getLength() + 1; i < xor_ip.length; j++, i++) {
                    packet[j] = xor_ip[i];
                    System.out.print(xor_ip[i]);
                }
                System.out.println("]");
            }
            //Sending packet
            System.out.println("[<] Sending Packet [Packet Size: " + packet.length + "]");
            DatagramPacket sdp = new DatagramPacket(packet, packet.length, ipAddress, nxtRouter);
            senderSocket.send(sdp);
        }
    }

    public void run() {
        try {
            markPacket();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
