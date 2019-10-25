package ChatGUI;

import java.net.URL;
import java.io.IOException;
import java.io.File;


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

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;

public class ControllerLogin implements Initializable{
    @FXML
    Button btnLogin;
    public AnchorPane mainSelect;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    @FXML
    public void onbtnLoginClick(MouseEvent e) throws Exception {
        Button button = (Button)e.getSource();
        System.out.println(button.getId());

        System.out.println("out");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChatInterface.fxml"));
            Parent root = fxmlLoader.load();
            mainSelect.getChildren().setAll(root);
            mainSelect.setTopAnchor(root, 0.0);
            mainSelect.setBottomAnchor(root, 0.0);
            mainSelect.setLeftAnchor(root, 0.0);
            mainSelect.setRightAnchor(root, 0.0);
            ControllerChatInterface controller = fxmlLoader.getController();
        } catch (Exception er) {
            er.printStackTrace();
        }

    }
}


