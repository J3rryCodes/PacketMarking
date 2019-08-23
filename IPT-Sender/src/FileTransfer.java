
import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileTransfer extends Thread {

    private File file;
    private String fileName;
    private InetAddress ipAddress;
    private int routerPort;
    DatagramSocket dsoc;

    public FileTransfer(File file, String ipAddress, int routerPort) {
        this.file = file;
        this.fileName = file.getName();
        this.routerPort = routerPort;
        try {
            this.ipAddress = InetAddress.getByName(ipAddress);
        dsoc = new DatagramSocket();
        } catch (UnknownHostException ex) {
            Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(FileTransfer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendFille() throws Exception {
        System.out.println("Sending Started");
        byte b[] = new byte[512];
        FileInputStream f = new FileInputStream(file);

        while (f.read(b) != -1) {
            sendData(b);
        }
        Thread.sleep(1000);
        sendData(new byte[]{(byte)-1});
        f.close();
        dsoc.close();

        System.out.println("Sending Completed");
    }

    private void sendFileName() throws Exception {
        byte t[] = fileName.getBytes();
        sendData(t);
    }

    private void sendData(byte[] b) throws Exception {
        System.out.println("[*]  Sending packet [Size: "+b.length+"]");
        dsoc.send(new DatagramPacket(b, b.length, ipAddress, routerPort));
        Thread.sleep(20);
    }
    
    @Override
    public void run() {
        try {
            sendFileName();
            sendFille();
            dsoc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
