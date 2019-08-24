package server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class FileReceiver extends Thread {

    private static final int MAX_LEN=544;
    File file;
    InetAddress ipAddress;
    String fName;
    DatagramSocket dsoc;
    ArrayList<byte[]> mark;
    ArrayList<byte[]> data;

    public FileReceiver(int serverPort) {
        mark = new ArrayList<>();
        data = new ArrayList<>();
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
                receiveData();
                new PackageAnalizer(mark, data).dataInfo();
                System.out.println("Listening Completed");
                new PackageAnalizer(mark, data).dataInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void receiveData() throws IOException {
        boolean flag=true;
        while (flag) {
            byte b[] = new byte[MAX_LEN];
            DatagramPacket dp = new DatagramPacket(b, b.length);
            dsoc.receive(dp);
            System.out.println("[>] Received Packet [Size: "+dp.getLength()+"]");
            
            byte[] rData=new byte[dp.getLength()];
            for(int i=0;i<rData.length;i++){
                rData[i]=dp.getData()[i];
            }
            byte[] dcp=CryptoUtils.decrypt(rData);
            byte[] inData=(dcp!=null)?dcp:rData;
            
            int beginMark=0;
            for(int i=0;i<inData.length;i++){
                System.out.print(inData[i]);
                if(inData[i]==(byte) -2)
                    beginMark=i+1;
            }
            System.out.println("");
            byte[] data = new byte[beginMark];
            byte[] mark = new byte[inData.length-beginMark];
            for(int i=0;i<data.length;i++)
                if(i<beginMark-1)
                    data[i]=inData[i];
                else if(i>=beginMark)
                    mark[i-beginMark]=inData[i];
                    
            this.mark.add(mark);
            this.data.add(data);
            System.out.println("[M] Mark Length : ["+mark.length+"]");
            System.out.println("[D] Data Length : ["+data.length+"]");
            //End of transation
            if(data[0]==-1)
                break;
        }
    }

}
