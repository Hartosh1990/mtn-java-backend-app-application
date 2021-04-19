package com.sap.nextgen.vlm;

import static com.sap.it.mobile.hcp.hamcrest.HttpMatchers.isOk;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
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
import com.sap.nextgen.vlm.constants.VlmConstants;
import com.sap.nextgen.vlm.rmo.GetMTNSearchResultRMO;
import com.sap.nextgen.vlm.rmo.MasterDataGenericRMO;
import com.sap.nextgen.vlm.utils.CacheManager;
import com.sap.nextgen.vlm.utils.JWTTokenFactory;
import com.sap.nextgen.vlm.utils.MasterPackageKey;

public class CorpOverviewApiV1Test extends APITest {

    @Test
    public void testGetCompanySearchResults() {
        mockResponseSequence("/response/TransactionsSalesADRMCloud.json");
        JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    	String token = jwtTokenFactory.getJWTToken("I314224", "budh.ram@sap.com", "Budh", "Ram", "employee");
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
    public void testSaveMtnCompanyProfileTestForPeriodFY() {
        mockResponseSequence("/response/TransactionsSalesADRMCloud.json");
        JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    	String token = jwtTokenFactory.getJWTToken("I303399", "hartosh.singh.bugra@sap.com", "Hartosh Singh", "Bugra", "employee");
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("mtnId", Lists.newArrayList("2443"));
        queryParams.put("saveType", Lists.newArrayList("3"));
        queryParams.put("newValue", Lists.newArrayList("0"));
        queryParams.put("jwtToken", Lists.newArrayList(token));
        final DataRequestBody dataRequestBody = new DataRequestBody();
        dataRequestBody.setQueryParams(queryParams);

        final Response response = target("v3/nucleus/data")
                .path("apps/cicorpoverview")
                .path("roles/Display")
                .path("resources")
                .path(DataEndpoint.SAVE_MTN_COMPANY_PROFILE_INFO.toString())
                .request()
                .post(Entity.json(dataRequestBody));
        
        assertThat(response, isOk());

        final ResponseComponentDTO c4sComponentDTO = response.readEntity(ResponseComponentDTO.class);
    }    

    @Test
    public void testSaveMtnCompanyProfileTestForPeriodTTM() {
        mockResponseSequence("/response/TransactionsSalesADRMCloud.json");
        JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    	String token = jwtTokenFactory.getJWTToken("I303399", "hartosh.singh.bugra@sap.com", "Hartosh Singh", "Bugra", "employee");
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("mtnId", Lists.newArrayList("2443"));
        queryParams.put("saveType", Lists.newArrayList("3"));
        queryParams.put("newValue", Lists.newArrayList("1"));
        queryParams.put("jwtToken", Lists.newArrayList(token));
        final DataRequestBody dataRequestBody = new DataRequestBody();
        dataRequestBody.setQueryParams(queryParams);

        final Response response = target("v3/nucleus/data")
                .path("apps/cicorpoverview")
                .path("roles/Display")
                .path("resources")
                .path(DataEndpoint.SAVE_MTN_COMPANY_PROFILE_INFO.toString())
                .request()
                .post(Entity.json(dataRequestBody));
        
        assertThat(response, isOk());

        final ResponseComponentDTO c4sComponentDTO = response.readEntity(ResponseComponentDTO.class);
    }    

    @Test
    public void testSaveMtnCompanyProfileTestForDenominationThousands() {
        mockResponseSequence("/response/TransactionsSalesADRMCloud.json");
        JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    	String token = jwtTokenFactory.getJWTToken("I303399", "hartosh.singh.bugra@sap.com", "Hartosh Singh", "Bugra", "employee");
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("mtnId", Lists.newArrayList("2443"));
        queryParams.put("saveType", Lists.newArrayList("4"));
        queryParams.put("newValue", Lists.newArrayList("1"));
        queryParams.put("jwtToken", Lists.newArrayList(token));
        final DataRequestBody dataRequestBody = new DataRequestBody();
        dataRequestBody.setQueryParams(queryParams);

        final Response response = target("v3/nucleus/data")
                .path("apps/cicorpoverview")
                .path("roles/Display")
                .path("resources")
                .path(DataEndpoint.SAVE_MTN_COMPANY_PROFILE_INFO.toString())
                .request()
                .post(Entity.json(dataRequestBody));
        
        assertThat(response, isOk());

        final ResponseComponentDTO c4sComponentDTO = response.readEntity(ResponseComponentDTO.class);
    }    

    @Test
    public void testSaveMtnCompanyProfileTestForDenominationMillions() {
        mockResponseSequence("/response/TransactionsSalesADRMCloud.json");
        JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    	String token = jwtTokenFactory.getJWTToken("I303399", "hartosh.singh.bugra@sap.com", "Hartosh Singh", "Bugra", "employee");
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("mtnId", Lists.newArrayList("2443"));
        queryParams.put("saveType", Lists.newArrayList("4"));
        queryParams.put("newValue", Lists.newArrayList("2"));
        queryParams.put("jwtToken", Lists.newArrayList(token));
        final DataRequestBody dataRequestBody = new DataRequestBody();
        dataRequestBody.setQueryParams(queryParams);

        final Response response = target("v3/nucleus/data")
                .path("apps/cicorpoverview")
                .path("roles/Display")
                .path("resources")
                .path(DataEndpoint.SAVE_MTN_COMPANY_PROFILE_INFO.toString())
                .request()
                .post(Entity.json(dataRequestBody));
        
        assertThat(response, isOk());

        final ResponseComponentDTO c4sComponentDTO = response.readEntity(ResponseComponentDTO.class);
    }    
    @Test
    public void testSaveMtnCompanyProfileTestForDenominationBillions() {
        mockResponseSequence("/response/TransactionsSalesADRMCloud.json");
        JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    	String token = jwtTokenFactory.getJWTToken("I303399", "hartosh.singh.bugra@sap.com", "Hartosh Singh", "Bugra", "employee");
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("mtnId", Lists.newArrayList("2443"));
        queryParams.put("saveType", Lists.newArrayList("4"));
        queryParams.put("newValue", Lists.newArrayList("3"));
        queryParams.put("jwtToken", Lists.newArrayList(token));
        final DataRequestBody dataRequestBody = new DataRequestBody();
        dataRequestBody.setQueryParams(queryParams);

        final Response response = target("v3/nucleus/data")
                .path("apps/cicorpoverview")
                .path("roles/Display")
                .path("resources")
                .path(DataEndpoint.SAVE_MTN_COMPANY_PROFILE_INFO.toString())
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
    public void testGoogleCacaheLibraray() throws ExecutionException, ClientProtocolException, IOException {
    	
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
    	 metadata.get("test");metadata.get("test");metadata.get("test");metadata.get("test");
    	 assertTrue("The data is being faetched only for one time" ,metadata.size() == 1);
    	 
    }
    
    @Test
    public void testCacheManager() throws ExecutionException, ClientProtocolException, IOException {
    	
    	 CacheManager<Object,Object> cm = new CacheManager<Object,Object>();
    	 MasterPackageKey currencyMpk = new MasterPackageKey();
    	 currencyMpk.setLangId(10);
    	 currencyMpk.setListName("CurrencyData");
    	 currencyMpk.setPackageId(30);
    	 currencyMpk.setPackVer(0);
    	 cm.getCachedObjects(currencyMpk);cm.getCachedObjects(currencyMpk);cm.getCachedObjects(currencyMpk);cm.getCachedObjects(currencyMpk);
    	 cm.getCachedObjects(VlmConstants.bcIndustry.toString());cm.getCachedObjects(VlmConstants.bcIndustry.toString());
    	 //System.out.println(cm.getCacheHitCount());
    	 assertThat("cache should be hit 4 times as there total 6 calls with 2 keys so 6-2=4",cm.getCacheHitCount() == 4);
    	 assertThat("US Dollar Currency is coming at index 0",((Map<String,MasterDataGenericRMO>)cm.getCachedObjects(currencyMpk)).containsKey("137"));
    	 assertThat("cache should be hit 5 times as there total 7 calls with 2 keys so 7-2=5",cm.getCacheHitCount() == 5);
    }
    
    @Test
 public void testMTNDashboard() {
        mockResponseSequence("/response/TransactionsSalesADRMCloud.json");
        JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    	String token = jwtTokenFactory.getJWTToken("I314224", "budh.ram@sap.com", "Budh", "Ram", "employee");
        Map<String, List<String>> queryParams = new HashMap<>();
        
        queryParams.put("jwtToken", Lists.newArrayList(token));
        final DataRequestBody dataRequestBody = new DataRequestBody();
        dataRequestBody.setQueryParams(queryParams);

        final Response response = target("v3/nucleus/data")
                .path("apps/cicorpoverview")
                .path("roles/Display")
                .path("resources")
                .path(DataEndpoint.MTN_DASHBOARD_DATA.toString())
                .request()
                .post(Entity.json(dataRequestBody));
        
        assertThat(response, isOk());

        final ResponseComponentDTO c4sComponentDTO = response.readEntity(ResponseComponentDTO.class);

    }
    @Test
    public void testGetMTNCompanyProfile() {
        mockResponseSequence("/response/TransactionsSalesADRMCloud.json");
        JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    	String token = jwtTokenFactory.getJWTToken("I303399", "hartosh.singh.bugra@sap.com", "Hartosh Singh", "Bugra", "employee");
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("mtnId", Lists.newArrayList("2602"));
        queryParams.put("ciqId", Lists.newArrayList("IQ126475"));
        queryParams.put("jwtToken", Lists.newArrayList(token));
        final DataRequestBody dataRequestBody = new DataRequestBody();
        dataRequestBody.setQueryParams(queryParams);

        final Response response = target("v3/nucleus/data")
                .path("apps/cicorpoverview")
                .path("roles/Display")
                .path("resources")
                .path(DataEndpoint.GET_MTN_COMPANY_PROFILE.toString())
                .request()
                .post(Entity.json(dataRequestBody));
        
        assertThat(response, isOk());

        final ResponseComponentDTO c4sComponentDTO = response.readEntity(ResponseComponentDTO.class);

    }
    
    @Test
    public void testGetMTNPeerProfile() {
        mockResponseSequence("/response/TransactionsSalesADRMCloud.json");
        JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    	String token = jwtTokenFactory.getJWTToken("I314224", "budh.ram@sap.com", "Budh", "Ram", "employee");
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("mtnId", Lists.newArrayList("2465"));
        queryParams.put("ciqId", Lists.newArrayList("IQ704634"));
        queryParams.put("jwtToken", Lists.newArrayList(token));
        final DataRequestBody dataRequestBody = new DataRequestBody();
        dataRequestBody.setQueryParams(queryParams);

        final Response response = target("v3/nucleus/data")
                .path("apps/cicorpoverview")
                .path("roles/Display")
                .path("resources")
                .path(DataEndpoint.GET_MTN_PEER_PROFILE.toString())
                .request()
                .post(Entity.json(dataRequestBody));
        
        assertThat(response, isOk());

        final ResponseComponentDTO c4sComponentDTO = response.readEntity(ResponseComponentDTO.class);

    }

    @Test
    public void testGetMTNKPICatalog() {
        mockResponseSequence("/response/TransactionsSalesADRMCloud.json");
        JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    	String token = jwtTokenFactory.getJWTToken("I303373", "akhilesh.jain@sap.com", "Akhilesh", "Jain", "employee");
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("mtnId", Lists.newArrayList("1864"));
        queryParams.put("jwtToken", Lists.newArrayList(token));
        final DataRequestBody dataRequestBody = new DataRequestBody();
        dataRequestBody.setQueryParams(queryParams);

        final Response response = target("v3/nucleus/data")
                .path("apps/cicorpoverview")
                .path("roles/Display")
                .path("resources")
                .path(DataEndpoint.GET_MTN_KPI_CATALOG.toString())
                .request()
                .post(Entity.json(dataRequestBody));
        
        assertThat(response, isOk());

        final ResponseComponentDTO c4sComponentDTO = response.readEntity(ResponseComponentDTO.class);

    }
    
    @Test
    public void testMTNTrendAnalysisYears() {
        mockResponseSequence("/response/TransactionsSalesADRMCloud.json");
        JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    	String token = jwtTokenFactory.getJWTToken("I314224", "budh.ram@sap.com", "Budh", "Ram", "employee");
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("mtnId", Lists.newArrayList("2464"));
        queryParams.put("ciqId", Lists.newArrayList("IQ704634"));
        queryParams.put("jwtToken", Lists.newArrayList(token));
        final DataRequestBody dataRequestBody = new DataRequestBody();
        dataRequestBody.setQueryParams(queryParams);

        final Response response = target("v3/nucleus/data")
                .path("apps/cicorpoverview")
                .path("roles/Display")
                .path("resources")
                .path(DataEndpoint.GET_MTN_TREND_ANALYSIS_YEARS.toString())
                .request()
                .post(Entity.json(dataRequestBody));
        
        assertThat(response, isOk());

        final ResponseComponentDTO c4sComponentDTO = response.readEntity(ResponseComponentDTO.class);

    }
    
    @Test
    public void testGetMTNKpiMetrics() {
        JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    	String token = jwtTokenFactory.getJWTToken("I303399", "hartosh.singh.bugra@sap.com", "Hartosh Singh", "Bugra", "employee");
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("mtnId", Lists.newArrayList("2556"));
        queryParams.put("jwtToken", Lists.newArrayList(token));
        queryParams.put("clientProcessId", Lists.newArrayList("testFromTestCases"));
        final DataRequestBody dataRequestBody = new DataRequestBody();
        dataRequestBody.setQueryParams(queryParams);

        final Response response = target("v3/nucleus/data")
                .path("apps/cicorpoverview")
                .path("roles/Display")
                .path("resources")
                .path(DataEndpoint.GET_MTN_KPI_METRICS.toString())
                .request()
                .post(Entity.json(dataRequestBody));
        
        assertThat(response, isOk());

        final ResponseComponentDTO c4sComponentDTO = response.readEntity(ResponseComponentDTO.class);

    }
    
    
   }
