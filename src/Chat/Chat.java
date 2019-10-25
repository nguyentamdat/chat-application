package Chat;

public class Chat {
    private int port;
    private String server, username;
    public Chat(String Server, String Username, int Port){
        server = Server;
        username = Username;
        port = Port;
    }
    public int getPort() {
        return port;
    }

    public String getServer() {
        return server;
    }

    public String getUsername() {
        return username;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
