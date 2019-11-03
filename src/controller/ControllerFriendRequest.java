package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerFriendRequest implements Initializable {
    @FXML
    JFXButton btnAdd, btnCancel;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    @FXML
    public void onbtnCancelClicked(MouseEvent e) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void onbtnAddClicked(MouseEvent e) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void onbtnEnter(MouseEvent e) {
        Button button = (Button) e.getSource();
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
        Button button = (Button) e.getSource();
        Glow glowfx = new Glow();
        glowfx.setLevel(0);
        button.setEffect(null);
    }
}
