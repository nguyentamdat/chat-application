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
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerChatInterface implements Initializable {
    @FXML
    JFXButton btnFile;
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

    private Chat user = Chat.getInstance();
    private ObservableList<Friend> listFriend;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listViewFriend.setCellFactory(lv -> new FriendCell());
        listFriend = FXCollections.observableList(user.getListFriend());
        listViewFriend.setItems(listFriend);
        inbox.setItems(user.getListMsg());
        inbox.setCellFactory(lv -> new InboxCell());
        lblName.setFont(Font.font("Roboto Black", FontWeight.BOLD, 20));
        lblName.setBackground(new Background(new BackgroundFill(
                Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        lblName.setEffect(dropShadow);
        lblName.setTextFill(Color.rgb(255, 0, 0, 0.5));
        lblName.setAlignment(Pos.CENTER);
        listViewFriend.setBackground(new Background(new BackgroundFill(
                Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        listViewFriend.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Friend>() {
            @Override
            public void changed(ObservableValue<? extends Friend> observableValue, Friend friend, Friend t1) {
                if (friend == t1) return;
                if (t1 != null) {
                    String name = t1.getName();
                    System.out.println("Selection change to " + name);
                    lblName.setText(name);
                    if (t1.isStatus()) {
                        System.out.println("Starting chatting");
                        if (!user.chatWith(name)) {
                            inputChat.setText("User offline!");
                            inputChat.setDisable(true);
                        } else {
                            inputChat.setText("");
                            inputChat.setDisable(false);
                        }
                    } else {
                        inputChat.setText("User offline!");
                        inputChat.setDisable(true);
                    }
                }
            }
        });
    }

    public void setLblName(String name) {
        lblName.setText(name);
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
    public void onBtnFileClicked(MouseEvent e) {
        Window stage = ((Node) e.getTarget()).getScene().getWindow();
        FileChooser fil_chooser = new FileChooser();
        File file = fil_chooser.showOpenDialog(stage);
        String filepath = file.getAbsolutePath();
        user.sendFile(filepath);
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

    public class FriendCell extends ListCell<Friend> {
        private Label lblName;
        private Circle state = new Circle(2.0f, Color.GRAY);
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
                lblName.setFont(Font.font("Roboto", 14));
                lblName.setTextFill(Color.WHITE);
                state.setFill(friend.isStatus() ? Color.GREEN : Color.RED);
                box.setBackground(new Background(new BackgroundFill(
                        Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
                setGraphic(box);
            }
        }
    }

    public class InboxCell extends ListCell<Message> {
        private Label lblMsg, lblUser;
        private HBox box;

        public InboxCell() {
            setPrefHeight(20);
            lblMsg = new Label();
            lblUser = new Label();
        }

        @Override
        protected void updateItem(Message msg, boolean b) {
            super.updateItem(msg, b);
            inbox.setBackground(new Background(new BackgroundFill(
                    Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
            if (msg == null || b) {
                setGraphic(null);
            } else {
                lblUser.setText(msg.getFrom());
                lblUser.setFont(Font.font("Roboto", 14));
                lblMsg.setText(msg.getType().equalsIgnoreCase("file") ? msg.getMessage().split(":")[1] : msg.getMessage());
                lblMsg.setFont(Font.font("Roboto", 14));
                if (msg.getFrom().equalsIgnoreCase(user.getUsername())) {
                    box = new HBox(lblMsg);
                    box.setAlignment(Pos.CENTER_RIGHT);
                    box.setBackground(new Background(new BackgroundFill(
                            Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
                } else {
                    box = new HBox(lblMsg);
                    box.setBackground(new Background(new BackgroundFill(
                            Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
                }
                setGraphic(box);
            }
        }
    }
}
