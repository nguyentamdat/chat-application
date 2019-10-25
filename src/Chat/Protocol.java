package Chat;


import java.io.File;

public class Protocol {
    public static String makeMsg(String type , String[] str) {
        StringBuilder typeBuilder = new StringBuilder(type);
        for (String i : str) {
            typeBuilder.append(" ").append(i);
        }
        type = typeBuilder.toString();
        return type;
    }

    static String[] splitMsg(String msg) {
        return msg.split(" ");
    }

    static File getFile(String name) {
        return new File(name);
    }

    static String sendMsg(String msg) {
        String[] msgs = { msg };
        return makeMsg("/msg", msgs);
    }
}
