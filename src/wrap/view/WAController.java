package wrap.view;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import wrap.Chapter;
import wrap.DBController;
import wrap.MainApp;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class WAController {

    private AnchorPane layout;
    private TableView<Chapter> table;
    private TableColumn<Chapter, String> iDColumn;
    private TableColumn<Chapter, String> dateColumn;
    private TableColumn<Chapter, String> chapterNameColumn;
    private TableColumn<Chapter, String> durationColumn;
    private TableColumn<Chapter, String> adminColumn;

    @FXML
    private TextField searchField;
    @FXML
    private TextField refineField;
    @FXML
    private ComboBox<String> comboBox1;
    @FXML
    private ComboBox<String> comboBox2;
    @FXML
    private TabPane leftPane;
    @FXML
    private CheckBox isMeticulously;
    @FXML
    private TabPane rightPane;
    @FXML
    private SplitPane root;

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

        comboBox1.setItems(options);
        comboBox2.setItems(options);
    }

    private Tab getLayoutTab(String s) {
        final Tab tab1 = new Tab();
        tab1.setText(s);
        leftPane.getTabs().add(tab1);
        tab1.setClosable(true);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("layout.fxml"));
        try {
            layout = (AnchorPane) fxmlLoader.load();
            tab1.setContent(layout);
            layout.autosize();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final ContextMenu tabMenu = new ContextMenu();
        MenuItem splitLeft = new MenuItem("Split left");
        MenuItem splitRight = new MenuItem("Split right");
        splitRight.setOnAction(event -> {
                rightPane.getTabs().add(tab1);
                leftPane.getTabs().remove(tab1);
        });

        splitLeft.setOnAction(event -> {
                leftPane.getTabs().add(tab1);
                rightPane.getTabs().remove(tab1);
        });

        layout.setOnContextMenuRequested(event -> {
                tabMenu.show(layout, event.getScreenX(), event.getScreenY());
        });

        tabMenu.getItems().addAll(splitLeft, splitRight);
        tab1.setContextMenu(tabMenu);
        return tab1;
    }

    //new table tab
    private Tab getTableTab(String s) {
        final Tab tab1 = new Tab();
        tab1.setText(s);
        leftPane.getTabs().add(tab1);
        tab1.setClosable(true);
        table = new TableView<>();
        tab1.setContent(table);
        final ContextMenu tabMenu = new ContextMenu();
        MenuItem splitLeft = new MenuItem("Split left");
        MenuItem splitRight = new MenuItem("Split right");
        MenuItem report = new MenuItem("Report");
        MenuItem delete = new MenuItem("Delete selected");
        delete.setOnAction(event -> {
            ObservableList<Chapter> rows = table.getItems();
            for (Chapter row : rows) {
                try {
                    JDBSCon().statement.executeUpdate("DELETE  FROM broadcasting_list WHERE id = '" + row.getId() + "' AND ch_id = (SELECT ch_id FROM chapter_list AS ch_id WHERE chapter_name = '" + row.getChapterName() + "' AND duration = '" + row.getDuration() + "')");
                    JDBSCon().statement.executeUpdate("DELETE  FROM chapter_list WHERE chapter_name = '" + row.getChapterName() + "' AND duration = '" + row.getDuration() + "'");
                    JDBSCon().statement.executeUpdate("DELETE  FROM by_date_list WHERE id = " + row.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        report.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();

            final File file = directoryChooser.showDialog(null);

            Workbook book = new HSSFWorkbook();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY");
            Sheet sheet = book.createSheet("report" + format.format(new Date()));
            ObservableList<Chapter> items = table.getItems();
            sheet.setFitToPage(true);
            for (int i = 0; i < items.size(); i++) {
                Row row = sheet.createRow(i);
                Cell id = row.createCell(0);
                Cell date = row.createCell(1);
                DataFormat DateFormat = book.createDataFormat();
                CellStyle dateStyle = book.createCellStyle();
                dateStyle.setDataFormat(DateFormat.getFormat("dd.mm.yyyy"));
                date.setCellStyle(dateStyle);
                Cell chapter = row.createCell(2);
                Cell duration = row.createCell(3);
                Cell admin = row.createCell(4);
                id.setCellValue(items.get(i).getId());
                date.setCellValue(items.get(i).getDate());
                chapter.setCellValue(items.get(i).getChapterName());
                duration.setCellValue(items.get(i).getDuration());
                admin.setCellValue(items.get(i).getAdmin());
                }
                try {
                book.write(new FileOutputStream(file.getAbsolutePath() + "/report" + format.format(new Date())));
                } catch (IOException e) {
                e.printStackTrace();
                }
        });
        splitRight.setOnAction(event -> {
                rightPane.getTabs().add(tab1);
                leftPane.getTabs().remove(tab1);
        });
        splitLeft.setOnAction(event -> {
                leftPane.getTabs().add(tab1);
                rightPane.getTabs().remove(tab1);
        });
        table.setOnContextMenuRequested(event -> {
                tabMenu.show(table, event.getScreenX(), event.getScreenY());
        });
        tabMenu.getItems().addAll(splitLeft, splitRight, report, delete);
        tab1.setContextMenu(tabMenu);
        return tab1;
    }

    public WAController() {
    }

    @FXML
    private void handleAdd() {
        getLayoutTab("Add new");
    }


    //to database
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
                new PropertyValueFactory<>("id")
        );
        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("date")
        );
        chapterNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("chapterName")
        );
        durationColumn.setCellValueFactory(
                new PropertyValueFactory<>("duration")
        );
        adminColumn.setCellValueFactory(
                new PropertyValueFactory<>("admin")
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

    private String toRefine() {
        if (refineField.getText().isEmpty()) {
            return "";
        }
        else return "AND " + comboBox2.getValue() + " LIKE '" + handleIsMeticulously()
                + refineField.getText() + handleIsMeticulously() + "'";
    }

    private String toSearch() {
        if(searchField.getText().isEmpty()) {
            return "";
        }
        else return " AND " + comboBox1.getValue() +
        " LIKE '" + handleIsMeticulously() + searchField.getText() + handleIsMeticulously()
                + "'";
    }

    @FXML
    private void handleSearchBy() throws SQLException {
        //SQL SELECT
        String searchQuery = "SELECT by_date_list.id, by_date_list.brcst_date, chapter_list.chapter_name, chapter_list.duration," +
                " by_date_list.admin FROM  broadcasting_list, by_date_list, chapter_list WHERE by_date_list.id = broadcasting_list.id AND chapter_list.ch_id = broadcasting_list.ch_id" +
               toSearch() + toRefine();
        getTableTab("review");
        ResultSet rS;
        rS = JDBSCon().statement.executeQuery(searchQuery);
        fillTable(rS);
    }




    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
