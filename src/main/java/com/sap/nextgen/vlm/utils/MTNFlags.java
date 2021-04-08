package com.sap.nextgen.vlm.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;




public class MTNFlags {
	
	public int isPeerDataAvailable;
	public int isQuesDataAvailable;
	public int isTrendAnalysisAvailable;
	
	public MTNFlags(int mtnId) {
		fetchMTNFlags(mtnId);
	}
	
	public void fetchMTNFlags(int mtnId) {
		   Connection conn =  DbConnection.getConnection();
		   CallableStatement cSt = null;
            ResultSet rs = null;
	         if (conn != null) {
	        	 /*
	            try {	             
	               Statement stmt = connection.createStatement();
	              // string sql = "select * from "
	               ResultSet resultSet = stmt.executeQuery("Select \"T0402_IsPeerDataAvailable\", \"T0402_IsQuesDataAvailable\", \"T0402_isTrendAnalysisAvailable\" from \"T0402_MTN_Attributes\" where \"T0401_AutoID\"  = " + mtnId);               
	               connection.close();
	               resultSet.next();
	              // String hello = resultSet.getString(1);
	             
	               this.isPeerDataAvailable = resultSet.getString(1);
	               this.isQuesDataAvailable = resultSet.getString(2);
	               this.isTrendAnalysisAvailable = resultSet.getString(3);
	          } catch (SQLException e) {        	  
	             System.err.println("Some error occured in execution" + e);
	          }
	          */
	        	 
	        	 
	            
	             String sql = "call USP_GET_MTN_FLAGS(?,?)";
	          //  Connection conn =  DbConnection.getConnection();
	            
	            try {
	                cSt = conn.prepareCall(sql);
	                if (cSt == null) {
	                    System.out.println("error preparing call: " + sql);	                   
	                }	               
	                cSt.setInt(1, mtnId);
	                boolean moreResults = cSt.execute();  
	                while (moreResults) {
	                    rs = cSt.getResultSet();
	                    rs.next();                  
	                   this.isPeerDataAvailable = rs.getInt("T0402_IsPeerDataAvailable");
	                   this.isQuesDataAvailable = rs.getInt("T0402_IsQuesDataAvailable");
	                   this.isTrendAnalysisAvailable = rs.getInt("T0402_isTrendAnalysisAvailable");
	                    rs.close();
	                    moreResults = cSt.getMoreResults();
	                } 
	                cSt.close(); 
	                /*
	                do {
	                    rs = cSt.getResultSet();
	                    
	                    while (rs != null && rs.next()) {
	                    	System.out.println( rs);
	                    	System.out.println("cSt: " + cSt);
	                        System.out.println("row: " + rs.getInt(1) + ", " +
	                                  rs.getInt(2) + ", " + rs.getInt(3));
	                    }
	                } while (cSt.getMoreResults());
	             // connection.close();
	                if (rs != null)
	                    rs.close();
	                if (cSt != null)
	                    cSt.close();
	                    */
	                
	            } catch (Exception se) {
	                se.printStackTrace();
	            } 
	            finally {
	            	
	            	 try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                if (rs != null)
						try {
							rs.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                if (cSt != null)
						try {
							cSt.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    
	            }
	        	 
	        }
	}
	
}
	

