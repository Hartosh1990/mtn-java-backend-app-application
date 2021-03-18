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


public class GetMTNSearchResultsProvider extends AbstractProvider implements DataProvider<GetMTNSearchResultRMO> {
    String searchTerm;
    String baseUri = "https://vlmdev.cfapps.eu10.hana.ondemand.com";
    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjQ4ODAxLCJmaXJzdE5hbWUiOiJNYXJpYW4iLCJsYXN0TmFtZSI6IkluZm8iLCJlbWFpbCI6ImluZm9AZXN0aW1hdGUuc2siLCJsYW5nSWQiOjEwLCJsYW5nTmFtZSI6IkVuZ2xpc2giLCJjb21wYW55TmFtZSI6ImVzdGltIiwidXNlclR5cGUiOiJHZW5lcmFsIFVzZXIiLCJ1c2VyQ2F0ZWdvcnkiOjIsImlzV2hhdHNOZXdBdmFpbGFibGUiOjEsImlhdCI6MTYxNjA0NTIxMywiZXhwIjoxNjE2OTA5MjEzfQ.Ug7pA_VZB69OhPJx9gzi1uq83F4-FxCgKL11JsdDB38";
    @Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.GET_COMPANY_SEARCH_RESULTS;
    }

    @SuppressWarnings("finally")
	@Override
    public ResultContainer<GetMTNSearchResultRMO> loadData(DataRequestBody requestBody) {

    	Map<String, List<String>> queryParams = Optional.ofNullable(requestBody.getQueryParams()).orElse(new HashMap<>());
    	if (queryParams.containsKey("searchTerm")) {
    		searchTerm = requestBody.getQueryParams().get("searchTerm").get(0);
    	}

        final List<GetMTNSearchResultRMO> data = new ArrayList<GetMTNSearchResultRMO>();
        
        CloseableHttpClient httpclient = HttpClients.createDefault();
    	
    	HttpGet get = new HttpGet(baseUri +"/services/getMtnSearchResults?langId=10&pageOffset=0&clientProcessId=20210304140829nlmahf2b65s6&seqNo=8&searchTerm="+searchTerm);
    	get.setHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
    	get.setHeader("x-auth-token", token);
    	
    	CloseableHttpResponse httpResponse;
		try {
			httpResponse = httpclient.execute(get);
			String response = EntityUtils.toString(httpResponse.getEntity());
	    	ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response);
			JsonNode companylist= root.get("results");
	    	data.addAll(mapper.readValue(new TreeTraversingParser(companylist),new TypeReference<List<GetMTNSearchResultRMO>>(){}));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return new ResultContainer<>(data, GetMTNSearchResultRMO.class);
		}
		
    	
    }


//    private Double calculateGrowth(Double d1, Double d2) {
//        return div(diff(d1, d2), d2);
//    }
}
