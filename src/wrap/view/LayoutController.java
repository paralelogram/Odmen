package wrap.view;


import javafx.fxml.FXML;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import wrap.DBController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class LayoutController {
    @FXML
    private GridPane layoutGrid;
    @FXML
    private TextArea output;

    private int rowCount;
    ArrayList<TextField> texts = new ArrayList<>();

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

    @FXML
    private void handleAddRow() {
        rowCount++;
        int col;
        int id = 0;

        try {
            ResultSet result = JDBSCon().statement.executeQuery("SELECT MAX(id) AS id FROM by_date_list");
            while (result.next()) {
                id = result.getInt("id");
            }
            id++;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for ( col = 0; col <= 4; col++) {
            TextField textField = new TextField();
            //id
            if(col == 0) {
                textField.setText(String.valueOf(id));
                textField.setEditable(false);
            }
            textField.autosize();
            texts.add(textField);
            layoutGrid.add(textField, col+1, rowCount);
        }
        layoutGrid.setMaxWidth(Region.USE_COMPUTED_SIZE);
    }


    @FXML
    private void handleCoincidence() {
        ArrayList<String> columns = new ArrayList<>();
        columns.add("by_date_list.id");
        columns.add("by_date_list.brcst_date");
        columns.add("chapter_list.chapter_name");
        columns.add("chapter_list.duration");
        columns.add("by_date_list.admin");
        for (String s : columns) {
            for (TextField t : texts) {
                if (!t.getText().isEmpty()) {
                    String query = "SELECT by_date_list.id, by_date_list.brcst_date, chapter_list.chapter_name, chapter_list.duration," +
                            " by_date_list.admin FROM  broadcasting_list, by_date_list, chapter_list WHERE by_date_list.id = broadcasting_list.id AND chapter_list.ch_id = broadcasting_list.ch_id AND " +
                            s + " LIKE '" + t.getText() + "'";
                    ResultSet rs;
                    try {
                        rs = JDBSCon().statement.executeQuery(query);
                        while (rs.next()) {
                            output.appendText(rs.getInt(1) + " | " + rs.getDate(2) + " | " + rs.getString(3) + " | " + rs.getTime(4) + " | " + rs.getString(5) + " |\n");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private int replace(String temp) throws SQLException {
        String query = "SELECT by_date_list.id, chapter_list.ch_id, by_date_list.brcst_date, chapter_list.chapter_name, chapter_list.duration," +
                " by_date_list.admin FROM  broadcasting_list, by_date_list, chapter_list WHERE by_date_list.id = broadcasting_list.id AND chapter_list.ch_id = broadcasting_list.ch_id";
        ResultSet rs = JDBSCon().statement.executeQuery(query);
        int result = 0;
        while ( rs.next()) {
            String string = (rs.getString("chapter_list.chapter_name") + " " + rs.getTime("chapter_list.duration"));
            if ( string.equals(temp)) {
                result = rs.getInt("chapter_list.ch_id");
            }
        }
        return result;
    }



    @FXML
    public void handleCommit() {
        output.appendText("думу думаю\n");
        //increments
        int i = 1;
        int id = 0;
        int ch_id = 0;

        //dataHolders
        StringBuilder queryBroadcasting_list = new StringBuilder();
        StringBuilder queryChapter_list= new StringBuilder();
        StringBuilder temp = new StringBuilder();
        StringBuilder queryBy_date_list = new StringBuilder();

        for(TextField s : texts) {

            //date
            if (i == 2 & !s.getText().isEmpty() & !queryBy_date_list.toString().contains(s.getText())) {
                queryBy_date_list.append("'" + s.getText() + "', ");
            }

            //admin
            if (i == 5 & !s.getText().isEmpty() & !queryBy_date_list.toString().contains(s.getText())) {
                queryBy_date_list.append("'" + s.getText() + "')");
            }

            //chapter name
            if (i == 3 & !s.getText().isEmpty()) {
                queryChapter_list.append("'" + s.getText() + "', ");
                temp.append(s.getText());
            }

            //duration
            if (i == 4 & !s.getText().isEmpty()) {
                queryChapter_list.append("'" + s.getText() + "')");
                temp.append(" " + s.getText());
            }

            if (i == 5) {
                //insert into by_date_list
                try {
                    JDBSCon().statement.executeUpdate("INSERT INTO by_date_list (brcst_date, admin) values (" + queryBy_date_list.toString());
                    output.appendText("внесено -> " + queryBy_date_list.toString() + "\n");
                    queryBy_date_list.delete(0, Integer.MAX_VALUE);
                } catch (SQLException e) {
                    output.appendText(e.getSQLState() + "\n");
                }
                boolean isInBD = false;

                //insert into chapter_list
                try {
                ResultSet rsId = JDBSCon().statement.executeQuery("SELECT MAX(id) AS id FROM  by_date_list");
                while (rsId.next()) {
                    id = rsId.getInt("id");
                }
                    output.appendText("внесення в таблицю записів -> " + queryChapter_list.toString() + "\n");
                    JDBSCon().statement.executeUpdate("INSERT INTO chapter_list (chapter_name, duration) values (" + queryChapter_list.toString() + "\n");
                } catch (SQLException e) {
                    output.appendText("Запис з даними " + queryChapter_list.toString() + " вже присутній в базі даних \n замінюю... \n");
                    try {
                        int t = replace(temp.toString());
                        if (t != 0) {
                            queryBroadcasting_list.append("(" + id + ", " + t + ")");
                            output.appendText("новий сh_id = " + t + " \n");
                            isInBD = true;
                        }
                        JDBSCon().statement.executeUpdate("INSERT INTO broadcasting_list (id, ch_id) values " + queryBroadcasting_list.toString());
                        output.appendText("внесення в проміжну таблицю запис " + id + "-" + ch_id + "\n");
                    } catch (SQLException e1) {
                    }
                    queryBroadcasting_list.delete(0 , Integer.MAX_VALUE);
                }
                temp.delete(0, Integer.MAX_VALUE);
                queryChapter_list.delete(0, Integer.MAX_VALUE);
                try {
                    ResultSet rsChId = JDBSCon().statement.executeQuery("SELECT MAX(ch_id) AS ch_id FROM  chapter_list ");
                    while (rsChId.next()) {
                        ch_id = rsChId.getInt("ch_id");
                    }
                } catch (SQLException e) {
                }
                if ( isInBD == false) {
                    queryBroadcasting_list.append("(" + id + ", " + ch_id + ")");
                    try {
                        JDBSCon().statement.executeUpdate("INSERT INTO broadcasting_list (id, ch_id) values " + queryBroadcasting_list.toString());
                        output.appendText("внесення в проміжну таблицю запис " + id + "-" + ch_id + "\n");
                    } catch (SQLException e) {
                    }
                    queryBroadcasting_list.delete(0 , Integer.MAX_VALUE);
                }
                i = 0;
            }
            i++;
        }
    }


}
