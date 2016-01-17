/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homis.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author troll
 */
public class DBConnection {
    private static Connection conn;  
    private static String url = Settings.url;
    private static String user = Settings.user;
    private static String pass = Settings.pass;
    public static Connection connect() throws SQLException{  
   
        try{
            Class.forName("com.mysql.jdbc.Driver");  
            
        }catch(ClassNotFoundException cnfe){  
            System.err.println("Error: "+cnfe.getMessage());  
        } 
        conn = DriverManager.getConnection(url,user,pass);  
        return conn;  
    }
    public static Connection getConnection() throws SQLException, ClassNotFoundException{  
     if(conn !=null && !conn.isClosed())  
       return conn;  
     connect();  
     return conn;  
    }  
}
