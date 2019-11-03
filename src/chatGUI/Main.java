package chatGUI;

import chat.Chat;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public Chat User;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/Login.fxml"));
        primaryStage.setTitle("Chat App");
        primaryStage.setScene(new Scene(root, 800, 400));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Stopping...");
        Chat.getInstance().keepGo = false;
    }
}
