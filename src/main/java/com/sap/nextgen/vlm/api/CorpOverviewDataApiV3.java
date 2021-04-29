package com.sap.nextgen.vlm.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
//import com.sap.ida.eacp.ci.co.togglz.FeatureToggle;
import com.sap.ida.eacp.nucleus.data.client.api.V3NucleusDataAPI;
import com.sap.ida.eacp.nucleus.data.client.mapper.ResponseComponentMapper;
import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.request.ResultContainer;
import com.sap.ida.eacp.nucleus.data.client.model.response.data.C4sComponentLabelDTO;
import com.sap.ida.eacp.nucleus.data.client.model.response.data.FieldOrdersDTO;
import com.sap.ida.eacp.nucleus.data.client.model.response.data.ResponseComponentDTO;
import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.nextgen.vlm.providers.DataProvider;
import com.sap.nextgen.vlm.utils.DBQueryManager;
import com.sap.nextgen.vlm.utils.JWTTokenFactory;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;


@Path("/")
@Produces("application/json")
public class CorpOverviewDataApiV3 implements V3NucleusDataAPI {
	
    @Inject
    private IterableProvider<DataProvider> dataProvider;
    
    @Inject
    private JWTTokenFactory jwtTokenFactory;

    @Context
    SecurityContext securityContext;
    
    private static final Logger LOG = LoggerFactory.getLogger(CorpOverviewDataApiV3.class);
    String type = "employee";

