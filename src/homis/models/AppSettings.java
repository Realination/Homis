package homis.models;

import homis.app.Model;

/**
 * Created by troll173 on 11/30/15.
 */
public class AppSettings extends Model {
    private static String tblName = "appSettings";
    private static String primaryKey = "id";


    @Override
    public String tblName() {
        return tblName;
    }

    @Override
    public String primaryKey() {
        return primaryKey;
    }
}
