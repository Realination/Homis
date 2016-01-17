package homis.controllers;

import com.sun.glass.ui.Screen;
import homis.DisplayDatabase;
import homis.app.Constants;
import homis.app.HttpRequest;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController {

    @FXML
    public Stage stage;
    @FXML
    public TextField txtMsg;
    @FXML
    public Label lblPending;
    @FXML
    public Label lblUpload;
    @FXML
    public Label uploadlbl;
    @FXML
    public Label uploadlbl1;
    @FXML
    public TextField txtuploaded;
    @FXML
    public Label uploadlbl2;
    @FXML
    public GridPane gridBtn;
    @FXML
    Button btnSettings;

    HttpRequest httpr = new HttpRequest();
    Constants constants = Constants.getInstance();
    public void initialize(Stage primaryStage) {
        // TODO




        constants.addValue("prgcode", "IPHP2");
        constants.addValue("nralcode", "RA6-ILO-M030-000");
        System.out.println("aaa");
        try {
            lblPending.setText(DisplayDatabase.countPending(constants.getValue("prgcode"),constants.getValue("nralcode"),1));
            lblUpload.setText(DisplayDatabase.countPending(constants.getValue("prgcode"),constants.getValue("nralcode"),2));
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if(constants.getValue("s_autoupload").equalsIgnoreCase("0")){
                generateButtons();
            }

        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
                System.out.println("asasa");
            }
        });

        Scene scene = btnSettings.getScene();

        final BooleanProperty sPressed = new SimpleBooleanProperty(false);
        final BooleanProperty ePressed = new SimpleBooleanProperty(false);
        final BooleanProperty tPressed = new SimpleBooleanProperty(false);
        final BooleanProperty iPressed = new SimpleBooleanProperty(false);
        final BooleanProperty nPressed = new SimpleBooleanProperty(false);
        final BooleanProperty gPressed = new SimpleBooleanProperty(false);
        final BooleanBinding setPressed = sPressed.and(ePressed.and(tPressed.and(iPressed.and(nPressed.and(gPressed)))));

// How to respond to both keys pressed together:
        setPressed.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                btnSettings.setVisible(newValue);
            }
        });

// Wire up properties to key events:
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.S) {
                    sPressed.set(true);
                } else if (ke.getCode() == KeyCode.E) {
                    ePressed.set(true);
                }else if(ke.getCode() == KeyCode.T) {
                    tPressed.set(true);
                }else if(ke.getCode() == KeyCode.I) {
                    iPressed.set(true);
                }else if(ke.getCode() == KeyCode.N) {
                    nPressed.set(true);
                }else if(ke.getCode() == KeyCode.G) {
                    gPressed.set(true);
                }



            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.S) {
                    sPressed.set(false);
                } else if (ke.getCode() == KeyCode.E) {
                    ePressed.set(false);
                }else if(ke.getCode() == KeyCode.T) {
                    tPressed.set(false);
                }else if(ke.getCode() == KeyCode.I) {
                    iPressed.set(false);
                }else if(ke.getCode() == KeyCode.N) {
                    nPressed.set(false);
                }else if(ke.getCode() == KeyCode.G) {
                    gPressed.set(false);
                }
            }
        });


    }




    public void onEnter(KeyEvent e){
        if(e.getCode().toString().equals("ENTER")){
            sendMsg();
        }
    }



    public void generateButtons() throws SQLException, ClassNotFoundException{


    }



    public void sendMsg(){
        ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
        nvp.add(new BasicNameValuePair("nralcode", constants.getValue("s_nralcode")));
        nvp.add(new BasicNameValuePair("msg", txtMsg.getText()));
        String response = httpr.ServicePost("chatMsg.php", nvp);
        System.out.println(response) ;
        txtMsg.setText("");
    }


    public void uploadRecords(final String prgcode) throws SQLException, ClassNotFoundException{
        uploadlbl.setText("Uploading...please wait..");

        gridBtn.setVisible(false);


        Thread timer = new Thread(){

            public void run(){

                try{
                    sleep(600);

                }catch(InterruptedException e){
                    e.printStackTrace();


                }finally{

                    try {
                        uploadme(prgcode);
                    } catch (SQLException ex) {
                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }


        };


        timer.start();
    }


    public void uploadme(String prgcode) throws SQLException, ClassNotFoundException {

        ResultSet result = DisplayDatabase.listToBeUploaded(constants.getValue("nralcode"), prgcode);
        ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
        ArrayList<String> fldnames = DisplayDatabase.listAllFldName(constants.getValue("nralcode"), prgcode);

        int prog = 0;
        int i = 0;
        int stats = 0;
        String strtotal = DisplayDatabase.countPending(prgcode,constants.getValue("nralcode"),1);
        int total = Integer.parseInt(strtotal);
        txtuploaded.setVisible(true);
        while(result.next()){
            for (String fldname : fldnames) {
                nvp.add( new BasicNameValuePair(fldname, result.getString(fldname)));
            }

            nvp.add( new BasicNameValuePair("`NRALcode`", constants.getValue("nralcode")));
            nvp.add( new BasicNameValuePair("`Prgcode`", prgcode));
            String response = httpr.ServicePost("dataReceiver.php", nvp);

            if(response.contains("<br>") || response.equalsIgnoreCase("error")){
                uploadlbl2.setVisible(true);

                stats = 1;
                break;
            }else{
                System.out.println(response);

                DisplayDatabase.updateStatus(result.getString("reckid"), "uploaded");

                prog = i/total;

                i++;
                txtuploaded.setText(i+" of "+total+" records uploaded!");
            }
        }

        if(stats == 1){
            uploadlbl1.setVisible(false);
            uploadlbl.setVisible(false);
            txtuploaded.setVisible(false);
        }else{
            uploadlbl1.setVisible(true);
            uploadlbl.setVisible(false);
            lblPending.setVisible(false);
            txtuploaded.setVisible(false);
        }

    }


    @FXML
    void openSettings() throws IOException {
        btnSettings.setVisible(false);
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/homis/views/Settings.fxml"));
        Parent root = fxmlLoader.load();
        SettingsController controller = fxmlLoader.<SettingsController>getController();
        stage.setScene(new Scene(root));
//        controller.initialize();
        stage.setWidth(Screen.getMainScreen().getWidth()*0.60);
        stage.setHeight(Screen.getMainScreen().getHeight()*0.70);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();


    }






}
