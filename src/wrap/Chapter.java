package wrap;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Time;
import java.util.Date;

public class Chapter {
    public SimpleStringProperty id = new SimpleStringProperty();
    public SimpleStringProperty date = new SimpleStringProperty();
    public SimpleStringProperty chapterName = new SimpleStringProperty();
    public SimpleStringProperty duration = new SimpleStringProperty();
    public SimpleStringProperty admin = new SimpleStringProperty();

    public String getId() {
        return id.get();
    }
    public String getDate() {
        return date.get();
    }
    public String getChapterName() {
        return chapterName.get();
    }
    public String getDuration() {
        return duration.get();
    }
    public String getAdmin() {
        return admin.get();
    }
}
