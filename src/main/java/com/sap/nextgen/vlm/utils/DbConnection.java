package com.sap.nextgen.vlm.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    public static Connection getConnection() {
    	
    	DbCredentials cred =  DbCredentials.getDbCredentials();
    	
    	 Connection connection = null;
         try {                  
            connection = DriverManager.getConnection(
               cred.jdbcUrl);                  
         } catch (SQLException e) {
            System.err.println("Connection Failed:");
            System.err.println(e);
          
         }
         return connection;
    }
}
