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
import com.sap.ida.eacp.nucleus.data.client.V3NucleusDataAPI;
import com.sap.ida.eacp.nucleus.data.client.mapper.V1C4sMapper;
import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.request.ResultContainer;
import com.sap.ida.eacp.nucleus.data.client.model.response.V1C4sComponentDTO;
import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.nextgen.vlm.providers.DataProvider;

import io.swagger.annotations.ApiParam;


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
    public V1C4sComponentDTO getData(@ApiParam(example = "cloud_transactions_sales_adrm",
            allowableValues = "cloud_transactions_sales_adrm," +
                    " cloud_transactions_sales_adrm_by_region," +
                    " cloud_transactions_sales_adrm_by_salesbag," +
                    " cloud_transactions_sales_deals," +
                    " software_transactions_sales_adrm," +
                    " software_transactions_sales_adrm_by_region," +
                    " software_transactions_sales_adrm_by_salesbag," +
                    " software_transactions_sales_deals," +
                    " strategic_renewals_analysis," +
                    " strategic_renewals_analysis_totals," +
                    " strategic_renewals_analysis_items," +
                    " strategic_total_escalations," +
                    " cloud_transactions_sales_adrm_s4," +
                    " software_transactions_sales_adrm_s4," +
                    " strategic_escalations_by_process_type," +
                    " strategic_escalations_by_customer"
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
            return new V1C4sComponentDTO();
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


        final V1C4sComponentDTO c4sComponentDTO = V1C4sMapper.fromResultRmo(result.getData(), result.getClz());

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
