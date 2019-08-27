package hacker;


import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataTransfer extends Thread {

    private String data;
    private InetAddress ipAddress;
    private int routerPort;
    DatagramSocket dsoc;

    public DataTransfer(String data, String ipAddress, int routerPort) {
        this.data = data;
        this.routerPort = routerPort;
        try {
            this.ipAddress = InetAddress.getByName(ipAddress);
        dsoc = new DatagramSocket();
        } catch (UnknownHostException ex) {
            Logger.getLogger(DataTransfer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(DataTransfer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendData() throws Exception {
        System.out.println("[*]  Sending packet [Size: "+data.getBytes().length+"]");
        dsoc.send(new DatagramPacket(data.getBytes(), data.getBytes().length, ipAddress, routerPort));
        Thread.sleep(20);
    }
    
    @Override
    public void run() {
        try {
            sendData();
            dsoc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
