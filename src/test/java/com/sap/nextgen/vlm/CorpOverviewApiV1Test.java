package com.sap.nextgen.vlm;

import static com.sap.it.mobile.hcp.hamcrest.HttpMatchers.isOk;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.response.data.ResponseComponentDTO;
import com.sap.nextgen.vlm.constants.DataEndpoint;

public class CorpOverviewApiV1Test extends APITest {

    @Test
    public void testTransactionsSalesADRM() {
        mockResponseSequence("/response/TransactionsSalesADRMCloud.json");

        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("searchTerm", Lists.newArrayList("SAP SE"));

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

   }
