package com.sap.nextgen.vlm;

import com.google.common.collect.Lists;
import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.response.V1C4sComponentDTO;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sap.it.mobile.hcp.hamcrest.HttpMatchers.isOk;
import static org.hamcrest.MatcherAssert.assertThat;

public class CorpOverviewApiV1Test extends APITest {

    //@Test
    public void testTransactionsSalesADRM() {
        mockResponseSequence("/response/TransactionsSalesADRMCloud.json");

        Map<String, List<String>> queryParams = new HashMap<>();
        //queryParams.put("isChart", Lists.newArrayList("true"));

        final DataRequestBody dataRequestBody = new DataRequestBody();
        dataRequestBody.setQueryParams(queryParams);

        final Response response = target("v3/nucleus/data")
                .path("apps/cicorpoverview")
                .path("roles/Display")
                .path("resources")
                .path(DataEndpoint.CLOUD_TRANSACTIONS_SALES_ADRM_S4.toString())
                .request()
                .post(Entity.json(null));
        
        assertThat(response, isOk());

        final V1C4sComponentDTO c4sComponentDTO = response.readEntity(V1C4sComponentDTO.class);

    }

   }
