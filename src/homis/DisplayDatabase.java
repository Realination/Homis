/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homis;

/**
 *
 * @author thana
 */

import homis.app.DBConnection;
import homis.models.Linkup;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.apache.http.NameValuePair;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DisplayDatabase {
    private static ObservableList<ObservableList> data;
    private Linkup linkup = new Linkup();
    public static void buildData(TableView tableview){
        Connection c;
        data = FXCollections.observableArrayList();
        try{
            c = DBConnection.getConnection();  
        String SQL = "SELECT * from linkconfig";  
        ResultSet rs = c.createStatement().executeQuery(SQL);  
       
        for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){  
        
            final int j = i;          
            TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));  
            col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){            
                public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                 
                return new SimpleStringProperty(param.getValue().get(j).toString());              
                }            
            });  
            tableview.getColumns().addAll(col);  
        }
        while(rs.next()){  
            ObservableList<String> row = FXCollections.observableArrayList();  
            for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){  
                row.add(rs.getString(i));  
            }  
            data.add(row);  
        }  
       
        tableview.setItems(data);  
        }catch(Exception e){  
            e.printStackTrace();  
            System.out.println("Error on Building Data");        
        }  
    }  
    
    public static ArrayList<String> listAllPrgCodes(){
        ArrayList<String> prgcodes = new ArrayList<String>();
        Connection c;
        try{
            c = DBConnection.getConnection();
            Statement stmt = c.createStatement();
            ResultSet result = stmt.executeQuery("SELECT prgcode FROM linkfld GROUP BY prgcode ORDER BY prgcode");
            while(result.next()){
                prgcodes.add(result.getString("prgcode").toString());                
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error");
        }
        return prgcodes;
    }
    
    public static ArrayList<String> listAllFldName(String nralcode, String prgcode){
        ArrayList<String> fldnames = new ArrayList<String>();
        Connection c;
        try{
            c = DBConnection.getConnection();
            Statement stmt = c.createStatement();
            ResultSet result = stmt.executeQuery("SELECT fldname, fldlbl FROM linkfld WHERE mainsub = 'M' AND nralcode = '"+ nralcode +"' AND prgcode = '"+ prgcode +"'");
            fldnames.add("reckid");
            while(result.next()){
                fldnames.add(result.getString("fldname"));
            }
            fldnames.add("status");
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error");
        }
        return fldnames;
        
    }
    
    public static ResultSet listToBeUploaded(String nralcode, String prgcode){
        ArrayList<NameValuePair> pending = new ArrayList<NameValuePair>();
        ResultSet result = null;
                
        Connection c;
        try{
            c = DBConnection.getConnection();
            Statement stmt = c.createStatement();
            
            ArrayList<String> fldnames = DisplayDatabase.listAllFldName(nralcode, prgcode);
            System.out.println("--"+prgcode+"--");
            String flds = "";
            for (String fldname : fldnames) {
                flds += fldname+", ";
            }
            flds = flds.substring(0, flds.length()-2);
            result = stmt.executeQuery("SELECT reckid,"+ flds +" FROM linkup WHERE status <> 'uploaded' AND nralcode = '"+ nralcode +"' AND prgcode = '"+ prgcode +"'");
//            while(result.next()){
//                 System.out.println("record!!!!!!!!");
//                for (String fldname : fldnames) {
//                     System.out.println(fldname+"----"+result.getString(fldname));
//                }
//                
//            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error");
        }
        return result;
    }

    public static ResultSet listToBeUploaded(){
        ArrayList<NameValuePair> pending = new ArrayList<NameValuePair>();
        ResultSet result = null;

        Connection c;
        try{
            c = DBConnection.getConnection();
            Statement stmt = c.createStatement();


            result = stmt.executeQuery("SELECT *  FROM linkup WHERE status <> 'uploaded' ");

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error");
        }
        return result;
    }


    public static ResultSet getEdited(){
        ArrayList<NameValuePair> pending = new ArrayList<NameValuePair>();
        ResultSet result = null;

        Connection c;
        try{
            c = DBConnection.getConnection();
            Statement stmt = c.createStatement();


            result = stmt.executeQuery("SELECT *  FROM linkup WHERE last_uploaded != datetime ");

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error");
        }
        return result;
    }


    public static ResultSet getPrgs() throws SQLException, ClassNotFoundException{
        
         Connection c;
        c = DBConnection.getConnection();
            Statement stmt = c.createStatement();
       
            String query = "SELECT prgname,prgcode FROM `linkconfig`";
        System.out.println(query);
            ResultSet result = stmt.executeQuery(query);
        return result;
        
    }
    
    
    
    
    public static String countPending(String prgcode,String nralcode,int type) throws SQLException, ClassNotFoundException{
        String count;
        String count2;
         Connection c;
        c = DBConnection.getConnection();
            Statement stmt = c.createStatement();
       
            String query = "SELECT count(reckid) as reccount,last_uploaded FROM linkup WHERE status <> 'uploaded' AND nralcode = '"+ nralcode +"' AND prgcode = '"+ prgcode +"'";
            ResultSet result = stmt.executeQuery(query);
            if(result.next()){
                count = result.getString("reccount");
                count2 = result.getString("reccount");
            }else{
                count = "0";
                count2 = "0";
            }
        
            if(type == 1){
                return count;
            }else{
                return count2;
            }
            
        
    };
    
    public static void updateStatus(String reckid, String status){
        Connection c;
        
        try{
            c=DBConnection.getConnection();
            String query = "UPDATE linkup SET status =?, last_uploaded = ? WHERE reckid = ?";
            
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
           
            
            
            PreparedStatement preparedStmt = c.prepareStatement(query);
            preparedStmt.setString(1, status);
            preparedStmt.setString(2, dateFormat.format(cal.getTime()));
            preparedStmt.setString(3, reckid);
 
      // execute the java preparedstatement
            preparedStmt.executeUpdate();
       
//            c.close();
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error");
        }
    }
    
}
