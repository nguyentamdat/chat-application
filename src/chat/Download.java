package chat;

import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Download implements Runnable {
    public Socket socket;
    public DataInputStream in;
    public FileOutputStream out;
    public String saveTo = "";
    public InetAddress addr;
    public int port;

    public Download(InetAddress addr, int port,String saveTo) {
        try {
            this.addr = addr;
            this.saveTo = saveTo;
            this.port = port;
        } catch (Exception e) {
            System.out.println("Error Download: Download()");
        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket(addr, port);
            in = new DataInputStream(socket.getInputStream());
            out = new FileOutputStream(saveTo);
            IOUtils.copy(in, out);
            out.close();
            in.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Error Download: run()");
        }
    }
}
