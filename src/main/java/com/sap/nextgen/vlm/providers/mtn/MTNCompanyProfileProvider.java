package com.sap.nextgen.vlm.providers.mtn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.sap.nextgen.vlm.rmo.MTNCompanyProfileRMO;

public class MTNCompanyProfileProvider extends AbstractProvider implements DataProvider<MTNCompanyProfileRMO> {
    String mtnId;
    String ciqId;
    String companyName;
    String isMtnCompany;
    String baseUri = "https://vlmdev.cfapps.eu10.hana.ondemand.com";
    String jwtToken; 
    
    @Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.GET_MTN_COMPANY_PROFILE;
    }
    @SuppressWarnings("finally")
	@Override
    public ResultContainer<MTNCompanyProfileRMO> loadData(DataRequestBody requestBody) {

    	Map<String, List<String>> queryParams = Optional.ofNullable(requestBody.getQueryParams()).orElse(new HashMap<>());
    	if (queryParams.containsKey("mtnId")) {
    		mtnId = requestBody.getQueryParams().get("mtnId").get(0);
    	}
    	if (queryParams.containsKey("jwtToken")) {
    		jwtToken = requestBody.getQueryParams().get("jwtToken").get(0);
    	}

        final List<MTNCompanyProfileRMO> data = new ArrayList<MTNCompanyProfileRMO>();
        
        CloseableHttpClient httpclient = HttpClients.createDefault();

        
    	HttpGet get = new HttpGet(baseUri +"/services/getMtnCompany?langId=10&clientProcessId=20210304140829nlmahf2b65s6&seqNo=8&mtnId="+mtnId);
		get.setHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
		get.setHeader("Cookie", "UserData="+jwtToken);
	    CloseableHttpResponse httpResponse;
		try {
			httpResponse = httpclient.execute(get);
			String response = EntityUtils.toString(httpResponse.getEntity());
		    ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response); 
			JsonNode companyProfileObject = root.get("results");
			data.add(mapper.treeToValue(companyProfileObject, MTNCompanyProfileRMO.class)); 		    	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return new ResultContainer<>(data, MTNCompanyProfileRMO.class);
		}		
		
        
		
    	
    }
    
}
