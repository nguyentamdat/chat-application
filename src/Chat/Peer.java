package Chat;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.Socket;

public class Peer implements Runnable {

    private Socket _socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String msg;

    public Peer(Socket socket) {
        _socket = socket;
        try {
            dis = new DataInputStream(_socket.getInputStream());
            dos = new DataOutputStream(_socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMsg(String msg) {
        try {
            dos.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private String receiveMsg() {
        try {
            return dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    private void receiveFile(String name) {
        try {
            FileOutputStream fos = new FileOutputStream(new File("./" + name));
            IOUtils.copy(dis, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean processMsg(String arg) {
        String[] args = Protocol.splitMsg(arg);
        if (args[0].equalsIgnoreCase("/bye")) {
            if (args.length > 1 && args[0].equalsIgnoreCase("/msg")) setMsg(args[1]);
            if (args[0].equalsIgnoreCase("/fil")) receiveFile(args[1]);
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        Thread inputThread = new Thread(() -> {

        });
        while (_socket.isConnected()) {
            if (!processMsg(receiveMsg())) break;
        }
        try {
            inputThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
