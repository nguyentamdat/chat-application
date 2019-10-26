package Chat;

import org.apache.commons.io.IOUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Upload implements Runnable {
    public String addr;
    public int port;
    public FileInputStream in;
    public DataOutputStream out;
    public File file;
    public Socket socket;

    public Upload(String addr, int port, File filepath) {
        try {
            file = filepath;
            this.addr = addr;
            this.port = port;
        } catch (Exception e) {
            System.out.println("Error Upload: Upload()");
        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket(InetAddress.getByName(addr), port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new FileInputStream(file);
            IOUtils.copy(in,out);
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Error Upload: run()");
        }
    }
}
