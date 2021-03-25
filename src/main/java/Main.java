package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws RuntimeException, IOException {
        StageInfo.pStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("resources/Calendar.fxml"));

        Scene scene = new Scene(root, 1000, StageInfo.height);
        StageInfo.pStage.setTitle("Calendar");
        StageInfo.pStage.setScene(scene);
        StageInfo.pStage.show();
    }

    public static void main(String[] args) throws RuntimeException {
        try{
            new BackgroundTasks();
            launch(args);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}

class StageInfo {
    protected static Stage pStage;
    protected static int height = 650;

    static void modifyStageHeight(int heightChange) {
        StageInfo.height += heightChange;
        StageInfo.pStage.setHeight(StageInfo.height);
    }

    static void resetStageHeight() {
        StageInfo.height = 650;
        StageInfo.pStage.setHeight(StageInfo.height);
    }
}

