package Chat;


import com.sun.webkit.network.Util;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Utils {
    public Socket socket;
    public ObjectInputStream in;
    public ObjectOutputStream out;

    public Utils(Socket sk) {
        socket = sk;
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
        } catch (IOException e) {
            System.out.println("Error Utils: Utils()");
        }
    }

    public Message read() {
        try {
            return (Message) in.readObject();
        } catch (IOException e) {
            System.out.println("Error Utils: read() - IO");
        } catch (ClassNotFoundException e) {
            System.out.println("Error Utils: read() - Class");
        }
        return null;
    }

    public void write(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error Utils: write()");
        }
    }
}

