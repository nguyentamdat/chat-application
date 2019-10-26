package ChatGUI;

import Chat.Chat;
import Chat.Friend;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerChatInterface implements Initializable {
    Chat user = Chat.getInstance();
    ObservableList<Friend> listFriend;
    @FXML
    Button btnAdd;
    @FXML
    Button btnRefresh;
    @FXML
    ListView<Friend> listViewFriend;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listViewFriend.setCellFactory(lv -> new FriendCell());
        listFriend = FXCollections.observableArrayList(user.getListFriend());
        listViewFriend.setItems(listFriend);
        listViewFriend.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Friend>() {
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
            System.out.println(listFriend);
            listFriend = FXCollections.observableArrayList(user.getListFriend());
        } catch (Exception er) {
            er.printStackTrace();
        }
    }

    public class FriendCell extends ListCell<Friend> {
        private Label lblName, lblStatus;
        private VBox box;
        private Circle state = new Circle(2.0f,Color.RED);

        public FriendCell() {
            setPrefWidth(100);
            lblName = new Label();
            lblStatus = new Label();
            box = new VBox(lblName, state);
        }

        @Override
        protected void updateItem(Friend friend, boolean b) {
            super.updateItem(friend, b);

            if (friend == null || b) {
                setGraphic(null);
            } else {
                lblName.setText(friend.getName());
                state.setFill(friend.isStatus() ? Color.GREEN : Color.RED);
                setGraphic(box);
            }
        }
    }
}
