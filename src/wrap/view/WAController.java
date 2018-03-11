package wrap.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import wrap.Chapter;
import wrap.DBController;
import wrap.MainApp;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WAController {

    private Tab tab1;
    private TableView<Chapter> table;
    private TableColumn<Chapter, String> iDColumn;
    private TableColumn<Chapter, String> dateColumn;
    private TableColumn<Chapter, String> chapterNameColumn;
    private TableColumn<Chapter, String> durationColumn;
    private TableColumn<Chapter, String> adminColumn;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private TabPane tabPane;
    @FXML
    private CheckBox isMeticulously;

    //new tab
    private Tab getTab(String s) {
        tab1 = new Tab();
        tab1.setText(s);
        tabPane.getTabs().add(tab1);
        tab1.setClosable(true);
        table = new TableView<>();
        tab1.setContent(table);
        return tab1;
    }

    public WAController() {
    }



    @FXML
    private void handleRemove() {
        String s = "LIKE";
        if(isMeticulously.isSelected()) {
            s = "=";
        }
        System.out.println(s);
        String searchQuery = "SELECT by_date_list.id, by_date_list.brcst_date, chapter_list.chapter_name, chapter_list.duration, by_date_list.admin FROM  broadcasting_list, by_date_list, chapter_list WHERE by_date_list.id = broadcasting_list.id AND chapter_list.ch_id = broadcasting_list.ch_id AND "+ comboBox.getValue() + "LIKE '" + handleIsMeticulously() + searchField.getText() + handleIsMeticulously() + "'";
        getTab("removed");
        ResultSet rS;
        try {
        rS = JDBSCon().statement.executeQuery("DELETE FROM broadcasting_list, by_date_list, chapter_list WHERE (" + searchQuery + ")");
            fillTable(rS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //to daba
    private DBController JDBSCon() {
        DBController controller = new DBController();
        try {
            controller.connection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return controller;
    }

    private void fillTable(ResultSet rS) throws SQLException {
        ObservableList<Chapter> chapters = FXCollections.observableArrayList();
        //filling table with SQL content
        iDColumn = new TableColumn<>("ID");
        dateColumn = new TableColumn<>("Date");
        chapterNameColumn = new TableColumn<>("Chapter");
        durationColumn = new TableColumn<>("Duration");
        adminColumn = new TableColumn<>("Admin");
        iDColumn.setCellValueFactory(
                new PropertyValueFactory<Chapter, String>("id")
        );
        dateColumn.setCellValueFactory(
                new PropertyValueFactory<Chapter, String>("date")
        );
        chapterNameColumn.setCellValueFactory(
                new PropertyValueFactory<Chapter, String>("chapterName")
        );
        durationColumn.setCellValueFactory(
                new PropertyValueFactory<Chapter, String>("duration")
        );
        adminColumn.setCellValueFactory(
                new PropertyValueFactory<Chapter, String>("admin")
        );
        table.getColumns().add(iDColumn);
        table.getColumns().add(dateColumn);
        table.getColumns().add(chapterNameColumn);
        table.getColumns().add(durationColumn);
        table.getColumns().add(adminColumn);
        while (rS.next()) {
            Chapter ch = new Chapter();
            ch.id.set(String.valueOf(rS.getInt("by_date_list.id")));
            ch.date.set(rS.getDate("by_date_list.brcst_date").toString());
            ch.chapterName.set(rS.getString("chapter_list.chapter_name"));
            ch.duration.set(rS.getTime("chapter_list.duration").toString());
            ch.admin.set(rS.getString("by_date_list.admin"));
            chapters.add(ch);
        }
        table.setItems(chapters);
    }

    @FXML
    private String handleIsMeticulously() {
        if (isMeticulously.isSelected()) {
            return "";
        }
        else return "%";
    }

    @FXML
    private void handleSearchBy() throws SQLException {
        //SQL SELECT
        String searchQuery = "SELECT by_date_list.id, by_date_list.brcst_date, chapter_list.chapter_name, chapter_list.duration, by_date_list.admin FROM  broadcasting_list, by_date_list, chapter_list WHERE by_date_list.id = broadcasting_list.id AND chapter_list.ch_id = broadcasting_list.ch_id AND "+ comboBox.getValue() +
                " LIKE '" + handleIsMeticulously() + searchField.getText() + handleIsMeticulously() + "'";
        getTab("review");
        ResultSet rS;
        rS = JDBSCon().statement.executeQuery(searchQuery);
        fillTable(rS);
    }

    @FXML
    private void initialize() {
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "by_date_list.id",
                        "by_date_list.brcst_date",
                        "chapter_list.chapter_name",
                        "chapter_list.duration",
                        "by_date_list.admin"
                );
        comboBox.setItems(options);
    }

    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
