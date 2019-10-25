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

import javafx.scene.control.TextField;

public class ControllerLogin implements Initializable{
    @FXML
    Button btnLogin;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            btnLogin.setOnMouseClicked(actionEvent -> {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BookDetails.fxml"));
                Parent root =  fxmlLoader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root, 600, 400));
                stage.show();

                ControllerChatInterface controller = fxmlLoader.getController();


            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


