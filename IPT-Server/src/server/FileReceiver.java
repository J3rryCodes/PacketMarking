package server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class FileReceiver extends Thread {

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
            byte b[] = new byte[528];
            DatagramPacket dp = new DatagramPacket(b, b.length);
            dsoc.receive(dp);
            System.out.println("[>] Received Packet [Size: "+dp.getLength()+"]");
            
            //data
            byte[] data = new byte[1];
            byte t;
            int count=0;
            boolean x=false;
            for(int i=0;i<dp.getLength();i++){
                //Data length
                if(dp.getData()[i]==(byte)-2){
                    data=new byte[i];
                    x=true;
                    continue;
                }
                //Mark length
                if(x){
                    count++;
                }
            }
            byte[] ip = new byte[count];
            
            System.out.print("{$} Mark : [");
            for(int i=0;i<dp.getLength();i++){
                if(i<data.length)
                    data[i]=dp.getData()[i];
                if(i>data.length){
                    ip[i-(data.length+1)]=dp.getData()[i];
                    System.out.print(dp.getData()[i]);
                }
            }
            System.out.println("]");
            this.mark.add(ip);
            this.data.add(data);
            System.out.println("[M] Mark Length : ["+ip.length+"]");
            System.out.println("[D] Data Length : ["+data.length+"]");
            //End of transation
            if(data[0]==-1)
                break;
        }
    }

}
