package controller;

import chat.Chat;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerRequestedFriend implements Initializable {
    @FXML
    JFXButton btnAccept, btnDeny;
    @FXML
    Label lblReqName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void Setname(String name){
        lblReqName.setText(name);
    }
    @FXML
    public void onbtnDenyClicked(MouseEvent e) {
        Stage stage = (Stage) btnDeny.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void onbtnAcceptClicked(MouseEvent e) {
        Chat instance = Chat.getInstance();
        instance.sendServer("ACCEPTED " + lblReqName.getText());
        Stage stage = (Stage) btnDeny.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void onbtnEnter(MouseEvent e) {
        javafx.scene.control.Button button = (javafx.scene.control.Button) e.getSource();
        Glow glowfx = new Glow();
        glowfx.setLevel(1);
        //Instantiating the Shadow class
        DropShadow dropShadow = new DropShadow();
        //dropShadow.setBlurType(BlurType.GAUSSIAN);
        dropShadow.setColor(Color.WHITE);
        dropShadow.setHeight(20);
        dropShadow.setWidth(20);
        dropShadow.setRadius(2.5);
        dropShadow.setSpread(10);
        button.setEffect(dropShadow);
    }

    @FXML
    public void onbtnLeave(MouseEvent e) {
        javafx.scene.control.Button button = (javafx.scene.control.Button) e.getSource();
        Glow glowfx = new Glow();
        glowfx.setLevel(0);
        button.setEffect(null);
    }
}
