package com.sap.nextgen.vlm;

import static com.sap.it.mobile.hcp.hamcrest.HttpMatchers.isOk;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.response.data.ResponseComponentDTO;
import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.nextgen.vlm.utils.JWTTokenFactory;
import com.sap.nextgen.vlm.utils.JWTTokenFactory.EmailTemplate;
import com.sap.nextgen.vlm.utils.JWTTokenFactory.Name;

import ch.qos.logback.core.encoder.NonClosableInputStream;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;

public class CorpOverviewApiV1Test extends APITest {

    @Test
    public void testTransactionsSalesADRM() {
        mockResponseSequence("/response/TransactionsSalesADRMCloud.json");
        JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    	String token = jwtTokenFactory.getJWTToken("I303399", "hartosh.singh.bugra@sap.com", "Hartosh Singh", "Bugra", "employee");
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("searchTerm", Lists.newArrayList("SAP SE"));
        queryParams.put("jwtToken", List.of(token));
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
    public void testJWTTokenFactory() throws ClientProtocolException, IOException {
    	
    	JWTTokenFactory jwtTokenFactory = new JWTTokenFactory();
    	String token = jwtTokenFactory.getJWTToken("I303399", "hartosh.singh.bugra@sap.com", "Hartosh Singh", "Bugra", "employee");
        CloseableHttpClient instance = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://vlmdev.cfapps.eu10.hana.ondemand.com/services/getMtnSearchResults?langId=10&pageOffset=0&clientProcessId=20210304140829nlmahf2b65s6&seqNo=8&searchTerm=SAP");
        request.setHeader("Cookie","UserData="+token);
        CloseableHttpResponse response = instance.execute(request);
        assertThat("Token is validated by calling vlm service", response.getStatusLine().getStatusCode() == 200);
    }

   }
