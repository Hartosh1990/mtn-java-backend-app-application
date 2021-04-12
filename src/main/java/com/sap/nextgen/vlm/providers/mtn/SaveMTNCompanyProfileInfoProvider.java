package com.sap.nextgen.vlm.providers.mtn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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
import com.sap.nextgen.vlm.rmo.SaveMTNCustomerProfileRMO;

public class SaveMTNCompanyProfileInfoProvider extends AbstractProvider implements DataProvider<SaveMTNCustomerProfileRMO> {
	String mtnId;
	String saveType;
	String id;
	String newValue;
    String jwtToken; 

	@Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.SAVE_MTN_COMPANY_PROFILE_INFO;
    }
	
    @SuppressWarnings("finally")
	@Override
    public ResultContainer<SaveMTNCustomerProfileRMO> loadData(DataRequestBody requestBody) {
    	Map<String, List<String>> queryParams = Optional.ofNullable(requestBody.getQueryParams()).orElse(new HashMap<>());
    	if (queryParams.containsKey("mtnId")) {
    		mtnId = requestBody.getQueryParams().get("mtnId").get(0);
    	}
    	if (queryParams.containsKey("saveType")) {
    		saveType = requestBody. getQueryParams().get("saveType").get(0);
    	}
    	if (queryParams.containsKey("newValue")) {
    		newValue = requestBody. getQueryParams().get("newValue").get(0);
    	}    	
    	if (queryParams.containsKey("jwtToken")) {
    		jwtToken = requestBody.getQueryParams().get("jwtToken").get(0);
    		System.out.println(jwtToken);
    	}
        final List<SaveMTNCustomerProfileRMO> data = new ArrayList<SaveMTNCustomerProfileRMO>();
        
        CloseableHttpClient httpclient = HttpClients.createDefault();

        
    	HttpPost post = new HttpPost(baseUri +"/services/saveMtnValue?langId=10&clientProcessId=20210304140829nlmahf2b65s6&seqNo=8&mtnId=" + mtnId + "&saveType=" + saveType);
        String json = "{\"id\" :\"" + mtnId + "\", \"newValue\": \"" + newValue + "\"}";
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
				data.add(mapper.treeToValue(root, SaveMTNCustomerProfileRMO.class)); 		    	
	    		
	    	} catch(IOException e) {
				e.printStackTrace();
	    	} finally {
				return new ResultContainer<>(data, SaveMTNCustomerProfileRMO.class);
	    	}
		}
    }

}
