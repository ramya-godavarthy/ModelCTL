package modelCTL_java.Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modelCTL_java.View.ViewGUI;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(ViewGUI.class.getResource("view.fxml"));

        Scene scene = new Scene(root,600,400);

        // set title for the stage
        primaryStage.setTitle("CTL Model Checker");
        primaryStage.setScene(scene);
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