    @SuppressWarnings("finally")
	@Override
    public ResponseComponentDTO getData(@Parameter(example = "cloud_transactions_sales_adrm")@Schema(
            allowableValues = "cloud_transactions_sales_adrm_s4," +
            				  "get_company_search_results," + 
            				  "save_mtn_company," +
                              "mtn_dashboard_data,"+
            				  "get_mtn_company_profile," +
                              "save_mtn_company_profile_info" +
            				  "get_mtn_kpi_catalog,"+
            				  "get_mtn_kpi_metrics,"+
            				  "mtn_trend_analysis_for_kpi,"+
            				  "mtn_trend_analysis_for_company,"+
            				  "mtn_currency_list"
    ) String appId,String role,String resourceId, Boolean useMock,Long variantId, DataRequestBody requestBody) throws Exception {

        try {
        	System.out.println(jwtTokenFactory.getId());
            System.out.println(securityContext.getUserPrincipal());
            AccessToken token = (AccessToken)securityContext.getUserPrincipal();
    		try {
    			if(token == null) {
    				System.out.println("The token is null");
    				
    				if(requestBody.getQueryParams() != null && requestBody.getQueryParams().get("jwtToken") != null) {
    					// Should not throw exception as to test it with jwtToken.
    				}else {
    					throw new BadRequestException("The token is not coming from intwo");
    				}
    			}else {
    				System.out.println("Token Email:" + token.getClaimAsString(TokenClaims.EMAIL) + "Token INumber " + token.getClaimAsString(TokenClaims.USER_NAME) );
    				String jwtToken = jwtTokenFactory.getJWTToken(token.getClaimAsString(TokenClaims.USER_NAME), token.getClaimAsString(TokenClaims.EMAIL), token.getClaimAsString(TokenClaims.GIVEN_NAME), token.getClaimAsString(TokenClaims.FAMILY_NAME), type);	
    				// For each request add token to the request body.
    				if (requestBody.getQueryParams() == null ) {
    					Map<String, List<String>> queryParams = new HashMap<>();
        				queryParams.put("jwtToken", Lists.newArrayList(jwtToken));
        				requestBody.setQueryParams(queryParams);	
    				}else {
    					requestBody.getQueryParams().put("jwtToken", Lists.newArrayList(jwtToken));
    				}
    				
    			}
    		} catch (Exception e) {
    			System.out.println(e.getMessage());
    			LOG.error("Failed to write error response: " + e.getMessage() + ".", e);
    		}
        } catch (Exception e) {
        	System.out.println(e.getMessage());
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

        ResponseComponentDTO c4sComponentDTO = new ResponseComponentDTO();
        try {
        	final ResultContainer result = dataProvider.loadData(requestBody);

            c4sComponentDTO = ResponseComponentMapper.fromResultRmo(result.getData(), result.getClz());
            Map<String, List<String>> queryParams = requestBody.getQueryParams();
            if(DataEndpoint.GET_MTN_COMPANY_PROFILE.toString().equals(resourceId) || DataEndpoint.GET_MTN_PEER_PROFILE.toString().equals(resourceId) ||
            		DataEndpoint.GET_MTN_KPI_METRICS.toString().equals(resourceId)) {
            	if (queryParams.containsKey("denomination")) {
            		double factor = 1;
            		String activeDen = getActiveDenomination(requestBody);
            		if ("1".equals(activeDen)) {
                		factor = 1000;
                	}else if("3".equals(activeDen)){
                		factor = 1000000000;
                	}else {
                		factor = 1000000;
                	}
            		c4sComponentDTO = getModifiedScaleFactorFromDenomination(c4sComponentDTO,factor,null);	
            	}
            		
            }else if(DataEndpoint.MTN_TREND_ANALYSIS_FOR_KPI.toString().equals(resourceId) || DataEndpoint.MTN_TREND_ANALYSIS_FOR_COMPANY.toString().equals(resourceId)) {
            	if(queryParams.containsKey("years") && !queryParams.get("years").isEmpty()) {
            		List<String> years = queryParams.get("years");
            		c4sComponentDTO = getModifiedScaleFactorFromDenomination(c4sComponentDTO, null, years);
            		c4sComponentDTO = getModifiedVisibility(c4sComponentDTO, years);
            	}
            }
            
        }catch(Exception e) {
        	e.printStackTrace();
        }finally {
        	return c4sComponentDTO;
        }
        
    }

	private String getActiveDenomination(DataRequestBody requestBody) throws SQLException {
		 
		String activeDen = requestBody.getQueryParams().get("denomination").get(0);
		
		if(activeDen == null || "null".equals(activeDen) || activeDen.isBlank()) {
			System.out.println("null den is matched");
			String mtnId = requestBody.getQueryParams().get("mtnId").get(0);
			Connection conn =  null;
			String sqlQuery = "select \"T0222_ID\" from \"T0402_MTN_Attributes\" a join \"T0401_MTN\" b on a.\"T0401_AutoID\" = b.\"T0401_AutoID\" where \"T0401_MTNID\" = "+ mtnId
					+" and \"T0401_VersionNo\" = 1";
			ResultSet rs = DBQueryManager.getResultSet(sqlQuery, conn);
			if(rs != null) {
				if(rs.next()) {
					activeDen = rs.getString(1);
				}
				rs.close();
			}
		}else {
			System.out.println("null den but not matched- The value is ");
			System.out.println(activeDen);
		}
		return activeDen;
		
	}

	private ResponseComponentDTO getModifiedScaleFactorFromDenomination(final ResponseComponentDTO c4sComponentDTO, final Double factor, final List<String> years) {
		
        	List<C4sComponentLabelDTO> companyProfileLabels = c4sComponentDTO.getMetadata().getLabels();
        	List<C4sComponentLabelDTO> modifiedLabels = new ArrayList<C4sComponentLabelDTO>();
        	companyProfileLabels.forEach((fieldDTO)-> {
            	if(factor != null && "revenue".equals(fieldDTO.getFieldName()) || "operatingInc".equals(fieldDTO.getFieldName()) || "monetryBenefit".equals(fieldDTO.getFieldName())) {
            		fieldDTO.getValueFormatting().getDisplay().setScaleFactor(factor);
            	}else if(years != null) {
            		switch(fieldDTO.getLabel()) {
            			case "Year1":
            				if(years.size() > 0) {
            					fieldDTO.setLabel(years.get(0));
            				}
            				break;
            			case "Year2":
            				if(years.size() > 1) {
            					fieldDTO.setLabel(years.get(1));
            				}
            				break;
            			case "Year3":
            				if(years.size() > 2) {
            					fieldDTO.setLabel(years.get(2));
            				}
            				break;
            			case "Year4":
            				if(years.size() > 3) {
            					fieldDTO.setLabel(years.get(3));
            				}
            				break;
            			case "Year5":
            				if(years.size() > 4) {
            					fieldDTO.setLabel(years.get(4));
            				}
            				break;
            		} 
            		
            	}
            	//if()
            	modifiedLabels.add(fieldDTO);
            	});
        c4sComponentDTO.getMetadata().setLabels(modifiedLabels);
        return c4sComponentDTO;
	}
	
	private ResponseComponentDTO getModifiedVisibility(final ResponseComponentDTO c4sComponentDTO, final List<String> years) {
		
    	List<FieldOrdersDTO> existinglabelOrders = c4sComponentDTO.getMetadata().getFieldOrders();
    	List<FieldOrdersDTO> modifiedLabelOrders = new ArrayList<FieldOrdersDTO>();
    	existinglabelOrders.forEach((fieldDTO)-> {
        	
        	if(years != null) {
        		switch(fieldDTO.getFieldName()) {
        			case "kpiValue1":
        				if(years.size() < 1) {
        					fieldDTO.setIsVisible(false);
        				}
        				break;
        			case "kpiValue2":
        				if(years.size() < 2) {
        					fieldDTO.setIsVisible(false);
        				}
        				break;
        			case "kpiValue3":
        				if(years.size() < 3) {
        					fieldDTO.setIsVisible(false);
        				}
        				break;
        			case "kpiValue4":
        				if(years.size() < 4) {
        					fieldDTO.setIsVisible(false);
        				}
        				break;
        			case "kpiValue5":
        				if(years.size() < 5) {
        					fieldDTO.setIsVisible(false);
        				}
        				break;
        		} 
        		
        	}
        	//if()
        	modifiedLabelOrders.add(fieldDTO);
        	});
    c4sComponentDTO.getMetadata().setFieldOrders(modifiedLabelOrders);
    return c4sComponentDTO;
}
}
