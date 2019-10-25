package ChatGUI;
import Chat.Friend;
import Chat.Chat;
import java.net.URL;
import java.io.IOException;
import java.io.File;

import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
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
    Chat user = Chat.getInstance();
    @FXML
    Button btnAdd;
    @FXML
    ListView<Friend> listFriend;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listFriend.setCellFactory(lv -> new FriendCell());
        listFriend.setItems(user.getListFriend());
        listFriend.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Friend>() {
            @Override
            public void changed(ObservableValue<? extends Friend> observableValue, Friend friend, Friend t1) {
                System.out.println("Selection change from " + friend.getName() + " to " + t1.getName());
            }
        });
    }

    public class FriendCell extends ListCell<Friend> {
        private Label lblName, lblStatus;
        private VBox box;

        public FriendCell() {
            setPrefWidth(100);
            lblName = new Label();
            lblStatus = new Label();
            box = new VBox(lblName, lblStatus);
        }

        @Override
        protected void updateItem(Friend friend, boolean b) {
            super.updateItem(friend, b);

            if (friend == null || b) {
                setGraphic(null);
            } else {
                lblName.setText(friend.getName());
                lblStatus.setText(friend.isStatus()?"Online":"Offline");
                setGraphic(box);
            }
        }
    }
    @FXML
    public void onBtnClicked(MouseEvent e) throws Exception {
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
