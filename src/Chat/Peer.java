package Chat;

import org.apache.commons.io.IOUtils;
import java.io.*;
import java.net.Socket;

public class Peer implements Runnable {

    private Socket _socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private volatile String inbox;
    private volatile String outbox;

    public Peer(Socket socket) {
        _socket = socket;
        try {
            dis = new DataInputStream(_socket.getInputStream());
            dos = new DataOutputStream(_socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(String msg) {
        try {
            dos.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String receiveMsg() {
        try {
            return dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setInbox(String inbox) {
        this.inbox = inbox;
    }

    public String getInbox() {
        return inbox;
    }

    private void receiveFile(String name) {
        try {
            FileOutputStream fos = new FileOutputStream(new File("./" + name));
            IOUtils.copy(dis, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean receiverMsg(String arg) {
        String[] args = Protocol.splitMsg(arg);
        if (args[0].equalsIgnoreCase("/bye")) {
            if (args.length > 1 && args[0].equalsIgnoreCase("/msg")) setInbox(args[1]);
            if (args[0].equalsIgnoreCase("/fil")) receiveFile(args[1]);
            return true;
        }
        return false;
    }

    public void setOutbox(String outbox) {
        this.outbox = outbox;
    }

    public String getOutbox() {
        return outbox;
    }

    public void sendMsg(String msg) {
        setOutbox(Protocol.sendMsg(msg));
    }

    private void senderProcess() {
        if (getOutbox() != null) {
            send(getOutbox());
            setOutbox(null);
        }
    }

    private boolean isConnected() {
        return _socket.isConnected();
    }

    @Override
    public void run() {
        Thread inputThread = new Thread(() -> {
            while (isConnected()) {
                senderProcess();
            }
        });
        while (isConnected()) {
            if (!receiverMsg(receiveMsg())) break;
        }
        try {
            inputThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
