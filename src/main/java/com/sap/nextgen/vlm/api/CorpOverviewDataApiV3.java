package com.sap.nextgen.vlm.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.hk2.api.IterableProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.sap.cloud.security.token.AccessToken;
import com.sap.cloud.security.token.TokenClaims;
import com.sap.ea.nga.jersey.provider.jackson.ObjectMapperProvider;
//import com.sap.ida.eacp.ci.co.togglz.FeatureToggle;
import com.sap.ida.eacp.nucleus.data.client.api.V3NucleusDataAPI;
import com.sap.ida.eacp.nucleus.data.client.mapper.ResponseComponentMapper;
import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.request.ResultContainer;
import com.sap.ida.eacp.nucleus.data.client.model.response.data.ResponseComponentDTO;
import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.nextgen.vlm.providers.DataProvider;
import com.sap.nextgen.vlm.utils.JWTTokenFactory;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;


@Path("/")
@Produces("application/json")
@PermitAll
public class CorpOverviewDataApiV3 implements V3NucleusDataAPI {
	
    @Inject
    private IterableProvider<DataProvider> dataProvider;
    JWTTokenFactory jwtTokenFactory;

    @Context
    SecurityContext securityContext;
    
    private static final Logger LOG = LoggerFactory.getLogger(CorpOverviewDataApiV3.class);
    String type = "employee";

    @Override
    public ResponseComponentDTO getData(@Parameter(example = "cloud_transactions_sales_adrm")@Schema(
            allowableValues = "cloud_transactions_sales_adrm_s4," +
            				  "get_company_search_results"
    ) String appId,String role,String resourceId, Boolean useMock,Long variantId, DataRequestBody requestBody) throws Exception {

        try {
            
            
            AccessToken token = (AccessToken)securityContext.getUserPrincipal();
    		try {
    			if(token == null) {
    				if(requestBody.getQueryParams() != null && requestBody.getQueryParams().get("jwtToken") != null) {
    					// Should not throw exception as to test it with jwtToken.
    				}else {
    					throw new BadRequestException("The token is not coming from intwo");
    				}
    			}else {
    				String jwtToken = jwtTokenFactory.getJWTToken(token.getClaimAsString(TokenClaims.USER_NAME), token.getClaimAsString(TokenClaims.EMAIL), token.getClaimAsString(TokenClaims.GIVEN_NAME), token.getClaimAsString(TokenClaims.FAMILY_NAME), type);
    				System.out.println("Token Email:" + token.getClaimAsString(TokenClaims.EMAIL) + "Token INumber " + token.getClaimAsString(TokenClaims.USER_NAME) );	
    				// For each request add token to the request body.
    				if (requestBody.getQueryParams() == null ) {
    					Map<String, List<String>> queryParams = new HashMap<>();
        				queryParams.put("jwtToken", List.of(jwtToken));
        				requestBody.setQueryParams(queryParams);	
    				}else {
    					requestBody.getQueryParams().put("jwtToken", List.of(jwtToken));
    				}
    				
    			}
    		} catch (Exception e) {
    			LOG.error("Failed to write error response: " + e.getMessage() + ".", e);
    		}
        } catch (Exception e) {
        	//e.printStackTrace(); // Will be calling logging...
        }

        final DataEndpoint dataEndpoint;
        try {
            dataEndpoint = DataEndpoint.fromString(resourceId);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("The data endpoint " + resourceId + " does not exist");
        }

        final DataProvider dataProvider = Optional.ofNullable(this.dataProvider.named(dataEndpoint.name()).get())
                .orElseThrow(() -> new InternalServerErrorException("There exists no data provider for the data endpoint " + dataEndpoint.name()));

        if (useMock) {
            return new ResponseComponentDTO();
        }


        final ResultContainer result = dataProvider.loadData(requestBody);


        final ResponseComponentDTO c4sComponentDTO = ResponseComponentMapper.fromResultRmo(result.getData(), result.getClz());
        return c4sComponentDTO;
    }
}
