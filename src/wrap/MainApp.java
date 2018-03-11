package wrap;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import wrap.view.LoginController;
import wrap.view.WAController;

public class MainApp extends Application {

    private Stage primaryStage;
    private AnchorPane login;
    private SplitPane workingPane;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Odmen");
        showLogin();
    }


    public void showWA() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("view/WorkingArea.fxml"));
            workingPane = (SplitPane) fxmlLoader.load();
            Scene scene2 = new Scene(workingPane);
            primaryStage.setScene(scene2);
            WAController waController = fxmlLoader.getController();
            waController.setMainApp(this);
            primaryStage.setMaximized(true);
            primaryStage.setResizable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLogin() {
        try {
            FXMLLoader loginLoader = new FXMLLoader();
            loginLoader.setLocation(MainApp.class.getResource("view/Login.fxml"));
            login = (AnchorPane) loginLoader.load();
            Scene scene1 = new Scene(login);
            primaryStage.setScene(scene1);
            LoginController controller = loginLoader.getController();
            controller.setMainApp(this);
            primaryStage.show();
            primaryStage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}