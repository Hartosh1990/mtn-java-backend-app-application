package com.sap.nextgen.vlm.providers.mtn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.request.ResultContainer;
import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.nextgen.vlm.providers.AbstractProvider;
import com.sap.nextgen.vlm.providers.DataProvider;
import com.sap.nextgen.vlm.rmo.MTNKpiMetricsRMO;
import com.sap.nextgen.vlm.utils.HttpRequestManager;
import com.sap.nextgen.vlm.utils.MTNFlags;


public class MTNKpiMetricsDataProvider extends AbstractProvider implements DataProvider<MTNKpiMetricsRMO> {
    String mtnId;
    String clientProcessId;
    String kpiId = "null";
    int langId = 10;
    String jwtToken; 
    
    @Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.GET_MTN_KPI_METRICS;
    }

    @SuppressWarnings("finally")
	@Override
    public ResultContainer<MTNKpiMetricsRMO> loadData(DataRequestBody requestBody) {

    	Map<String, List<String>> queryParams = Optional.ofNullable(requestBody.getQueryParams()).orElse(new HashMap<>());
    	if (queryParams.containsKey("mtnId")) {
    		mtnId = requestBody.getQueryParams().get("mtnId").get(0);
    	}
    	if (queryParams.containsKey("clientProcessId")) {
    		clientProcessId = requestBody.getQueryParams().get("clientProcessId").get(0);
    	}else {
    		clientProcessId = mtnId+"kpiMetrics+_intwomtn";
    	}
    	if (queryParams.containsKey("langId")) {
    		langId = Integer.parseInt(requestBody.getQueryParams().get("langId").get(0));
    	}
    	if (queryParams.containsKey("kpiId")) {
    		kpiId = requestBody.getQueryParams().get("kpiId").get(0);
    	}
    	if (queryParams.containsKey("jwtToken")) {
    		jwtToken = requestBody.getQueryParams().get("jwtToken").get(0);
    		System.out.println(jwtToken);
    	}

        final List<MTNKpiMetricsRMO> data = new ArrayList<MTNKpiMetricsRMO>();
      	try {
      		Integer isQuestionDataAvailable = new MTNFlags(Integer.parseInt(mtnId)).isQuesDataAvailable;
      		System.out.println(isQuestionDataAvailable);
      		String uri = baseUri +"/services/getMtnMetricsKpis?langId="+langId+"&clientProcessId="+clientProcessId+"&seqNo=0&mtnId="+mtnId+"&isQuestionDataAvailable="+isQuestionDataAvailable+"&kpiId="+kpiId;
            JsonNode root = HttpRequestManager.getRootObjectFromGetNodeService(jwtToken, uri); 
    		JsonNode kpiList= root.get("results").get(0).get("kpiInfo");
    		System.out.println(kpiList);
    		ObjectMapper mapper = new ObjectMapper();
        	data.addAll(mapper.readValue(new TreeTraversingParser(kpiList),new TypeReference<List<MTNKpiMetricsRMO>>(){}));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return new ResultContainer<>(data, MTNKpiMetricsRMO.class);
		}
    }

}
