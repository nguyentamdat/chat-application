package Chat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Peer extends Thread {

    final BlockingQueue<String> pack = new LinkedBlockingQueue<>(4096);
    public volatile ObservableList<Message> inbox = Chat.getInstance().getListMsg();
    public int port;
    private Socket _socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String name;
    private boolean keepGo = true;

    public Peer(Socket socket) {
        _socket = socket;
        open();
    }

    private void open() {
        try {
            System.out.println("Initialize IO");
            out = new ObjectOutputStream(_socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(_socket.getInputStream());
            System.out.println("IO done");
        } catch (IOException e) {
            System.out.println("Error Peer: open()");
        }
    }

    public void send(Message msg) {
        try {
            System.out.println("Send from: " + _socket.getLocalPort() + " to " + _socket.getPort());
            out.writeObject(msg);
            out.flush();
            if (msg.getMessage()) inbox.add(msg);
        } catch (IOException e) {
            System.out.println("Error Peer: send()");
            e.printStackTrace();
        }
    }

    public void addPack(String msg) {
        pack.add(msg);
    }

    private Message receiveMsg() {
        try {
            System.out.println("Receive from: " + _socket.getPort() + " to " + _socket.getLocalPort());
            return (Message) in.readObject();
        } catch (IOException e) {
            System.out.println("Error Peer: receiveMsg() - IO");
        } catch (ClassNotFoundException e) {
            System.out.println("Error Peer: receiveMsg() - Class");
        }
        return null;
    }

    private void processMsg(Message msg) {
        if (msg == null) return;
        String type = msg.getType();
        switch (type) {
            case "bye":
                keepGo = false;
                break;
            case "msg":
                inbox.add(msg);
                break;
            case "file":
                String[] msgSep = msg.getMessage().split(":");
                new Thread(new Download(_socket.getInetAddress(), Integer.parseInt(msgSep[0]), "./" + msgSep[1])).start();
                break;
            case "start":
                name = msg.getFrom();
                Chat.getInstance().addPeer(name, this);
                send(new Message("hello", Chat.getInstance().getUsername(), name, ""));
                Chat.getInstance().setCurrent(name);
                break;
            case "hello":
                break;
        }
    }

    private boolean isConnected() {
        return _socket.isConnected();
    }

    private void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (_socket != null) _socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Start receive from " + _socket.getPort());
        while (isConnected() && keepGo) {
            processMsg(receiveMsg());
        }
        close();
    }
}
