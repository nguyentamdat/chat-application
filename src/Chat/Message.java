package Chat;

import java.io.Serializable;

public class Message implements Serializable {
    private String type;
    private Object content;

    public  Message(String type, Object content) {
        setContent(content);
        setType(type);
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getContent() {
        return content;
    }

    public String getType() {
        return type;
    }
}
