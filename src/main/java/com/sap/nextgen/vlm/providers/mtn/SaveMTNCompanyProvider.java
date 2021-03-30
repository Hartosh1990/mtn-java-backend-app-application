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
import com.sap.nextgen.vlm.rmo.SaveMTNCompanyRMO;

public class SaveMTNCompanyProvider extends AbstractProvider implements DataProvider<SaveMTNCompanyRMO> {
    String mtnId;
    String ciqId;
    String companyName;
    String isMtnCompany;
    String baseUri = "https://vlmdev.cfapps.eu10.hana.ondemand.com";
    String jwtToken; 
    
    @Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.SAVE_MTN_COMPANY;
    }
    @SuppressWarnings("finally")
	@Override
    public ResultContainer<SaveMTNCompanyRMO> loadData(DataRequestBody requestBody) {

    	Map<String, List<String>> queryParams = Optional.ofNullable(requestBody.getQueryParams()).orElse(new HashMap<>());
    	if (queryParams.containsKey("mtnId")) {
    		mtnId = requestBody.getQueryParams().get("mtnId").get(0);
    	}
    	if (queryParams.containsKey("ciqId")) {
    		ciqId = requestBody. getQueryParams().get("ciqId").get(0);
    	}
    	if (queryParams.containsKey("companyName")) {
    		companyName = requestBody.getQueryParams().get("companyName").get(0);
    	}
    	if (queryParams.containsKey("isMtnCompany")) {
    		isMtnCompany = requestBody.getQueryParams().get("isMtnCompany").get(0);
    	}
    	if (queryParams.containsKey("jwtToken")) {
    		jwtToken = requestBody.getQueryParams().get("jwtToken").get(0);
    	}

        final List<SaveMTNCompanyRMO> data = new ArrayList<SaveMTNCompanyRMO>();
        
        CloseableHttpClient httpclient = HttpClients.createDefault();

        
    	HttpPost post = new HttpPost(baseUri +"/services/saveCompanyForMtn?langId=10&clientProcessId=20210304140829nlmahf2b65s6&seqNo=8");
        String json = "{\"Response\": [{\"ciq_id\" :\"" + ciqId + "\", \"companyName\": \"" + companyName + "\", \"isMtnCompany\": \"" + isMtnCompany + "\", \"mtnid\":" + mtnId + "}]}";
        try {
			StringEntity entity = new StringEntity(json);
			post.setEntity(entity);			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {

			post.setHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
			post.setHeader("Cookie", "UserData="+jwtToken);
	    	CloseableHttpResponse httpResponse;
			try {
				httpResponse = httpclient.execute(post);
				String response = EntityUtils.toString(httpResponse.getEntity());
		    	ObjectMapper mapper = new ObjectMapper();
				JsonNode root = mapper.readTree(response); 
				JsonNode companylist = root.get("results").get(0);
				data.add(mapper.treeToValue(companylist, SaveMTNCompanyRMO.class)); 		    	
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				return new ResultContainer<>(data, SaveMTNCompanyRMO.class);
			}		
		}
        
		
    	
    }
    
}
