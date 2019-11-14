package controller;

import chat.Chat;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerChatAdmin implements Initializable {
    @FXML
    TextField input;
    @FXML
    TextArea msg;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        msg.setDisable(true);
    }

    @FXML
    public void onEnter(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            Chat.getInstance().sendServer("MSG guest " + input.getText());
            msg.setText(msg.getText() + "\n" + input.getText());
            input.setText("");
        }
    }

    public void addMsg(String mes) {
        msg.setText(msg.getText() + "\n" + mes);
    }
}
