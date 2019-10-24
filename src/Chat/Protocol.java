package Chat;

public class Protocol {
    public String makeMsg(String type ,String[] str) {
        StringBuilder typeBuilder = new StringBuilder(type);
        for (String i : str) {
            typeBuilder.append(" ").append(i);
        }
        type = typeBuilder.toString();
        return type;
    }

    public String[] splitMsg(String msg) {
        return msg.split(" ");
    }


}
