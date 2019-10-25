package Chat;

public class Friend {
    private String name;
    private boolean status;

    public Friend(String name, boolean status) {
        setName(name);
        setStatus(status);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public boolean isStatus() {
        return status;
    }
}
