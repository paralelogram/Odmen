package wrap.view;
import javafx.animation.Animation;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.ArrayList;

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
    @FXML
    private Slider theme;
    @FXML
    private Label press;

    public LoginController() {
    }

    private MainApp mainApp;

    private void options() {
        try {
            String line;
            ObservableList<String> options =
                    FXCollections.observableArrayList();
            BufferedReader reader = new BufferedReader(new FileReader( "journals.txt"));
            while ((line = reader.readLine()) != null) {
                options.add(line);
            }
            dbUrl.setItems(options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static int status;
    @FXML
    private void initialize() {
        press.opacityProperty();
        theme.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() == 1) {
                    status = 1;
                    mainApp.scene1.getStylesheets().add(LoginController.class.getResource("darculalike.css").toExternalForm());

                }
                if (newValue.intValue() == 0) {
                    status = 0;
                    mainApp.scene1.getStylesheets().clear();
                }
            }
        });
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
                       if ( status == 1) {
                           mainApp.scene2.getStylesheets().add(LoginController.class.getResource("darculalike.css").toExternalForm());
                       }
                   }
               }
           }
        });
    }

    public void note(String option) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("journals.txt"));
            FileWriter writer = new FileWriter("journals.txt", true);
            ArrayList<String> lines = new ArrayList<>();
            while (reader.readLine() != null) {
                lines.add(reader.readLine());
            }
            for (String s: lines) {
                if (s == option) {
                    writer.write(option + "\n");
                    writer.close();
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
