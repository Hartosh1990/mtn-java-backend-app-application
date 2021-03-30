package com.sap.nextgen.vlm;

import static com.sap.it.mobile.hcp.hamcrest.HttpMatchers.isOk;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.response.data.ResponseComponentDTO;
import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.nextgen.vlm.rmo.GetMTNSearchResultRMO;
import com.sap.nextgen.vlm.utils.JWTTokenFactory;

public class CorpOverviewApiV1Test extends APITest {

    @Test
    public void testGetCompanySearchResults() {
        mockResponseSequence("/response/TransactionsSalesADRMCloud.json");
        JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    	String token = jwtTokenFactory.getJWTToken("I303399", "hartosh.singh.bugra@sap.com", "Hartosh Singh", "Bugra", "employee");
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("searchTerm", Lists.newArrayList("SAP SE"));
        queryParams.put("jwtToken", Lists.newArrayList(token));
        final DataRequestBody dataRequestBody = new DataRequestBody();
        dataRequestBody.setQueryParams(queryParams);

        final Response response = target("v3/nucleus/data")
                .path("apps/cicorpoverview")
                .path("roles/Display")
                .path("resources")
                .path(DataEndpoint.GET_COMPANY_SEARCH_RESULTS.toString())
                .request()
                .post(Entity.json(dataRequestBody));
        
        assertThat(response, isOk());

        final ResponseComponentDTO c4sComponentDTO = response.readEntity(ResponseComponentDTO.class);

    }
    
    @Test
    public void saveMtnCompanyTest() {
        mockResponseSequence("/response/TransactionsSalesADRMCloud.json");
        JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    	String token = jwtTokenFactory.getJWTToken("I303399", "hartosh.singh.bugra@sap.com", "Hartosh Singh", "Bugra", "employee");
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("mtnId", Lists.newArrayList("null"));
        queryParams.put("ciqId", Lists.newArrayList("IQ126475"));
        queryParams.put("companyName", Lists.newArrayList("SAP SE"));
        queryParams.put("isMtnCompany", Lists.newArrayList("1"));
        queryParams.put("jwtToken", Lists.newArrayList(token));
        final DataRequestBody dataRequestBody = new DataRequestBody();
        dataRequestBody.setQueryParams(queryParams);

        final Response response = target("v3/nucleus/data")
                .path("apps/cicorpoverview")
                .path("roles/Display")
                .path("resources")
                .path(DataEndpoint.SAVE_MTN_COMPANY.toString())
                .request()
                .post(Entity.json(dataRequestBody));
        
        assertThat(response, isOk());

        final ResponseComponentDTO c4sComponentDTO = response.readEntity(ResponseComponentDTO.class);

    	
    }
    
    @Test
    public void testJWTTokenFactory() throws ClientProtocolException, IOException {
    	
    	JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    	String token = jwtTokenFactory.getJWTToken("I303399", "hartosh.singh.bugra@sap.com", "Hartosh Singh", "Bugra", "employee");
        CloseableHttpClient instance = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://vlmdev.cfapps.eu10.hana.ondemand.com/services/getMtnSearchResults?langId=10&pageOffset=0&clientProcessId=20210304140829nlmahf2b65s6&seqNo=8&searchTerm=SAP");
        request.setHeader("Cookie","UserData="+token);
        CloseableHttpResponse response = instance.execute(request);
        assertThat("Token is validated by calling vlm service", response.getStatusLine().getStatusCode() == 200);
    }
    
    @Test
    public void testEcacheLib() throws ExecutionException, ClientProtocolException, IOException {
    	
    	// Get the search result //
    	
    	CacheLoader<String, List<GetMTNSearchResultRMO>> loader = new CacheLoader<String, List<GetMTNSearchResultRMO>>() {
    		   public List<GetMTNSearchResultRMO> load(String key) throws ClientProtocolException, IOException {
    			   JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    		    	String token = jwtTokenFactory.getJWTToken("I303399", "hartosh.singh.bugra@sap.com", "Hartosh Singh", "Bugra", "employee");
    		        CloseableHttpClient instance = HttpClientBuilder.create().build();
    		        HttpGet request = new HttpGet("https://vlmdev.cfapps.eu10.hana.ondemand.com/services/getMtnSearchResults?langId=10&pageOffset=0&clientProcessId=20210304140829nlmahf2b65s6&seqNo=8&searchTerm=SAP");
    		        request.setHeader("Cookie","UserData="+token);
    		        CloseableHttpResponse response = instance.execute(request);
    		        String sResponse = EntityUtils.toString(response.getEntity());
    		        ObjectMapper mapper = new ObjectMapper();
    				JsonNode root = mapper.readTree(sResponse);
    				JsonNode companylist= root.get("results");
    				List<GetMTNSearchResultRMO> list = mapper.readValue(new TreeTraversingParser(companylist),new TypeReference<List<GetMTNSearchResultRMO>>(){});
    				
    			   return list;
    		   }
    		 };
        
    	 LoadingCache<String, List<GetMTNSearchResultRMO>> metadata = CacheBuilder.newBuilder()
    			       .build(loader);
    	 System.out.println(metadata.get("test"));
    	 System.out.println(metadata.get("test"));
    	 System.out.println(metadata.get("test"));
    	 System.out.println(metadata.get("test"));
    	 
    	 
    }
   }
