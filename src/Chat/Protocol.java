package Chat;

import org.jetbrains.annotations.NotNull;

public class Protocol {
    @NotNull
    public static String makeMsg(String type , @NotNull String[] str) {
        StringBuilder typeBuilder = new StringBuilder(type);
        for (String i : str) {
            typeBuilder.append(" ").append(i);
        }
        type = typeBuilder.toString();
        return type;
    }

    static String[] splitMsg(@NotNull String msg) {
        return msg.split(" ");
    }


}
