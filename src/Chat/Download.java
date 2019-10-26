package Chat;

import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Download implements Runnable {
    public int port;
    public ServerSocket server;
    public Socket socket;
    public DataInputStream in;
    public FileOutputStream out;
    public String saveTo = "";

    public Download(String saveTo) {
        try {
            server = new ServerSocket(0);
            port = server.getLocalPort();
            this.saveTo = saveTo;
        } catch (Exception e) {
            System.out.println("Error Download: Download()");
        }
    }

    @Override
    public void run() {
        try {
            socket = server.accept();
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
