/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homis.app;

import org.apache.http.NameValuePair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author DarkMatter
 */
public abstract class Model {
    
    public abstract String tblName();
    public abstract String primaryKey();

    
     public void save(ArrayList<NameValuePair> data) {
        Connection conn;
       String fields = "";
       String fillers = "";
       int c = 0;
       for (NameValuePair datum : data) {
            fields += "`"+datum.getName()+"`";
            fillers += "?";
            if(data.size()-1 > c){
                fields += ",";
                fillers +=",";
            }
            c++;
       }
        
        try{
            conn= DBConnection.getConnection();
            String query = "INSERT INTO `"+tblName()+"` ("+fields+")VALUES("+fillers+")";
          
            System.out.println(query);    
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            int i = 1; 
            for (NameValuePair datum : data) {
                preparedStmt.setString(i, datum.getValue());
                i++;
             }
          
 
      // execute the java preparedstatement
            preparedStmt.execute();
       
            
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error");
              
        }
      
    }


    public void save(ArrayList<NameValuePair> data,String id) {
        Connection conn;
        String fields = "";

        int c = 0;
        for (NameValuePair datum : data) {
            fields += "`"+datum.getName()+"` = ?";
            if(data.size()-1 > c){
                fields += ",";
            }
            c++;
        }

        try{
            conn= DBConnection.getConnection();
            String query = "UPDATE `"+tblName()+"` SET  "+fields+" WHERE "+primaryKey()+"='"+id+"'";
            System.out.println(query);
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            int i = 1;
            for (NameValuePair datum : data) {
                preparedStmt.setString(i, datum.getValue());
                i++;
            }


            // execute the java preparedstatement
            preparedStmt.execute();


        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error");

        }

    }

     
     public ResultSet get(){
         ResultSet result = null;
                
        Connection conn;
        try{
            conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            result = stmt.executeQuery("SELECT * FROM "+tblName());
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error");
        }
        return result;
     }
    
     public ResultSet get(String id){
         ResultSet result = null;
                
        Connection conn;
        try{
            conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            result = stmt.executeQuery("SELECT * FROM "+tblName()+" WHERE "+primaryKey()+" = '"+id+"'");
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error");
        }
        return result;
     }
   
     
     public ResultSet get(ArrayList<NameValuePair> data){
          ResultSet result = null;
           String where="";     
            
           int i=0;
           for (NameValuePair datum : data) {
                where += " `"+datum.getName()+"` LIKE '"+datum.getValue()+"'";
                    if(data.size()-1 > i){
                        where += " AND";
                    }
                i++;
             }
          
        Connection conn;
        try{
            conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM `"+tblName()+"` WHERE "+where;
            result = stmt.executeQuery(sql);
            System.out.println(sql);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error");
        }
        return result;
     }
     
     public ResultSet query(String query){
         ResultSet result = null;

         Connection conn;
         try{
             conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             result = stmt.executeQuery(query);
         }catch(Exception e){
             e.printStackTrace();
             System.out.println("Error");
         }
         return result;
     }
    
}
