package chat;

import controller.ControllerChatInterface;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Chat extends Thread {
    private static Chat instance;
    public ConcurrentHashMap<String, Peer> peers;
    public volatile ObservableList<Message> listMsg = FXCollections.observableArrayList();
    public volatile Peer current;
    public ControllerChatInterface controller;
    private int port;
    private String servername, username;
    private ServerSocket selfSocket;
    private Socket serverSocket;
    private BufferedReader dis;
    private PrintWriter dos;
    public ObservableList<Friend> listFriend = FXCollections.observableArrayList();
    public boolean keepGo = true;

    private Chat() {
        peers = new ConcurrentHashMap<>(200);
    }

    public static Chat getInstance() {
        if (instance == null) {
            instance = new Chat();
            instance.setDaemon(true);
        }
        return instance;
    }

    public ObservableList<Message> getListMsg() {
        return listMsg;

    }

    public boolean setCurrent(String name) {
        Peer res;
        if ((res = peers.get(name)) != null) {
            current = res;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    listMsg.clear();
                    listMsg.addAll(current.inbox);
                }
            });
            return true;
        }
        return false;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServername() {
        return servername;
    }

    public void setServername(String servername) {
        this.servername = servername;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Peer getPeer(String name) {
        return peers.get(name);
    }

    public void addPeer(String name, Peer peer) {
        peers.put(name, peer);
    }

    public void removePeer(String name) {
        peers.remove(name);
    }

    public Socket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public boolean init(String server, int port, String username) throws Exception {
        setUsername(username);
        setPort(port);
        setServername(server);
        return init();
    }

    private boolean init() throws Exception {
        //region start connect to server
        setServerSocket(new Socket(getServername(), getPort()));
        Socket server = getServerSocket();
        System.out.println("Connected to server on port: " + server.getPort() + " at: " + server.getInetAddress());
        //endregion
        //region init reader and writer on server
        dis = new BufferedReader(new InputStreamReader(server.getInputStream()));
        dos = new PrintWriter(server.getOutputStream(), true);
        //endregion
        //region start local server as peer
        if (selfSocket == null) selfSocket = new ServerSocket(0);
        int selfPort = selfSocket.getLocalPort();
        System.out.println("Start peer on port: " + selfPort + " at: " + selfSocket.getInetAddress());
        //endregion
        //region send login information to server
        DataInputStream cmd = new DataInputStream(System.in);
        String msg = "LOGIN " + username + " " + selfPort;
        dos.println(msg);
        System.out.println(msg);
        String res = dis.readLine();
        System.out.println(res);
        if (res.equalsIgnoreCase("true")) {
            System.out.println("You can chat now!");
            return true;
        }
        System.out.println("Duplicate username, enter another username!");
        throw new Exception("Duplicate username");
        //endregion
    }

    public void refreshListFriend() {
        sendServer("GET");
    }

    public void sendMsg(String msg) {
        System.out.println("Sending message: " + msg);
        current.send(new Message("msg", username, current.name, msg));
        System.out.println("Sent");
    }

    public void byeMsg() {
        current.send(new Message("bye", username, current.name, ""));
        current.keepGo = false;
        peers.remove(current.name);
    }

    public void sendFile(String filepath) {
        File file = new File(filepath);
        Upload up = new Upload(file);
        new Thread(up).start();
        current.send(new Message("file", username, current.name, up.port + ":" + file.getName()));
    }

    public boolean chatWith(String name) {
        if (!peers.containsKey(name)) {
            return startWith(name);
        }
        return setCurrent(name);
    }

    private boolean startWith(String name) {
        dos.println("GET " + name);
        return true;
    }

    public void leaveChat() {
        dos.println("LOGOUT");
        for (Peer peer : peers.values()) {
            peer.leaveChat();
        }
    }

    public void addFriend(String name) {
        sendServer("ADD " + name);
    }

    private void processMsgFromServer() {
        while (true) {
            try {
                String[] args = dis.readLine().split(" ");
                System.out.println("From server" + args[0]);
                if (args[0].equalsIgnoreCase("FOUND")) {
                    String name = args[1];
                    String[] ip_port = args[2].replaceAll("/","").split(":");
                    Socket socket = new Socket(ip_port[0], Integer.parseInt(ip_port[1]));
                    Peer p = new Peer(socket);
                    addPeer(name, p);
                    p.setDaemon(true);
                    p.start();
                    p.send(new Message("start", username, (p.name = name), ""));
                    setCurrent(name);
                }
                if (args[0].equalsIgnoreCase("REQUEST")) {
                    controller.FriendRequest(args[1]);
                }
                if (args[0].equalsIgnoreCase("LIST")) {
                    String[] f;
                    Platform.runLater(() -> {
                        listFriend.clear();
                    });
                    while (!(f = dis.readLine().split(" "))[0].equalsIgnoreCase("END")) {
                        String[] finalF = f;
                        Platform.runLater(() -> {
                            listFriend.add(new Friend(finalF[0], finalF[1].equalsIgnoreCase("true")));
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendServer(String msg) {
        dos.println(msg);
    }

    @Override
    public void run() {
        new Thread(() -> {
            processMsgFromServer();
        }).start();
        while (keepGo) {
            try {
                Socket socket = selfSocket.accept();
                Peer p = new Peer(socket);
                p.setDaemon(true);
                p.start();
                System.out.println("Port accept: " + socket.getLocalPort());
            } catch (Exception e) {
                System.out.println("Error Chat: run()");
            }
        }
        leaveChat();
    }
}

