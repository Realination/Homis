package homis.controllers;


import homis.Main;
import homis.app.Constants;
import homis.app.Settings;
import homis.app.Uploader;
import homis.models.AppSettings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Timer;

/**
 * Created by troll173 on 11/25/15.
 */

public class SettingsController {
    @FXML
    Button btnSave,btnBrowseDir;
    @FXML
    CheckBox chckboxautoUpload;
    @FXML
    TextField txtnralcode,txtDir,txtfiles,txtinterval;
    @FXML
    VBox vRestore,vBarBDirs;
    @FXML
    ChoiceBox cbRestoreDirectory;
    @FXML
    AnchorPane settingsRoot;


    Constants constants;
    AppSettings tblsettings = new AppSettings();
    public void initialize() {
        constants = Constants.getInstance();

        if(constants.getValue("s_autoupload").equalsIgnoreCase("1")){
            chckboxautoUpload.setSelected(true);
        }else{
            chckboxautoUpload.setSelected(false);
        }

        txtnralcode.setText(constants.getValue("s_nralcode"));
        txtfiles.setText(constants.getValue("s_archiveCount"));
        txtinterval.setText(constants.getValue("s_backupInterval"));
        txtDir.setText(constants.getValue("s_dumpDir"));


        generateBackupPaths();

//                generateRestore();


    }



    void generateBackupPaths(){
        ObservableList restorePaths = FXCollections.observableArrayList();
        restorePaths.clear();
        vBarBDirs.getChildren().clear();
//        cbRestoreDirectory.getItems().clear();
        String[] backupArr = constants.getValue("s_dumpDir").split(";");
        for (final String dir : backupArr){
            restorePaths.add(dir);
            HBox hbox = new HBox();
            Label lbl = new Label(dir);
            Button btn = new Button("Delete");
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String nString = constants.getValue("s_dumpDir");
                    nString = nString.replaceAll(";"+dir, "");
                    nString = nString.replaceAll(dir, "");
                    constants.addValue("s_dumpDir",nString);
                    System.out.println(constants.getValue("s_dumpDir"));
                    txtDir.setText(nString);
                    generateBackupPaths();
                }
            });
            hbox.getChildren().add(lbl);
            hbox.getChildren().add(btn);
            vBarBDirs.getChildren().add(hbox);
        }

        try {

            cbRestoreDirectory.setItems(restorePaths);
            cbRestoreDirectory.setValue(restorePaths.get(0));
            generateRestore(restorePaths.get(0).toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        cbRestoreDirectory.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String t, String t1) {
//                System.out.println(ov);
//                System.out.println(t);
//                System.out.println(t1);
                generateRestore(t1);
            }
        });
    }





    void generateRestore(final String dir){
        File directory = new File(dir);
        File[] files = directory.listFiles();
        vRestore.getChildren().clear();
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.compare(f1.lastModified(), f2.lastModified());
            }
        });
        Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        System.out.println("restoreDir="+dir);
        for(final File file : files){
            HBox hbox = new HBox();
            Label tlbl = new Label(file.getName());
            Button btn = new Button("Restore");
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

//                    String dbstring = "mysql -u "+ Settings.user+" -p"+Settings.pass+" "+Settings.dbname+" < "+constants.getValue("s_dumpDir")+"/"+file.getName();


                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Restoring Database");
                    alert.setHeaderText("Please wait while we restore your database");
                    alert.setContentText("The program will not respond until the process is complete");

                    alert.showAndWait();



                    String[] executeCmd;
                    executeCmd = new String[]{"/bin/sh", "-c", "mysql -u "+ Settings.user+" -p"+Settings.pass+" "+Settings.dbname+" < "+dir+"/"+file.getName()};
                    Process runtimeProcess = null;
                    try {
                        runtimeProcess = Runtime.getRuntime().exec(executeCmd);
                        int processComplete = runtimeProcess.waitFor();
                        if(processComplete == 0){
                            System.out.println("Succes");
                        } else {
                            System.out.println("Failure");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                    alert2.setTitle("Database Restored!");
                    alert2.setHeaderText(null);
                    alert2.setContentText("Success! we have successfully restored your database!");
                    alert2.showAndWait();
                }
            });
            hbox.getChildren().add(tlbl);
            hbox.getChildren().add(btn);
            vRestore.getChildren().add(hbox);
        }
    }


    @FXML
    void closeSettings(){
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    @FXML
    void saveSettings(){
//        AUTOUPLOAD
        if(chckboxautoUpload.isSelected()){
            ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("value","1"));
            tblsettings.save(nvp,"1");
            constants.addValue("s_autoupload","1");
            Main.timer = new Timer();
            Main.timer.schedule(new Uploader(), 0, 15000);
        }else{
            ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("value","0"));
            tblsettings.save(nvp,"1");
            constants.addValue("s_autoupload","0");
            try {
                Main.timer.cancel();
            }catch (Exception e){

            }

        }

//        NRALCODE
        constants.addValue("s_nralcode",txtnralcode.getText());
        ArrayList<NameValuePair> nvp2 = new ArrayList<NameValuePair>();
        nvp2.add(new BasicNameValuePair("value",txtnralcode.getText()));
        tblsettings.save(nvp2,"2");

        //     ARCHIVE DIR
        constants.addValue("s_dumpDir",txtDir.getText());
        ArrayList<NameValuePair> nvp3 = new ArrayList<NameValuePair>();
        nvp2.add(new BasicNameValuePair("value",txtDir.getText()));
        tblsettings.save(nvp2,"3");

        //     ARCHIVE INTERVAL
        constants.addValue("s_backupInterval",txtinterval.getText());
        ArrayList<NameValuePair> nvp4 = new ArrayList<NameValuePair>();
        nvp2.add(new BasicNameValuePair("value",txtinterval.getText()));
        tblsettings.save(nvp2,"4");

//     ARCHIVE COUNT
        constants.addValue("s_archiveCount",txtfiles.getText());
        ArrayList<NameValuePair> nvp5 = new ArrayList<NameValuePair>();
        nvp2.add(new BasicNameValuePair("value",txtfiles.getText()));
        tblsettings.save(nvp2,"5");

        closeSettings();
    }


    @FXML
    void browseDir() {

        DirectoryChooser directoryChooser = new DirectoryChooser();

        directoryChooser.setTitle("Select Archive Directory");
        //Show open file dialog

        File file = directoryChooser.showDialog(null);

        if (file != null) {

            txtDir.setText(txtDir.getText()+";"+file.getPath());
            constants.addValue("s_dumpDir",txtDir.getText());
            generateBackupPaths();
        }
    }


}
