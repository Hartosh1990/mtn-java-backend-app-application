package com.sap.nextgen.vlm.providers.mtn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import com.sap.nextgen.vlm.utils.MTNFlags;

public class MTNPeerProfileProvider extends AbstractProvider implements DataProvider<MTNPeerProfileRMO> {
    String mtnId;
    String ciqId;
    String companyName;
    String isMtnCompany;
    String baseUri = "https://vlmdev.cfapps.eu10.hana.ondemand.com";
    String jwtToken; 
    
    @Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.GET_MTN_PEER_PROFILE;
    }
    @SuppressWarnings("finally")
	@Override
    public ResultContainer<MTNPeerProfileRMO> loadData(DataRequestBody requestBody) {
    	
    	Map<String, List<String>> queryParams = Optional.ofNullable(requestBody.getQueryParams()).orElse(new HashMap<>());
    	if (queryParams.containsKey("mtnId")) {
    		mtnId = requestBody.getQueryParams().get("mtnId").get(0);
    	}
    	if (queryParams.containsKey("ciq_id")) {
    		ciqId = requestBody.getQueryParams().get("ciq_id").get(0);
    	}
    	if (queryParams.containsKey("jwtToken")) {
    		jwtToken = requestBody.getQueryParams().get("jwtToken").get(0);
    	}
    	
    	//fetch the MTN flags
    	MTNFlags mtnFlags = new MTNFlags(Integer.parseInt(mtnId));
    	  System.out.println(mtnFlags.isPeerDataAvailable + " BUDH " + mtnFlags.isQuesDataAvailable);
    	
    	

        final List<MTNPeerProfileRMO> data = new ArrayList<MTNPeerProfileRMO>();
        
        
        CloseableHttpClient httpclient = HttpClients.createDefault();

        
        HttpPost post = new HttpPost(baseUri +"/services/getMtnProfileData?langId=10&clientProcessId=testbudh1&seqNo=8&mtnId="+mtnId + "&isPeerDataAvailable="+ mtnFlags.isPeerDataAvailable + "&ciq_id="+ ciqId);
		post.setHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
		post.setHeader("Cookie", "UserData="+jwtToken);		
        String json = "{\"ciq_id\" :\"" + ciqId + "\", \"isPeerDataAvailable\": \"" + mtnFlags.isPeerDataAvailable + "\",  \"mtnid\":" + mtnId + "}";
      
			
			try {
				StringEntity entity = new StringEntity(json);				
				post.setEntity(entity);	
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
	    CloseableHttpResponse httpResponse;
		try {
			httpResponse = httpclient.execute(post);
			String response = EntityUtils.toString(httpResponse.getEntity());
		    ObjectMapper mapper = new ObjectMapper();
		   // System.out.println(response);
		    
			JsonNode root = mapper.readTree(response); 
			JsonNode peerList = root.get("results").get(0).get("peerData");;
			//JsonNode peerData = companyProfileObject.get("peerData");
			//System.out.println(peerList);
			data.addAll(mapper.readValue(new TreeTraversingParser(peerList),new TypeReference<List<MTNPeerProfileRMO>>(){})); 	
				    	
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		finally {
			return new ResultContainer<>(data, MTNPeerProfileRMO.class);
		}	
		
		
        
    	//  final List<MTNPeerProfileRMO> data = new ArrayList<MTNPeerProfileRMO>();
    	//  return new ResultContainer<>(data, MTNPeerProfileRMO.class);
		
    	
    }
    
}
