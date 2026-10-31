package com.enigma;

import com.enigma.controller.EnigmaController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/com/enigma/fxml/main-view.fxml")
        );

        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);

        EnigmaController controller = fxmlLoader.getController();
        controller.AttachKeyboardInput(scene, primaryStage);

        primaryStage.setTitle("Enigma Machine I");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(850);
        primaryStage.setMinHeight(650);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
