package wrap.view;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import wrap.DBController;
import wrap.MainApp;
import java.io.*;
import java.sql.SQLException;
import java.util.Arrays;

public class LoginController {

    @FXML
    private Pane pane;
    @FXML
    private TextField login;
    @FXML
    private TextField password;
    @FXML
    private Label connect;
    @FXML
    private ComboBox<String> dbUrl;
    @FXML
    private MenuButton settings;
    @FXML
    private MenuItem item1;
    @FXML
    private MenuItem item2;
    @FXML
    private MenuItem item3;

    public LoginController() {
    }

    private MainApp mainApp;

    private void options() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader( "journals.txt"));
            ObservableList<String> options =
                    FXCollections.observableArrayList(Arrays.asList(reader.readLine().split(" ")));
            dbUrl.setItems(options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        options();
        dbUrl.setPromptText("jdbc:mysql://");
        pane.setOnKeyPressed(new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent keyEvent) {
               if(keyEvent.getCode() == KeyCode.ENTER) {
                   String log, pass, url;
                   log = login.getText();
                   pass = password.getText();
                   url = "jdbc:mysql://" + dbUrl.getValue();
                   DBController dbController = new DBController(log, pass, url);
                   try {
                       dbController.connection();
                   } catch (SQLException e) {
                       initialize();
                   }
                   if (dbController.isConnected == false) connect.setText("overfill the fields");
                   else {
                       note(dbUrl.getValue());
                       connect.setText("You are connected");
                       mainApp.showWA();
                       WAController waController = new WAController();
                   }
               }
           }
        });
    }

    public void note(String option) {
        try {
            FileWriter writer = new FileWriter("journals.txt", true);
            writer.write(option + " ");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
