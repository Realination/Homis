package homis.app;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by troll173 on 12/14/15.
 */
public class DataObject {

    private final SimpleStringProperty str1;
    private final SimpleStringProperty str2;

    public DataObject(String str1, String str2){
        this.str1 = new SimpleStringProperty(str1);
        this.str2 = new SimpleStringProperty(str2);
    }


}
