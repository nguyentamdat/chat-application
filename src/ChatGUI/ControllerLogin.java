package ChatGUI;
import Chat.Chat;

import java.net.ConnectException;
import java.net.URL;
import java.io.IOException;
import java.io.File;

import com.jfoenix.controls.JFXButton;
import org.apache.commons.lang3.StringUtils;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Platform;
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
                user.init(server, port,username );
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChatInterface.fxml"));
                Parent root = fxmlLoader.load();
                mainSelect.getChildren().setAll(root);
                AnchorPane.setTopAnchor(root, 0.0);
                AnchorPane.setBottomAnchor(root, 0.0);
                AnchorPane.setLeftAnchor(root, 0.0);
                AnchorPane.setRightAnchor(root, 0.0);
                ControllerChatInterface controller = fxmlLoader.getController();
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
}




