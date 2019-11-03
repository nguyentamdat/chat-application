package controller;
import chat.Chat;

import java.net.URL;

import com.jfoenix.controls.JFXButton;
import org.apache.commons.lang3.StringUtils;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;

public class ControllerLogin implements Initializable{
    Chat user = Chat.getInstance();
    @FXML
    JFXButton btnLogin;
    @FXML
    TextField portTF, serverTF, usernameTF;
    public AnchorPane mainSelect;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        portTF.setText("6000");
        serverTF.setText("localhost");
    }
    @FXML
    public void onBtnLoginClick(MouseEvent e) throws Exception {
        Button button = (Button)e.getSource();
        if (!StringUtils.isBlank(portTF.getText()) && !StringUtils.isBlank(serverTF.getText()) && !StringUtils.isBlank(usernameTF.getText()) ) {
            int port = Integer.parseInt(portTF.getText());
            String server = serverTF.getText();
            String username = usernameTF.getText();
            try  {
                if (user.init(server, port,username )) user.start();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/ChatInterface.fxml"));
                Parent root = fxmlLoader.load();
                mainSelect.getChildren().setAll(root);
                AnchorPane.setTopAnchor(root, 0.0);
                AnchorPane.setBottomAnchor(root, 0.0);
                AnchorPane.setLeftAnchor(root, 0.0);
                AnchorPane.setRightAnchor(root, 0.0);
                ControllerChatInterface controller = fxmlLoader.getController();
                user.controller = controller;
            }
            catch (Exception er) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle(er.toString());
                alert.setHeaderText(er.toString());
                alert.showAndWait();
                er.printStackTrace();
            }
        }
        else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("ERROR LOGIN PAGE");
            alert.setHeaderText("Missing field value");
            alert.setContentText("Please enter full value");
            alert.showAndWait();
        }
    }
    @FXML
    public void onbtnEnter(MouseEvent e) {
        Button button = (Button)e.getSource();
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
        Button button = (Button)e.getSource();
        button.setEffect(null);
    }
}




