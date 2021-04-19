package com.sap.nextgen.vlm.providers.mtn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.http.*;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.request.ResultContainer;
import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.nextgen.vlm.providers.AbstractProvider;
import com.sap.nextgen.vlm.providers.DataProvider;
import com.sap.nextgen.vlm.rmo.MtnDashboardRMO;
import java.sql.*;
import com.sap.nextgen.vlm.utils.*;

public class GetMTNDashboardDataProvider extends AbstractProvider implements DataProvider<MtnDashboardRMO> {
	 
	    String clientProcessId;	    
	    int langId = 10;
	    String baseUri = "https://vlmdev.cfapps.eu10.hana.ondemand.com";
	    String jwtToken; 
	    Connection conn;
    @Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.MTN_DASHBOARD_DATA;
    }

    @Override
    public ResultContainer<MtnDashboardRMO> loadData(DataRequestBody requestBody)  throws ClientProtocolException, IOException, SQLException {
        
        String baseUri = "https://vlmdev.cfapps.eu10.hana.ondemand.com";
    	CloseableHttpClient httpclient = HttpClients.createDefault();
    	Map<String, List<String>> queryParams = Optional.ofNullable(requestBody.getQueryParams()).orElse(new HashMap<>());
    	
    	if (queryParams.containsKey("clientProcessId")) {
    		clientProcessId = requestBody.getQueryParams().get("clientProcessId").get(0);
    	}else {
    		clientProcessId = "intwomtn";
    	}
    	if (queryParams.containsKey("langId")) {
    		langId = Integer.parseInt(requestBody.getQueryParams().get("langId").get(0));
    	}    	
    	if (queryParams.containsKey("jwtToken")) {
    		jwtToken = requestBody.getQueryParams().get("jwtToken").get(0);
    		System.out.println(jwtToken);
    	}
    	
    	HttpGet get = new HttpGet(baseUri +"/services/getMTNList?langId="+langId+"&clientProcessId="+clientProcessId+"&seqNo=8");
    	get.setHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
    	get.setHeader("Cookie", "UserData="+jwtToken);
    	
    	CloseableHttpResponse httpResponse = httpclient.execute(get);
    	String response = EntityUtils.toString(httpResponse.getEntity());
    	
    	final List<MtnDashboardRMO> data = new ArrayList<MtnDashboardRMO>();
    	ObjectMapper mapper = new ObjectMapper();
    	JsonNode root = mapper.readTree(response);
    	
    	System.out.println(root);
    	JsonNode mtnListNode=root.get("results").get(0).get("mtnList");
    	Iterator<JsonNode> elements = mtnListNode.elements();
         while(elements.hasNext()){
        	String mtnjsonstring =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(elements.next());
        	data.add(mapper.readValue(mtnjsonstring, MtnDashboardRMO.class));
         } 
        
        ResultSet rs = DBQueryManager.getResultSet("Select \"T0014_FirstName\", \"T0014_LastName\" from \"T0014_User\" where \"T0014_ID\" =52",conn);
        rs.next();
        System.out.println(rs.getString(1)); 
        if(conn != null) {
        	conn.close();	
        }
        return new ResultContainer<>(data, MtnDashboardRMO.class);
        
        
        
    }

	


//    private Double calculateGrowth(Double d1, Double d2) {
//        return div(diff(d1, d2), d2);
//    }
}
