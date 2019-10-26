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
        initIO();
        String[] args = Utils.splitMsg(receiveMsg());
        if (args[0].equalsIgnoreCase("/start")) {
            name = args[1];
            Chat.getInstance().addPeer(args[1], this);
        }
    }

    private void initIO() {
        try {
            in = new ObjectInputStream(_socket.getInputStream());
            out = new ObjectOutputStream(_socket.getOutputStream());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
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

    private String receiveMsg() {
        try {
            String msg = in.readLine();
            System.out.println("msg: " + msg);
            return msg;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
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

    private void disconnect() {
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
        System.out.println("Start chat with " + name);
        while (isConnected() && keepGo) {
            try {
                senderProcess();
            } catch (Exception e) {
                e.printStackTrace();
            }
            processMsg(receiveMsg());
        }
        disconnect();
    }
}
