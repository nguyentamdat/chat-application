package ChatGUI;

import Chat.Chat;
import Chat.Friend;
import Chat.Message;
import com.jfoenix.controls.JFXButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerChatInterface implements Initializable {
    private Chat user = Chat.getInstance();
    private ObservableList<Friend> listFriend;
    private ObservableList<Message> inboxList;
    @FXML
    JFXButton btnAdd;
    @FXML
    JFXButton btnRefresh;
    @FXML
    ListView<Friend> listViewFriend;
    @FXML
    Label lblName;
    @FXML
    TextField inputChat;
    @FXML
    ListView<Message> inbox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listViewFriend.setCellFactory(lv -> new FriendCell());
        listFriend = FXCollections.observableList(user.getListFriend());
        listViewFriend.setItems(listFriend);
        inbox.setCellFactory(lv -> new InboxCell());
        listViewFriend.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Friend>() {
            @Override
            public void changed(ObservableValue<? extends Friend> observableValue, Friend friend, Friend t1) {
                String name = t1.getName();
                System.out.println("Selection change to " + name);
                lblName.setText(name);
                if (!user.chatWith(name)) {
                    inputChat.setText("User offline!");
                    inputChat.setDisable(true);
                }
                else {
                    inputChat.setText("");
                    inputChat.setDisable(false);
                    inboxList = FXCollections.observableList(user.getInbox());
                }
            }
        });
    }

    @FXML
    public void onClickedSend(MouseEvent event) {
        try {
            String msg = inputChat.getText();
            if (!"".equalsIgnoreCase(msg)) {
                user.sendMsg(msg);
                inputChat.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onKeyEnter(KeyEvent event) {
        try {
            if (event.getCode() == KeyCode.ENTER) {
                onClickedSend(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            listFriend.clear();
            listFriend.addAll(user.getListFriend());
        } catch (Exception er) {
            er.printStackTrace();
        }
    }

    public class FriendCell extends ListCell<Friend> {
        private Label lblName;
        private Circle state = new Circle(2.0f,Color.GRAY);
        private HBox box;

        public FriendCell() {
            setPrefWidth(100);
            lblName = new Label();
            box = new HBox(lblName, state);
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
    public class InboxCell extends ListCell<Message> {
        private Label lblMsg, lblUser;
        private VBox box;

        public InboxCell() {
            setPrefHeight(20);
            lblMsg = new Label();
            lblUser = new Label();
            box = new VBox(lblUser, lblMsg);
        }

        @Override
        protected void updateItem(Message msg, boolean b) {
            super.updateItem(msg, b);

            if (msg == null || b) {
                setGraphic(null);
            } else {
                lblUser.setText(msg.getFrom());
                lblMsg.setText(msg.getMessage());
                setGraphic(box);
            }
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
        Glow glowfx = new Glow();
        glowfx.setLevel(0);
        button.setEffect(null);
    }
}
