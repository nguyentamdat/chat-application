package Chat;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.*;

public class Peer implements Runnable {

    final BlockingQueue<String> pack = new LinkedBlockingQueue<>(4096);
    private Socket _socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    final BlockingQueue<String> inbox = new LinkedBlockingQueue<>(4096);
    private ConcurrentHashMap<String, File> files;

    public void addFile(File file) {
        files.put(file.getName(), file);
    }

    public File getFile(String name) {
        return files.get(name);
    }

    public Peer(Socket socket) {
        _socket = socket;
        try {
            dis = new DataInputStream(_socket.getInputStream());
            dos = new DataOutputStream(_socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean send(String msg) {
        try {
            dos.writeUTF(msg);
            dos.flush();
            return dis.readChar() == '$';
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addPack(String msg) {
        pack.add(msg);
    }

    public String getInbox() {
        return inbox.poll();
    }

    public void addInbox(String msg) {
        inbox.add(msg);
    }

    private String receiveMsg() {
        try {
            return dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private char receiveHeader() {
        try {
            return dis.readChar();
        } catch (IOException e) {
            e.printStackTrace();
            return '!';
        }
    }

    private void receiveFile() {
        try {
            char ch;
            StringBuilder name = new StringBuilder();
            while ((ch = receiveHeader()) != '#') {
                name.append(ch);
            }
            FileOutputStream fos = new FileOutputStream(new File("./" + name.toString()));
            IOUtils.copy(dis, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean receiverMsg(char cmd) {
        if (cmd != '!') {
            if (cmd == '@') receiveFile();
            if (cmd == '/') {
                String[] args = Utils.splitMsg(Objects.requireNonNull(receiveMsg()));
                if (args[0].equalsIgnoreCase("bye")) {
                    if (args.length > 1 && args[0].equalsIgnoreCase("msg")) {
                        addInbox(args[1]);
                        send("$");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean sendFile(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            IOUtils.copy(fis, dos);
            dos.flush();
            return dis.readChar() == '$';
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private void sendHeaderFile(String name) {
        try {
            dos.writeUTF("@" + name + "#");
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void senderProcess() throws InterruptedException {
        if (!pack.isEmpty()) {
            String packet = pack.take();
            String type = Utils.getType(packet);
            if (type.equalsIgnoreCase("/msg")) send(packet);
            if (type.equalsIgnoreCase("/file")) {
                String name = Utils.splitMsg(packet)[1];
                sendHeaderFile(name);
                sendFile(files.get(name));
            }
        }
    }

    private boolean isConnected() {
        return _socket.isConnected();
    }

    @Override
    public void run() {
        Thread inputThread = new Thread(() -> {
            while (isConnected()) {
                try {
                    senderProcess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        while (isConnected()) {
            if (!receiverMsg(receiveHeader())) break;
        }
        try {
            inputThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
