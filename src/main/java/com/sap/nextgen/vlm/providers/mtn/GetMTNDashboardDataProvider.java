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
    public ResultContainer<MtnDashboardRMO> loadData(DataRequestBody requestBody)  throws ClientProtocolException, IOException {
       //resolveRequestBody(requestBody);

//        RemoteQueryBridge.Client client = getBridgeClient();
//
//        Map<String, List<String>> queryParams = Optional.ofNullable(requestBody.getQueryParams()).orElse(new HashMap<>());
//
//        String quarter = YearQuarter.now().toString();
//
//        List<String> regions = Lists.newArrayList("AP", "EMNORTH", "EMSOUTH", "GCN", "MEE", "LA", "NA");
//        List<String> salesbags = Lists.newArrayList(
//                "S4H Cloud ES - Enterprise",
//                "S4H Cloud ES - Tech Addons & Serv",
//                "S4H Cloud EX - EMS",
//                "S4H Cloud EX - Enterprise",
//                "S4H Cloud EX - Suite Foundation",
//                "S4H Cloud EX - Tech Addons & Serv");
//
//        List<String> products = Lists.newArrayList(
//                "DCL",
//                "DSC",
//                "ERP",
//                "FCL",
//                "FIN",
//                "FS4",
//                "HEE",
//                "PC4",
//                "S4H",
//                "SBP",
//                "SOP",
//                "SPC",
//                "SPO");
//
//        if (queryParams.containsKey("quarter")) {
//            quarter = requestBody.getQueryParams().get("quarter").get(0);
//        }
//        if (queryParams.containsKey("regions")) {
//            regions = requestBody.getQueryParams().get("regions");
//        }
        
        String baseUri = "https://vlmdev.cfapps.eu10.hana.ondemand.com";
    	String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjQ4ODAxLCJmaXJzdE5hbWUiOiJNYXJpYW4iLCJsYXN0TmFtZSI6IkluZm8iLCJlbWFpbCI6ImluZm9AZXN0aW1hdGUuc2siLCJsYW5nSWQiOjEwLCJsYW5nTmFtZSI6IkVuZ2xpc2giLCJjb21wYW55TmFtZSI6ImVzdGltIiwidXNlclR5cGUiOiJHZW5lcmFsIFVzZXIiLCJ1c2VyQ2F0ZWdvcnkiOjIsImlzV2hhdHNOZXdBdmFpbGFibGUiOjEsImlhdCI6MTYxNzA4NzUxMiwiZXhwIjoxNjE3OTUxNTEyfQ.twvEECFT45utyp03f7VPho4Ijt6nR2CFEk9hmyqiCvg";
    	CloseableHttpClient httpclient = HttpClients.createDefault();
    	
    	HttpGet get = new HttpGet(baseUri +     "/services/getMTNList?langId=10&pageOffset=0&clientProcessId=20210304140829nlmahf2b65s6&seqNo=8");
    	get.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
    	get.setHeader("x-auth-token", token);
    	
    	CloseableHttpResponse httpResponse = httpclient.execute(get);
    	String response = EntityUtils.toString(httpResponse.getEntity());
    	
    	final List<MtnDashboardRMO> data = new ArrayList<MtnDashboardRMO>();
    	ObjectMapper mapper = new ObjectMapper();
    	//JSONObject result = new JSONObject(response);
    	// JSONArray mtnlist = result.getJSONArray("mtnList");
    	JsonNode root = mapper.readTree(response);
    	
    //	 System.out.println(prettyPrintEmployee+"\n");
    	System.out.println(root);
    	JsonNode mtnListNode=root.get("results").get(0).get("mtnList");
    	Iterator<JsonNode> elements = mtnListNode.elements();
         while(elements.hasNext()){
        	String mtnjsonstring =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(elements.next());
            //JsonNode mtnnode = elements.next();
           data.add(mapper.readValue(mtnjsonstring, MtnDashboardRMO.class));
         }
         
        
    	//System.out.println( mtnListNode);
    	//final List<MtnDashboardRMO> data  = (List<MtnDashboardRMO>) mapper.readValue(response, MtnDashboardRMO.class);

    
    	//System.out.println(result);
    	
    	//JsonObject obj = new JsonParser().parse(response).getAsJsonObject();
    	//g.toJson(obj);
    	//System.out.println(obj.get("mtnList"));

    	
    	
      //  final List<MtnDashboardRMO> data = new ArrayList<MtnDashboardRMO>();
      //  MtnDashboardRMO obj1 = new MtnDashboardRMO();
      //  obj1.setMtnId(1011);
       // obj1.setCompanyName("Test MTN");
       // data.add(obj1);
         
       //HANA connection test
         /*
         CallableStatement cSt = null;
         ResultSet rs = null;
         String sql = "call USP_SURVEYDASHBOARD(?,?,?,?,?,?,?,?,?,?,?)";
        Connection conn =  DbConnection.getConnection();
        
        try {
            cSt = conn.prepareCall(sql);
            if (cSt == null) {
                System.out.println("error preparing call: " + sql);
               
            }
            cSt.setString(1, "testbudh");
            cSt.setInt(2, 52);
            cSt.setInt(3, 10);
            cSt.setInt(4, 0);
            cSt.setInt(5, 0);
            cSt.setString(6, "test");
            cSt.setString(7, "testbudh");
            cSt.setInt(8, 1);
            
            boolean res = cSt.execute();
            System.out.println("result: " + res);
            System.out.println("Connection to HANA successful!");
            do {
                rs = cSt.getResultSet();
                while (rs != null && rs.next()) {
                    System.out.println("row: " + rs.getString(1) + ", " +
                              rs.getString(2) + ", " + rs.getString(3));
                }
            } while (cSt.getMoreResults());
         // connection.close();
            if (rs != null)
                rs.close();
            if (cSt != null)
                cSt.close();
            
        } catch (Exception se) {
            se.printStackTrace();
        } 
        finally {
        	// connection.close();
            if (rs != null)
                rs.close();
            if (cSt != null)
                cSt.close();
        }*/
        
         Connection connection =  DbConnection.getConnection();
         if (connection != null) {
            try {
               System.out.println("Connected");
               Statement stmt = connection.createStatement();
              // string sql = "select * from "
               ResultSet resultSet = stmt.executeQuery("Select \"T0014_FirstName\", \"T0014_LastName\" from \"T0014_User\" where \"T0014_ID\"  =52 ");               
               connection.close();
               resultSet.next();
              // String hello = resultSet.getString(1);
               System.out.println(resultSet.getString(1) + " " + resultSet.getString(2));
          } catch (SQLException e) {        	  
             System.err.println("Some error occured in execution" + e);
          }
        }
         
         //HANA connection test  - end
         
        return new ResultContainer<>(data, MtnDashboardRMO.class);
        
        
        
    }


//    private Double calculateGrowth(Double d1, Double d2) {
//        return div(diff(d1, d2), d2);
//    }
}
