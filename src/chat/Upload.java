package chat;

import org.apache.commons.io.IOUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Upload implements Runnable {
    public int port;
    public FileInputStream in;
    public DataOutputStream out;
    public ServerSocket server;
    public File file;
    public Socket socket;


    public Upload(File file) {
        try {
            server = new ServerSocket(0);
            port = server.getLocalPort();
            this.file = file;
        } catch (Exception e) {
            System.out.println("Error Upload: Upload()");
        }
    }

    @Override
    public void run() {
        try {
            socket = server.accept();
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
