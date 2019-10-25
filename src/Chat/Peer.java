package Chat;

import org.apache.commons.io.IOUtils;
import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Peer implements Runnable {

    private Socket _socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private volatile String inbox;
    private volatile Map<String, File> files;
    final BlockingQueue<String> pack = new LinkedBlockingDeque<>(4096);

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
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setInbox(String inbox) {
        this.inbox = inbox;
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

    public String getInbox() {
        return inbox;
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
                    if (args.length > 1 && args[0].equalsIgnoreCase("msg")) setInbox(args[1]);
                    return true;
                }
            }
        }
        return false;
    }

    public void sendMsg(String msg) {
        pack.add(Utils.sendMsg(msg));
    }

    public void sendFile(String[] names) {
        File file;
        for (String name: names) {
            file = new File(name);
            files.put(file.getName(), file);
            pack.add(Utils.sendFile(file.getName()));
        }
    }

    public void sendFile(String name) {
        String[] names = {name};
        sendFile(names);
    }

    private void sendFile(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            IOUtils.copy(fis, dos);
            dos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendHeaderFile(String name) {
        try {
            dos.writeUTF("@"+name+"#");
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
            };
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
