package com.sap.nextgen.vlm.providers.mtn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    String jwtToken; 
    
    @Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.GET_COMPANY_SEARCH_RESULTS;
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
    @SuppressWarnings("finally")
	@Override
    public ResultContainer<GetMTNSearchResultRMO> loadData(DataRequestBody requestBody) {

    	Map<String, List<String>> queryParams = Optional.ofNullable(requestBody.getQueryParams()).orElse(new HashMap<>());
    	if (queryParams.containsKey("searchTerm")) {
    		searchTerm = requestBody.getQueryParams().get("searchTerm").get(0);
    	}
    	if (queryParams.containsKey("jwtToken")) {
    		jwtToken = requestBody.getQueryParams().get("jwtToken").get(0);
    		System.out.println(jwtToken);
    	}

        final List<GetMTNSearchResultRMO> data = new ArrayList<GetMTNSearchResultRMO>();
        
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String encodedSearchTerm = encodeValue(searchTerm); // Encoding a query string
        
    	HttpGet get = new HttpGet(baseUri +"/services/getMtnSearchResults?langId=10&pageOffset=0&clientProcessId=20210304140829nlmahf2b65s6&seqNo=8&searchTerm="+encodedSearchTerm);
    	get.setHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
    	get.setHeader("Cookie", "UserData="+jwtToken);
    	
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

}
