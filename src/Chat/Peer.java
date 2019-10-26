package Chat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Peer implements Runnable {

    public final ObservableList<Message> inbox = FXCollections.observableArrayList();
    final BlockingQueue<String> pack = new LinkedBlockingQueue<>(4096);
    private Socket _socket;
    private BufferedReader in;
    private PrintWriter out;
    private ConcurrentHashMap<String, File> files;
    private String name;

    public Peer(Socket socket, String name) {
        _socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
            out = new PrintWriter(_socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.name = name;
    }

    public void addFile(File file) {
        files.put(file.getName(), file);
    }

    public File getFile(String name) {
        return files.get(name);
    }

    private boolean send(String msg) {
        out.println(msg);
        System.out.println(msg);
        String res = receiveMsg();
        return res.equalsIgnoreCase("$") &&
                addInbox(Chat.getInstance().getUsername(), msg.replace("/msg ", ""));
    }

    public void addPack(String msg) {
        pack.add(msg);
    }

    private boolean addInbox(String user, String msg) {
        inbox.add(new Message(user, msg));
        return true;
    }

    private String receiveMsg() {
        try {
            return in.readLine();
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

    private boolean processMsg(String msg) {
        String[] args = Utils.splitMsg(msg);
        System.out.println(msg);
        if (!args[0].equalsIgnoreCase("/bye")) {
            if (args.length > 1 && args[0].equalsIgnoreCase("/msg")) {
                addInbox(name, args[1]);
                out.println("$");
            }
            if (args[0].equalsIgnoreCase("/file")) {
                receiveFile(args[1]);
                out.println("$");
            }
            return true;
        }
        return false;
    }

    private boolean sendFile(File file) {
        try {
            sendHeaderFile(file.getName());
            FileInputStream fis = new FileInputStream(file);
            DataOutputStream fos = new DataOutputStream(_socket.getOutputStream());
            IOUtils.copy(fis, fos);
            fis.close();
            fos.close();
            return in.readLine().equalsIgnoreCase("$") &&
                    addInbox(Chat.getInstance().getUsername(), file.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private void sendHeaderFile(String name) {
        out.println("/file " + name);
    }

    private boolean senderProcess() throws InterruptedException {
        if (!pack.isEmpty()) {
            String packet = pack.take();
            String type = Utils.getType(packet);
            if (type.equalsIgnoreCase("/msg")) {
                return send(packet);
            }
            if (type.equalsIgnoreCase("/file")) {
                String name = Utils.splitMsg(packet)[1];
                sendHeaderFile(name);
                return sendFile(files.get(name));
            }
            return !type.equalsIgnoreCase("/bye");
        }
        return true;
    }

    private boolean isConnected() {
        return _socket.isConnected();
    }

    private void disconnect() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Start chat with " + name);
        Thread inputThread = new Thread(() -> {
            while (isConnected()) {
                try {
                    if (!senderProcess()) break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        inputThread.start();
        while (isConnected()) {
            if (!processMsg(receiveMsg())) break;
        }
        try {
            inputThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnect();
    }
}
