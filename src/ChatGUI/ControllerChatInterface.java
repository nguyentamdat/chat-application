package ChatGUI;

import Chat.Chat;
import Chat.Friend;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerChatInterface implements Initializable {
    Chat user = Chat.getInstance();
    @FXML
    Button btnAdd;
    @FXML
    Button btnRefresh;
    @FXML
    ListView<Friend> listFriend;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listFriend.setCellFactory(lv -> new FriendCell());
        listFriend.setItems(user.getListFriend());
        listFriend.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Friend>() {
            @Override
            public void changed(ObservableValue<? extends Friend> observableValue, Friend friend, Friend t1) {
                System.out.println("Selection change to " + t1.getName());
            }
        });
    }

    @FXML
    public void onBtnClicked(MouseEvent e) {
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

    @FXML
    public void onBtnRefreshClicked(MouseEvent e) {
        try {
            listFriend.setItems(user.getListFriend());
            listFriend.refresh();
        } catch (Exception er) {
            er.printStackTrace();
        }
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
                lblStatus.setText(friend.isStatus() ? "Online" : "Offline");
                setGraphic(box);
            }
        }
    }
}
