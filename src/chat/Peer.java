package chat;

import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Peer extends Thread {

    public volatile List<Message> inbox = new LinkedList<>();
    public int port;
    private Socket _socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    public String name;
    public boolean keepGo = true;

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
            if (!msg.getMessage().equalsIgnoreCase("")) {
                inbox.add(msg);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Chat.getInstance().listMsg.add(msg);
                    }
                });
            }
        } catch (IOException e) {
            System.out.println("Error Peer: send()");
            e.printStackTrace();
        }
    }

    private void addToListMsg(Message msg) {
        if (msg.getFrom().equalsIgnoreCase(Chat.getInstance().current.name)) {
            Chat.getInstance().listMsg.add(msg);
        }
    }

    private Message receiveMsg() {
        try {
            System.out.println("Receive from: " + _socket.getPort() + " to " + _socket.getLocalPort());
            return (Message) in.readObject();
        } catch (IOException e) {
            System.out.println("Error Peer: receiveMsg() - IO");
            keepGo = false;
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
                Chat.getInstance().peers.remove(msg.getTo());
                keepGo = false;
                break;
            case "msg":
                inbox.add(msg);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Chat.getInstance().setCurrent(name);
                        Chat.getInstance().controller.setLblName(name);
                        addToListMsg(msg);
                    }
                });
                break;
            case "file":
                String[] msgSep = msg.getMessage().split(":");
                new Thread(new Download(_socket.getInetAddress(), Integer.parseInt(msgSep[0]), "D:/" + msgSep[1])).start();
                inbox.add(msg);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Chat.getInstance().setCurrent(name);
                        Chat.getInstance().controller.setLblName(name);
                        addToListMsg(msg);
                    }
                });
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

    public void leaveChat() {
        send(new Message("bye", Chat.getInstance().getUsername(), name, ""));
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
