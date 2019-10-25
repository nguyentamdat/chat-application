package Chat;

import com.sun.webkit.network.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Chat {
    private int port;
    private String servername, username;
    private ServerSocket selfSocket;
    private Socket serverSocket;
    private BufferedReader dis;
    private PrintWriter dos;
    private ConcurrentHashMap<String, Peer> peers;
    private static Chat instance;
    private Peer current;
    private List<Friend> listFriend;

    public void setCurrent(String name) throws Exception {
        Peer res;
        if ((res = peers.get(name)) != null) {
            current = res;
        } else throw new Exception("Not found user");
    }

    public static Chat getInstance() {
        if (instance == null) {
            instance = new Chat();
        }
        return instance;
    }

    private Chat() {}

    public int getPort() {
        return port;
    }

    public String getServername() {
        return servername;
    }

    public String getUsername() {
        return username;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setServername(String servername) {
        this.servername = servername;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setServerSocket(Socket serverSocket) {
        this.serverSocket = serverSocket;
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

    public boolean init(String server, int port, String username) throws Exception{
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
        selfSocket = new ServerSocket(0);
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
            listFriend = refreshListFriend();
            return true;
        }
        System.out.println("Duplicate username, enter another username!");
        throw new Exception("Duplicate username");
        //endregion
    }

    public List<Friend> getListFriend() {
        refreshListFriend();
        return listFriend;
    }

    private List<Friend> refreshListFriend() {
        List<Friend> list = new ArrayList<Friend>();
        dos.println("GET");
        try {
            String res = dis.readLine();
            if (res.equalsIgnoreCase("LIST")) {
                String[] args;
                while (!(res = dis.readLine()).equalsIgnoreCase("END")) {
                    args = Utils.splitMsg(res);
                    list.add(new Friend(args[0], args[1].equalsIgnoreCase("true")));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void sendMsg(String msg) {
        current.addPack(Utils.sendMsg(msg));
    }

    public void sendFile(File file) {
        current.addFile(file);
        current.addPack(Utils.sendFile(file.getName()));
    }
}

