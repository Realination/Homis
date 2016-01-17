package homis.app;

import homis.DisplayDatabase;
import homis.models.Linkup;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by troll173 on 12/10/15.
 */
public class Uploader extends TimerTask {
    Linkup tbllinkup = new Linkup();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    HttpRequest httpr = new HttpRequest();
    Constants constants = Constants.getInstance();

    @Override
    public void run() {
        try {
            updateEdit();
            checkUpload();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Check");
    }

    private void checkUpload() throws SQLException {
        ResultSet result = DisplayDatabase.listToBeUploaded();


        while (result.next()) {
            System.out.println("add--"+result.getString("reckid"));
            ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
            ResultSetMetaData rsmd = result.getMetaData();
            int columnCount = rsmd.getColumnCount();

// The column count starts from 1
            for (int f = 1; f < columnCount + 1; f++) {
                String fldname = rsmd.getColumnName(f);
                if(!fldname.equalsIgnoreCase("localid")) {
                    nvp.add(new BasicNameValuePair(fldname, result.getString(fldname)));
                }
            }

            String nralcode = constants.getValue("s_nralcode");
            String response = httpr.ServicePost("dataReceiver.php?nralcode="+nralcode, nvp);

            if (response.contains("<br />") || response.equalsIgnoreCase("error")) {
                System.out.println(response);
                break;
            } else {
                System.out.println(response);

                DisplayDatabase.updateStatus(result.getString("reckid"), "uploaded");


            }
        }
    }


    public void updateEdit() throws SQLException {
        ResultSet edited = DisplayDatabase.getEdited();

        while (edited.next()) {

            DisplayDatabase.updateStatus(edited.getString("reckid"), "updated");

            System.out.println("update--"+edited.getString("reckid"));

        }
    }
}
