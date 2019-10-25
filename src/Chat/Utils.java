package Chat;


import java.io.File;

public class Utils {
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

    static String getType(String msg) {
        return splitMsg(msg)[0];
    }

    static String sendMsg(String msg) {
        String[] msgs = { msg };
        return makeMsg("/msg", msgs);
    }

    static String sendFile(String name) {
        String[] msgs = { name };
        return makeMsg("/file", msgs);
    }
}
