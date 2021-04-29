package com.sap.nextgen.vlm.providers.mtn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.request.ResultContainer;
import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.nextgen.vlm.providers.AbstractProvider;
import com.sap.nextgen.vlm.providers.DataProvider;
import com.sap.nextgen.vlm.rmo.MTNTrendAnalysisKpiRMO;
import com.sap.nextgen.vlm.utils.HttpRequestManager;


public class MTNTrendAnalysisForCompany extends AbstractProvider implements DataProvider<MTNTrendAnalysisKpiRMO>{
	String jwtToken; 
	String mtnId;
	String clientProcessId;
	String langId;
	
	@Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.MTN_TREND_ANALYSIS_FOR_COMPANY;
    }

    @SuppressWarnings("finally")
	@Override
    public ResultContainer<MTNTrendAnalysisKpiRMO> loadData(DataRequestBody requestBody) {
    	Map<String, List<String>> queryParams = Optional.ofNullable(requestBody.getQueryParams()).orElse(new HashMap<>());
    	List<String> gvnYrs = new ArrayList<String>();
    	if (queryParams.containsKey("mtnId")) {
    		mtnId = requestBody.getQueryParams().get("mtnId").get(0);
    	}
    	if (queryParams.containsKey("jwtToken")) {
    		jwtToken = requestBody.getQueryParams().get("jwtToken").get(0);
    		System.out.println(jwtToken);
    	}
    	if (queryParams.containsKey("clientProcessId")) {
    		clientProcessId = requestBody.getQueryParams().get("clientProcessId").get(0);
    	}else {
    		clientProcessId = mtnId+"_intwomtn";
    	}
    	if (queryParams.containsKey("langId")) {
    		langId = requestBody.getQueryParams().get("langId").get(0);
    	}
    	if(queryParams.containsKey("years")) {
    		gvnYrs = queryParams.get("years");
    	}
    	
        final List<MTNTrendAnalysisKpiRMO> data = new ArrayList<MTNTrendAnalysisKpiRMO>();
        	ObjectMapper mapper = new ObjectMapper();
	    	try {
	    		String uri = baseUri+"/services/getTrendAnalysisData?langId=10&clientProcessId="+clientProcessId+"&seqNo=8&mtnId="+mtnId;
	    		
	    		JsonNode root =	HttpRequestManager.getRootObjectFromGetNodeService(jwtToken, uri);
	    		if(root != null && root.get("results")!= null && root.get("results").get(0)!= null) {
	    			JsonNode rootObject = root.get("results").get(0);
	    			JsonNode kpisList = rootObject.get("kpis");
	    			JsonNode allYrsArray = rootObject.get("labels");
	    			
	    			ObjectReader yrsReader = mapper.readerFor(new TypeReference<List<String>>() {});
					List<String> allYrsList = yrsReader.readValue(allYrsArray);
					
					InjectableValues injectableVals = new InjectableValues.Std().addValue("allYrs", allYrsList).addValue("gvnYrs", gvnYrs);
			    	data.addAll(mapper.reader(injectableVals).forType(MTNTrendAnalysisKpiRMO.class).readValue(new TreeTraversingParser(kpisList),new TypeReference<List<MTNTrendAnalysisKpiRMO>>(){}));
	    		}
				/*Reading all yrs from service*/
				
				
	    	} catch(Exception e) {
				e.printStackTrace();
	    	} finally {
				return new ResultContainer<>(data, MTNTrendAnalysisKpiRMO.class);
	    	}
    }	
}
