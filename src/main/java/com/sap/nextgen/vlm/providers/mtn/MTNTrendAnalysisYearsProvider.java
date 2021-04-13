package com.sap.nextgen.vlm.providers.mtn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.request.ResultContainer;
import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.nextgen.vlm.providers.AbstractProvider;
import com.sap.nextgen.vlm.providers.DataProvider;
import com.sap.nextgen.vlm.rmo.GetMTNSearchResultRMO;
import com.sap.nextgen.vlm.rmo.MTNPeerProfileRMO;
import com.sap.nextgen.vlm.rmo.MTNTrendAnalysisYearsRMO;
import com.sap.nextgen.vlm.utils.MTNFlags;
import com.sap.nextgen.vlm.utils.DBQueryManager;
import com.sap.nextgen.vlm.utils.DbConnection;

public class MTNTrendAnalysisYearsProvider extends AbstractProvider implements DataProvider<MTNTrendAnalysisYearsRMO> {
    int mtnId;
    String jwtToken; 
    String clientProcessId;    
    int langId = 10;
    
    @Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.GET_MTN_TREND_ANALYSIS_YEARS;
    }
    @SuppressWarnings("finally")
	@Override
    public ResultContainer<MTNTrendAnalysisYearsRMO> loadData(DataRequestBody requestBody) throws SQLException {
    	
    	Map<String, List<String>> queryParams = Optional.ofNullable(requestBody.getQueryParams()).orElse(new HashMap<>());
    	if (queryParams.containsKey("mtnId")) {
    		mtnId = Integer.parseInt(requestBody.getQueryParams().get("mtnId").get(0));
    	}
    	if (queryParams.containsKey("clientProcessId")) {
    		clientProcessId = requestBody.getQueryParams().get("clientProcessId").get(0)+"_intwomtn";
    	}else {
    		clientProcessId = mtnId+"_intwomtn";
    	}    	
    	if (queryParams.containsKey("jwtToken")) {
    		jwtToken = requestBody.getQueryParams().get("jwtToken").get(0);
    	}
    	if (queryParams.containsKey("langId")) {
    		langId = Integer.parseInt(requestBody.getQueryParams().get("langId").get(0));
    	}
    	
    	final List<MTNTrendAnalysisYearsRMO> data = new ArrayList<MTNTrendAnalysisYearsRMO>();
    	//fetch the years data from HANA
    	Connection conn =  DbConnection.getConnection();
		CallableStatement cSt = null;
        ResultSet rs = null;
         if (conn != null) {        	
            try {
            	String sql = "call USP_GET_MTN_TREND_ANALYSIS_YEARS(?,?,?,?,?,?)";
                cSt = conn.prepareCall(sql);
                if (cSt == null) {
                    System.out.println("error preparing call: " + sql);	                   
                }
                cSt.setInt(1, mtnId);
                cSt.setString(2, clientProcessId);
                cSt.setInt(3, 1);
                cSt.setInt(4, 52);
                cSt.setInt(5, langId);
                
                boolean moreResults = cSt.execute();  
                while (moreResults) {
                    rs = cSt.getResultSet();                        
                    while (rs.next()) {
                    	System.out.println(rs.getInt(1));
                    	MTNTrendAnalysisYearsRMO rmo = new MTNTrendAnalysisYearsRMO();
                    	rmo.setYear(rs.getInt(1));
                    	data.add(rmo);
                    }                   
                    rs.close();
                    moreResults = cSt.getMoreResults();
                } 
                cSt.close();                
                
            } catch (Exception se) {
                se.printStackTrace();
            } 
            finally { 
					conn.close();
					rs.close();
					cSt.close();
					
            }
        	 
        }
         return new ResultContainer<>(data, MTNTrendAnalysisYearsRMO.class);
    }
    
}
