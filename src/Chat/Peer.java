package Chat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Peer extends Thread {

    public ObservableList<Message> inbox = FXCollections.observableArrayList();
    final BlockingQueue<String> pack = new LinkedBlockingQueue<>(4096);
    public int port;
    private Socket _socket = null;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ConcurrentHashMap<String, File> files;
    private String name;
    private boolean keepGo;
    private ServerSocket server = null;

    public Peer(ServerSocket server) {
        this.server = server;
        port = server.getLocalPort();
    }

    public Peer(Socket socket) {
        _socket = socket;
        open();
    }

    private void open() {
        try {
            in = new ObjectInputStream(_socket.getInputStream());
            out = new ObjectOutputStream(_socket.getOutputStream());
            out.flush();
        } catch (IOException e) {
            System.out.println("Error Peer: open()");
        }
    }

    public void initChat(){
        try {
            Message msg = (Message) in.readObject();
            Socket socket;
            if (msg.getType().equalsIgnoreCase("start")) {
                socket = new Socket(_socket.getInetAddress(), Integer.parseInt(msg.getMessage().split(",")[0]));
                close();
                _socket = socket;
                open();
                name = msg.getMessage().split(",")[1];
                Chat.getInstance().addPeer(name, this);
                return;
            }
            throw new IOException();
        } catch (IOException e) {
            System.out.println("Error Peer: initChat() - IO");
        } catch (ClassNotFoundException e) {
            System.out.println("Error Peer: initChat() - Class");
        }
    }

    public void addFile(File file) {
        files.put(file.getName(), file);
    }

    public File getFile(String name) {
        return files.get(name);
    }

    public void send(Message msg) {
        try {
            out.writeObject(msg);
            inbox.add(msg);
        } catch (IOException e) {
            System.out.println("Error Peer: send()");
        }
    }

    public void addPack(String msg) {
        pack.add(msg);
    }

    private void receiveMsg() {
        try {
            Message msg = (Message) in.readObject();
            inbox.add(msg);
        } catch (IOException e) {
            System.out.println("Error Peer: receiveMsg() - IO");
        } catch (ClassNotFoundException e) {
            System.out.println("Error Peer: receiveMsg() - Class");
        }
    }

    private void receiveFile(String filename) {
        try {
            DataInputStream fis = new DataInputStream(_socket.getInputStream());
            FileOutputStream fos = new FileOutputStream(new File("./" + filename));
            IOUtils.copy(fis, fos);
            fos.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processMsg(String msg) {
        String type = Utils.getType(msg);
        if (!type.equalsIgnoreCase("/bye")) {
            switch (type) {
                case "/msg": {
                    break;
                }
                case "/file": {
                    receiveFile(Utils.splitMsg(msg)[1]);
                    break;
                }
            }
        }
    }

    private void sendFile(File file) {
        try {
            sendHeaderFile(file.getName());
            FileInputStream fis = new FileInputStream(file);
            DataOutputStream fos = new DataOutputStream(_socket.getOutputStream());
            IOUtils.copy(fis, fos);
            fis.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendHeaderFile(String name) {
    }

    private void senderProcess() throws InterruptedException {
        String packet = pack.poll(1, TimeUnit.SECONDS);
        if (packet != null) {
            String type = Utils.getType(packet);
            System.out.println(type);
            if (type.equalsIgnoreCase("/msg")) {
            }
            if (type.equalsIgnoreCase("/file")) {
                String name = Utils.splitMsg(packet)[1];
                sendHeaderFile(name);
                sendFile(files.get(name));
            }
            keepGo = !type.equalsIgnoreCase("/bye");
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
        if (server != null) {
            try {
                _socket = server.accept();
                open();
            } catch (IOException e) {
                System.out.println("Error Peer: run() - server");
            }
        }
        System.out.println("Start receive from " + name);
        while (isConnected() && keepGo) {
                receiveMsg();
        }
        close();
    }
}
