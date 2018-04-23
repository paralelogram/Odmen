package wrap;

import javafx.beans.property.SimpleStringProperty;

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
