package Chat;

public class Message {
    private String user;
    private String message;

    public Message(String user, String msg) {
        setMessage(msg);
        setUser(user);
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }
}
