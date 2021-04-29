package com.sap.nextgen.vlm.providers.mtn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.request.ResultContainer;
import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.nextgen.vlm.providers.AbstractProvider;
import com.sap.nextgen.vlm.providers.DataProvider;
import com.sap.nextgen.vlm.rmo.MTNPeerProfileRMO;
import com.sap.nextgen.vlm.rmo.PeerInfoRMO;
import com.sap.nextgen.vlm.rmo.PeersDeleteListRMO;
import com.sap.nextgen.vlm.utils.HttpRequestManager;
import com.sap.nextgen.vlm.utils.MTNFlags;

public class MTNPeerProfileProvider extends AbstractProvider implements DataProvider<MTNPeerProfileRMO> {
    
   
    @Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.GET_MTN_PEER_PROFILE;
    }
    @SuppressWarnings("finally")
	@Override
    public ResultContainer<MTNPeerProfileRMO> loadData(DataRequestBody requestBody) throws JsonMappingException, JsonProcessingException, ClientProtocolException, IOException, SQLException {
    	String mtnId = null;
        String ciqId = null;
        String newCompanyName = null;
        String isMtnCompany = "0";
        String baseUri = "https://vlmdev.cfapps.eu10.hana.ondemand.com";
        String jwtToken = null; 
        String clientProcessId = null;    
        int langId = 10;
        String newCiqId = null;
    	String companyIdtobedeleted = null;
    	PeersDeleteListRMO plObject = new PeersDeleteListRMO();
    	Map<String, List<String>> queryParams = Optional.ofNullable(requestBody.getQueryParams()).orElse(new HashMap<>());
    	if (queryParams.containsKey("mtnId")) {
    		mtnId = requestBody.getQueryParams().get("mtnId").get(0);
    	}
    	if (queryParams.containsKey("clientProcessId")) {
    		clientProcessId = requestBody.getQueryParams().get("clientProcessId").get(0)+"_intwomtn";
    	}else {
    		clientProcessId = mtnId+"_intwomtn";
    	}
    	if (queryParams.containsKey("ciqId")) {
    		ciqId = requestBody.getQueryParams().get("ciqId").get(0);
    	}
    	if (queryParams.containsKey("jwtToken")) {
    		jwtToken = requestBody.getQueryParams().get("jwtToken").get(0);
    	}
    	if (queryParams.containsKey("langId")) {
    		langId = Integer.parseInt(requestBody.getQueryParams().get("langId").get(0));
    	}
    	if (queryParams.containsKey("newPeerCIQ")&& !queryParams.get("newPeerCIQ").isEmpty() && queryParams.get("newPeerCIQ").get(0) != null) {
    		newCiqId = queryParams.get("newPeerCIQ").get(0);
    	}
    	if (queryParams.containsKey("newPeerCompName") && !queryParams.get("newPeerCompName").isEmpty() && queryParams.get("newPeerCompName").get(0) != null) {
    		newCompanyName = requestBody.getQueryParams().get("newPeerCompName").get(0);
    	}
    	if(queryParams.containsKey("companyId")&& !queryParams.get("companyId").isEmpty() && queryParams.get("companyId").get(0) != null) {
    		companyIdtobedeleted = queryParams.get("companyId").get(0);
    		PeerInfoRMO pInfoRMO = new PeerInfoRMO();
    		pInfoRMO.setCompanyId(companyIdtobedeleted);
    		List<PeerInfoRMO> pl = List.of(pInfoRMO);
    		plObject.setPeers(pl);
    	}
    	
    	//fetch the MTN flags
    	MTNFlags mtnFlags = new MTNFlags(Integer.parseInt(mtnId));
    	System.out.println(mtnFlags.isPeerDataAvailable + " BUDH " + mtnFlags.isQuesDataAvailable);
    	
    	

        final List<MTNPeerProfileRMO> data = new ArrayList<MTNPeerProfileRMO>();
        ObjectMapper mapper = new ObjectMapper();
        String addPeerUri = baseUri+"/services/saveCompanyForMtn?langId=" + langId + "&clientProcessId=" + clientProcessId +"&seqNo=0";
        if(newCiqId != null) {
        	String json = "{\"Response\": [{\"ciq_id\" :\"" + newCiqId + "\", \"companyName\": \"" + newCompanyName + "\", \"isMtnCompany\": \"" + isMtnCompany + "\", \"mtnid\":" + mtnId + "}]}";
        	JsonNode addedPeer = HttpRequestManager.getRootObjectFromPostNodeService(jwtToken, addPeerUri, json);
        	if(addedPeer != null && addedPeer.get("success")!= null) {
  				if(addedPeer.get("success").asBoolean()) {
  					System.out.println("The Peer with cid "+ newCiqId + " is added successfully");
  				};
  			}
        }
        if(companyIdtobedeleted != null) {
        	String json = mapper.writeValueAsString(plObject);
        	String uri = baseUri + "/services/deleteMTNCompany?langId=" + langId + "&clientProcessId=" + clientProcessId +"&seqNo=0&mtnId="+mtnId;
        	JsonNode deletedPeer = HttpRequestManager.getRootObjectFromPostNodeService(jwtToken, uri, json);
        	if(deletedPeer != null && deletedPeer.get("success")!= null) {
  				if(deletedPeer.get("success").asBoolean()) {
  					System.out.println("The Peer with company id "+ companyIdtobedeleted + " is deleted successfully");
  				};
  			}
        }
        
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost(baseUri +"/services/getMtnProfileData?langId=" + langId + "&clientProcessId=" + clientProcessId +"&seqNo=8");
		post.setHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
		post.setHeader("Cookie", "UserData="+jwtToken);		
        String json = "{\"ciq_id\" :\"" + ciqId + "\", \"isPeerDataAvailable\": " + mtnFlags.isPeerDataAvailable + ",  \"mtnid\":" + mtnId + "}";
      
			
		try {
			StringEntity entity = new StringEntity(json);				
			post.setEntity(entity);	
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
	    CloseableHttpResponse httpResponse;
		try {
			httpResponse = httpclient.execute(post);
			String response = EntityUtils.toString(httpResponse.getEntity());
			JsonNode root = mapper.readTree(response); 
			JsonNode peerList = root.get("results").get(0).get("peerData");
			data.addAll(mapper.readValue(new TreeTraversingParser(peerList),new TypeReference<List<MTNPeerProfileRMO>>(){})); 	
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		finally {
			return new ResultContainer<>(data, MTNPeerProfileRMO.class);
		}	
				
    	
    }
    
}
