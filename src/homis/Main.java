package homis;

import homis.app.Constants;
import homis.app.Settings;
import homis.app.Uploader;
import homis.controllers.MainController;
import homis.models.AppSettings;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main extends Application {
    public static Stage stage;
    Constants constants;
    AppSettings tblsettings;
    public static Timer timer;
    public static Timer archiveTimer;
    @Override
    public void start(Stage stage) throws Exception{
        constants = Constants.getInstance();
        tblsettings = new AppSettings();
        ResultSet settings;
        settings = tblsettings.get("1");
        settings.first();
        constants.addValue("s_autoupload", settings.getString("value"));
        settings = tblsettings.get("2");
        settings.first();
        constants.addValue("s_nralcode", settings.getString("value"));
        settings = tblsettings.get("3");
        settings.first();
        constants.addValue("s_dumpDir", settings.getString("value"));
        settings = tblsettings.get("4");
        settings.first();
        constants.addValue("s_backupInterval", settings.getString("value"));
        settings = tblsettings.get("5");
        settings.first();
        constants.addValue("s_archiveCount", settings.getString("value"));

        if(constants.getValue("s_autoupload").equalsIgnoreCase("1")){
            try {
                timer  = new Timer();
                timer.schedule(new Uploader(), 0, 3600000);
                System.out.println("starting autoupload");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

       Long interval = Long.parseLong(constants.getValue("s_backupInterval"))*3600000;
       CreateArchives(interval);



         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/homis/views/Main.fxml"));
        Parent root = fxmlLoader.load();
        MainController controller = fxmlLoader.<MainController>getController();
        stage.setScene(new Scene(root));
        controller.initialize(stage);
        stage.show();

    }

    private void CreateArchives(Long interval) {
        archiveTimer = new Timer();
        archiveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm");
                Date date = new Date();



                String[] backupArr = constants.getValue("s_dumpDir").split(";");
                for (String dir : backupArr) {


                    try {
                        String executeCmd = "mysqldump -u " + Settings.user + " -p" + Settings.pass + " " + Settings.dbname + " -r " + dir + "/FBU" + dateFormat.format(date) + ".sql";
                        Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
                        int processComplete = runtimeProcess.waitFor();
                        if (processComplete == 0) {
                            System.out.println("Backup taken successfully @ " + dir + "/FBU" + dateFormat.format(date) + ".sql");
                        } else {
                            System.out.println("Could not take mysql backup @ " + dir + "/FBU" + dateFormat.format(date) + ".sql");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    int archiveCount = new File(dir).list().length;
                    while (archiveCount > Integer.parseInt(constants.getValue("s_archiveCount"))) {

                        File directory = new File(dir);
                        File[] files = directory.listFiles();

                        Arrays.sort(files, new Comparator<File>() {
                            public int compare(File f1, File f2) {
                                return Long.compare(f1.lastModified(), f2.lastModified());
                            }
                        });

                        System.out.println(files[0].getName());
                        files[0].delete();
                        archiveCount = new File(dir).list().length;
                    }


                }







            }
        },0,interval);
//        },0,15000);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
