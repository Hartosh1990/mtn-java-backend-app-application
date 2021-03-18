package com.sap.nextgen.vlm.api;

import java.io.IOException;
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


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;


@Path("/")
@Produces("application/json")
@PermitAll
public class CorpOverviewDataApiV3 implements V3NucleusDataAPI {
	
    @Inject
    private IterableProvider<DataProvider> dataProvider;

    @Context
    SecurityContext securityContext;
    
    private static final Logger LOG = LoggerFactory.getLogger(CorpOverviewDataApiV3.class);

    @Override
    public ResponseComponentDTO getData(@Parameter(example = "cloud_transactions_sales_adrm")@Schema(
            allowableValues = "cloud_transactions_sales_adrm_s4," +
            				  "get_company_search_results"
    ) String appId,String role,String resourceId, Boolean useMock,Long variantId, DataRequestBody requestBody) throws Exception {

        try {
            
            
            AccessToken token = (AccessToken)securityContext.getUserPrincipal();
    		try {
    			System.out.println("Token Email:" + token.getClaimAsString(TokenClaims.EMAIL) + "Token INumber " + token.getClaimAsString(TokenClaims.USER_NAME) );	
    		} catch (Exception e) {
    			LOG.error("Failed to write error response: " + e.getMessage() + ".", e);
    		}
        } catch (Exception e) {
            // ignore
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

        //final Map<String, List<String>> queryParams = requestBody.getQueryParams();
//        if (FeatureToggle.FREEZE_QUARTER.isActive()) {
//            YearQuarter quarter = YearQuarter.now().minusQuarters(1);
//            YearQuarter prevYearQuarter = quarter.minusYears(1);
//            final HashMap<String, List<String>> additionalQueryParams = new HashMap<>();
//            additionalQueryParams.put("quarter", Lists.newArrayList(quarter.toString()));
//            additionalQueryParams.put("prevYearQuarter", Lists.newArrayList(prevYearQuarter.toString()));
//
//            if (Objects.isNull(queryParams)) {
//                requestBody.setQueryParams(additionalQueryParams);
//            } else {
//                queryParams.putAll(additionalQueryParams);
//            }
//        }

        final ResultContainer result = dataProvider.loadData(requestBody);


        final ResponseComponentDTO c4sComponentDTO = ResponseComponentMapper.fromResultRmo(result.getData(), result.getClz());

//        if (queryParams.containsKey("isChart")) {
//            if ("true".equals(queryParams.get("isChart").get(0))) {
//
//                C4SComponentMetaDataChartDTO c4SComponentMetaDataChartDTO = (C4SComponentMetaDataChartDTO) new C4SComponentMetaDataChartDTO()
//                        .setId(c4sComponentDTO.getMetadata().getId())
//                        .setLabels(c4sComponentDTO.getMetadata().getLabels())
//                        .setTitle(c4sComponentDTO.getMetadata().getTitle());
//
//                String chartType = "COLUMN_CHART";
//
//                if (queryParams.containsKey("chartType")) {
//                    chartType = queryParams.get("chartType").get(0);
//                }
//                c4SComponentMetaDataChartDTO.setType(chartType);
//
//                c4sComponentDTO.setMetadata(c4SComponentMetaDataChartDTO);
//            }
//
//
//        }

        return c4sComponentDTO;
    }
}
