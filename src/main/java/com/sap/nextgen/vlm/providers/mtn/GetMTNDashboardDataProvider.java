package com.sap.nextgen.vlm.providers.mtn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    @Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.MTN_DASHBOARD_DATA;
    }

    @Override
    public ResultContainer<MtnDashboardRMO> loadData(DataRequestBody requestBody)  throws ClientProtocolException, IOException, SQLException {
        
        String baseUri = "https://vlmdev.cfapps.eu10.hana.ondemand.com";
    	String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjQ4ODAxLCJmaXJzdE5hbWUiOiJNYXJpYW4iLCJsYXN0TmFtZSI6IkluZm8iLCJlbWFpbCI6ImluZm9AZXN0aW1hdGUuc2siLCJsYW5nSWQiOjEwLCJsYW5nTmFtZSI6IkVuZ2xpc2giLCJjb21wYW55TmFtZSI6ImVzdGltIiwidXNlclR5cGUiOiJHZW5lcmFsIFVzZXIiLCJ1c2VyQ2F0ZWdvcnkiOjIsImlzV2hhdHNOZXdBdmFpbGFibGUiOjEsImlhdCI6MTYxNzk1NTgwOSwiZXhwIjoxNjE4ODE5ODA5fQ.89agJOgUJ3gPIQpf_D6Gw8xZntDQZ_BG6zIS1wVCBlE";
    	CloseableHttpClient httpclient = HttpClients.createDefault();
    	
    	HttpGet get = new HttpGet(baseUri +     "/services/getMTNList?langId=10&pageOffset=0&clientProcessId=20210304140829nlmahf2b65s6&seqNo=8");
    	get.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
    	get.setHeader("x-auth-token", token);
    	
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
        
        ResultSet rs = DBQueryManager.getResultSet("Select \"T0014_FirstName\", \"T0014_LastName\" from \"T0014_User\" where \"T0014_ID\" =52");
        rs.next();
        System.out.println(rs.getString(1)); 
        return new ResultContainer<>(data, MtnDashboardRMO.class);
        
        
        
    }

	


//    private Double calculateGrowth(Double d1, Double d2) {
//        return div(diff(d1, d2), d2);
//    }
}
