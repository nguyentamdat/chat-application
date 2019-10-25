package ChatGUI;
import Chat.Chat;
import java.net.URL;
import java.io.IOException;
import java.io.File;

import javafx.event.ActionEvent;
import org.apache.commons.lang3.StringUtils;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class ControllerChatInterface implements Initializable{
    @FXML
    Button btnAdd;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    String FriendUser;
    public void initUser(Chat user){
        return;
    }
    @FXML
    public void onBtnClicked(MouseEvent e) throws Exception {
        Button button = (Button)e.getSource();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("RegisterConnect.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 250);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

        } catch (Exception er) {
            er.printStackTrace();
        }

    }

}
