package ChatGUI;

import Chat.Chat;
import Chat.Friend;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerChatInterface implements Initializable {
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
}
