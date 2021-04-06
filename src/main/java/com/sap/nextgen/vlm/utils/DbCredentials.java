package com.sap.nextgen.vlm.utils;



import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.jdbc.CfJdbcEnv;

public class DbCredentials {
	
    private static DbCredentials dbCredentials = null;
    String jdbcUrl;
    
    private DbCredentials()
    {
    	
    	CfJdbcEnv cfJdbcEnv = new CfJdbcEnv();
    	
    	if (cfJdbcEnv.isInCf()) {   //application is running in CF 	
    	CfCredentials hanaCredentials = cfJdbcEnv.findCredentialsByName("hanaschemadetail"); 
    	if (hanaCredentials != null) {
			jdbcUrl = hanaCredentials.getUri();
		}
    	}
    	else {  //application running locally
    		jdbcUrl = "jdbc:sap://localhost:30015/?user=nextgen&password=123!@#Nextgen";
    	}	
    	
    }  
    
    public static DbCredentials getDbCredentials()
    {
        if (dbCredentials == null)
        	dbCredentials = new DbCredentials(); 
        
        return dbCredentials;
    }
  
}
