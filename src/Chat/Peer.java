package Chat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Peer extends Thread {

    public ObservableList<Message> inbox = FXCollections.observableArrayList();
    final BlockingQueue<String> pack = new LinkedBlockingQueue<>(4096);
    private Socket _socket;
    private BufferedReader in;
    private PrintWriter out;
    private ConcurrentHashMap<String, File> files;
    private String name;
    private boolean keepGo;

    public Peer(Socket socket, String name) {
        _socket = socket;
        initIO();
        out.println("/start " + Chat.getInstance().getUsername());
        this.name = name;
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
            in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
            out = new PrintWriter(_socket.getOutputStream(), true);
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

    private void send(String msg) {
        out.println(msg);
        addInbox(Chat.getInstance().getUsername(), msg.replace("/msg ", ""));
    }

    public void addPack(String msg) {
        pack.add(msg);
    }

    private void addInbox(String user, String msg) {
//        inbox.add(new Message(user, msg));
        System.out.println("add: " + msg);
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
            addInbox(name, filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processMsg(String msg) {
        String type = Utils.getType(msg);
        if (!type.equalsIgnoreCase("/bye")) {
            switch (type) {
                case "/msg": {
                    addInbox(name, Utils.splitMsg(msg)[1]);
                    out.println("$");
                    break;
                }
                case "/file": {
                    receiveFile(Utils.splitMsg(msg)[1]);
                    out.println("$");
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
            addInbox(Chat.getInstance().getUsername(), file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendHeaderFile(String name) {
        out.println("/file " + name);
    }

    private void senderProcess() throws InterruptedException {
        String packet = pack.poll(1, TimeUnit.SECONDS);
        if (packet != null) {
            String type = Utils.getType(packet);
            System.out.println(type);
            if (type.equalsIgnoreCase("/msg")) {
                send(packet);
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
