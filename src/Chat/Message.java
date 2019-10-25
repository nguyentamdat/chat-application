package Chat;

public class Message {
    private Chat receiver;
    private String message;

    public void setReceiver(Chat receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Chat getReceiver() {
        return receiver;
    }
}
