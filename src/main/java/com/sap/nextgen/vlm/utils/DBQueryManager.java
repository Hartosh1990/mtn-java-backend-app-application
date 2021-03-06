package com.sap.nextgen.vlm.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBQueryManager {

	public static ResultSet getResultSet(String sqlQuery,Connection connection) throws SQLException {
		//String sqlQuery = "Select \"T0014_FirstName\", \"T0014_LastName\" from \"T0014_User\" where \"T0014_ID\" =52"; 
        connection =  DbConnection.getConnection();
        ResultSet resultSet = null;
        if (connection != null) {
           try {
               System.out.println("Connected");
               Statement stmt = connection.createStatement();
               resultSet = stmt.executeQuery(sqlQuery);               
          } catch (SQLException e) { 
        	 connection.close();
             throw new SQLException("Cause:" + e.getCause() + "SQL Error Code :" + e.getErrorCode());
          }
        }
        return resultSet;
	}
}
