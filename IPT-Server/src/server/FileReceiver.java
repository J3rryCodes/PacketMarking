package server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class FileReceiver extends Thread {

    private static final int MAX_LEN = 544;
    private File file;
    private InetAddress ipAddress;
    private String fName;
    private DatagramSocket dsoc;
    private ArrayList<byte[]> mark;
    private ArrayList<byte[]> data;
    private ServerGUI serverGUI;
    private boolean isEncrypt;

    public FileReceiver(int serverPort, boolean flag) {
        this.mark = new ArrayList<>();
        this.data = new ArrayList<>();
        this.isEncrypt = flag;
        try {
            dsoc = new DatagramSocket(serverPort);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Receiver Started " + serverPort + ".....");
    }

    @Override
    public void run() {
        while(true){
        try {
            System.out.println("Listening Started");
            System.out.println("IS PACKET MARKING ENABLED :" + isEncrypt);
            if (isEncrypt) {
                receiveMarkedData();
                //new PackageAnalizer(mark, data).dataInfo();
            } else {
                receiveData();
            }
            SourceIPReconstructor.setMark(mark);
            System.out.println("Listening Completed");
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }
    
    //noraml data
    public void receiveData() throws IOException{
        while (true) {
            byte b[] = new byte[MAX_LEN];
            DatagramPacket dp = new DatagramPacket(b, b.length);
            dsoc.receive(dp);
            System.out.println("[>] Received Packet [Size: " + dp.getLength() + "]");
            
            //eof file
            if(dp.getData()[0]==(byte)-1)
                break;
            data.add(dp.getData());
            mark.add(dp.getAddress().getAddress());
        }
        //refreshing window and showing data name
        showData();
    }
    //marked encrypted data
    public void receiveMarkedData() throws IOException {
        while (true) {
            byte b[] = new byte[MAX_LEN];
            DatagramPacket dp = new DatagramPacket(b, b.length);
            dsoc.receive(dp);
            System.out.println("[>] Received Packet [Size: " + dp.getLength() + "]");

            byte[] rData = new byte[dp.getLength()];
            for (int i = 0; i < rData.length; i++) {
                rData[i] = dp.getData()[i];
            }
            byte[] dcp = CryptoUtils.decrypt(rData);
            byte[] inData = (dcp != null) ? dcp : rData;

            int beginMark = 0;
            for (int i = 0; i < inData.length; i++) {
                System.out.print(inData[i]);
                if (inData[i] == (byte) -2) {
                    beginMark = i + 1;
                }
            }
            System.out.println("");
            byte[] data = new byte[beginMark];
            byte[] mark = new byte[inData.length - beginMark];
            for (int i = 0; i < inData.length; i++) {
                if (i < beginMark - 1) {
                    data[i] = inData[i];
                } else if (i >= beginMark) {
                    mark[i - beginMark] = inData[i];
                }
            }
            if (data[0] == -1) {
                break;
            }
            this.mark.add(mark);
            this.data.add(data);
            System.out.println("[M] Mark Length : [" + mark.length + "]");
            System.out.println("[D] Data Length : [" + data.length + "]");
            //End of transation
        }
        showData();
    }

    void showData() {
        String d = "";
        String name = new String(data.get(0));
        for (int i = 1; i < data.size(); i++) {
            d += new String(data.get(i));
        }
        serverGUI.refreshFrame(name, d);
    }

    public void setServerGUI(ServerGUI serverGUI) {
        this.serverGUI = serverGUI;
    }

}
