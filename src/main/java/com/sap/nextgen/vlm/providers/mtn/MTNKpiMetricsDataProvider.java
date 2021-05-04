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
    List<String> akpiId = null;
    List<String> dkpiId = null;
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
    	if (queryParams.containsKey("kpiIdtobeAdded") && !queryParams.get("kpiIdtobeAdded").isEmpty() && queryParams.get("kpiIdtobeAdded").get(0) != null) {
    		akpiId = queryParams.get("kpiIdtobeAdded");
    	}
    	if (queryParams.containsKey("kpiIdtobeDeleted") && !queryParams.get("kpiIdtobeDeleted").isEmpty() && queryParams.get("kpiIdtobeDeleted").get(0) != null) {
    		dkpiId = queryParams.get("kpiIdtobeDeleted");
    	}
    	if (queryParams.containsKey("jwtToken")) {
    		jwtToken = requestBody.getQueryParams().get("jwtToken").get(0);
    		System.out.println(jwtToken);
    	}

        final List<MTNKpiMetricsRMO> data = new ArrayList<MTNKpiMetricsRMO>();
        ResultContainer<MTNKpiMetricsRMO> finalResultCont = new ResultContainer<>(data, MTNKpiMetricsRMO.class);
      	try {
      		Integer isQuestionDataAvailable = new MTNFlags(Integer.parseInt(mtnId)).isQuesDataAvailable;
      		System.out.println(isQuestionDataAvailable);
      		String uri = baseUri +"/services/getMtnMetricsKpis?langId="+langId+"&clientProcessId="+clientProcessId+"&seqNo=0&mtnId="+mtnId+"&isQuestionDataAvailable="+isQuestionDataAvailable; 
      		if(akpiId != null) {
      			akpiId.forEach((kpiId)->{
      				String addUri = baseUri +"/services/getMtnMetricsKpis?langId="+langId+"&clientProcessId="+clientProcessId+"&seqNo=0&mtnId="+mtnId+"&isQuestionDataAvailable="+isQuestionDataAvailable;
      				addUri = uri+"&kpiId="+kpiId;
      				try {
						HttpRequestManager.callGetNodeService(jwtToken, addUri);
					} catch (Exception e) {
						e.printStackTrace();
					}
      			});
      			
      		}
      		if(dkpiId!= null) {
      			dkpiId.forEach((kpiId)->{
      				String duri = baseUri+"/services/deleteMtnKpi?mtnId="+mtnId+"&kpiId="+kpiId+"&langId="+langId+"&clientProcessId="+clientProcessId+"&seqNo=0";
					try {
						JsonNode dRoot = HttpRequestManager.getRootObjectFromGetNodeService(jwtToken, duri);
						if(dRoot != null && dRoot.get("success")!= null) {
	          				if(dRoot.get("success").asBoolean()) {
	          					System.out.println("The KPI with id "+dkpiId + " is deleted successfully");
	          				};
	          			}
					} catch (Exception e) {
						e.printStackTrace();
					}
          				
      			});
      			
      		}
            JsonNode root = HttpRequestManager.getRootObjectFromGetNodeService(jwtToken, uri); 
            JsonNode kpiList= root.get("results").get(0).get("kpiInfo");
    		System.out.println(kpiList);
    		ObjectMapper mapper = new ObjectMapper();
        	data.addAll(mapper.readValue(new TreeTraversingParser(kpiList),new TypeReference<List<MTNKpiMetricsRMO>>(){}));
        	finalResultCont = new ResultContainer<>(data, MTNKpiMetricsRMO.class);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {			
			return finalResultCont;
		}
    }

}
