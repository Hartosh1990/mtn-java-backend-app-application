package com.sap.nextgen.vlm.api;

import java.io.IOException;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.sap.cloud.security.token.AccessToken;
import com.sap.cloud.security.token.TokenClaims;
import com.sap.nextgen.vlm.utils.HttpRequestManager;
import com.sap.nextgen.vlm.utils.JWTTokenFactory;



@Path("/v3/nucleus/data/apps/mtn/roles/role3/resources/getMtnAnalysis")
@Produces("application/json")
public class MTNAnalysisAPI {
	@Context
	    SecurityContext securityContext;
    @Inject
    private JWTTokenFactory jwtTokenFactory;

    private static final Logger LOG = LoggerFactory.getLogger(MTNAnalysisAPI.class);
    String type = "employee";
    String baseUri = "https://vlmdev.cfapps.eu10.hana.ondemand.com";
    int langId = 10;
    String jwtToken = "";
    @GET
	@Produces(MediaType.APPLICATION_JSON)
	public String message(@Context UriInfo uriInfo) throws JsonMappingException, JsonProcessingException, ClientProtocolException, IOException, SQLException {		

		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters(); 
		String results = "";
        try {
            AccessToken token = (AccessToken)securityContext.getUserPrincipal();
    		try {
    			if(token == null) {
    				System.out.println("The token is null");
    				
    				if(queryParams != null && queryParams.getFirst("jwtToken") != null) {
    					// Should not throw exception as to test it with jwtToken.
    					jwtToken = queryParams.getFirst("jwtToken");
    				}else {
    					throw new BadRequestException("The token is not coming from intwo");
    				}
    			} else {
    				System.out.println("Token Email:" + token.getClaimAsString(TokenClaims.EMAIL) + "Token INumber " + token.getClaimAsString(TokenClaims.USER_NAME) );
    				jwtToken = jwtTokenFactory.getJWTToken(token.getClaimAsString(TokenClaims.USER_NAME), token.getClaimAsString(TokenClaims.EMAIL), token.getClaimAsString(TokenClaims.GIVEN_NAME), token.getClaimAsString(TokenClaims.FAMILY_NAME), type);	
    				// For each request add token to the request body.    				
    			}
    		} catch (Exception e) {
    			System.out.println(e.getMessage());
    			LOG.error("Failed to write error response: " + e.getMessage() + ".", e);
    		}
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        	//e.printStackTrace(); // Will be calling logging...
        }
        String mtnId = queryParams.getFirst("mtnId");
        String clientProcessId = queryParams.getFirst("clientProcessId");  		        
    	if (queryParams.containsKey("langId")) {
    		langId = Integer.parseInt(queryParams.getFirst("langId"));
    	}
    	String langId = queryParams.getFirst("langId");

	    String uri = baseUri +"/services/getMtnAnalysisData?langId="+langId+"&clientProcessId="+clientProcessId+"&seqNo=0";
        JsonNode root = HttpRequestManager.getRootObjectFromGetNodeService(jwtToken, uri); 
        results = root.toString();
		return results;		

	}
}
