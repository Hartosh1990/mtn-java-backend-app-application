package com.sap.nextgen.vlm.providers.mtn;

import java.io.IOException;
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
import com.sap.nextgen.vlm.rmo.MTNTrendAnalysisCompanyRMO;
import com.sap.nextgen.vlm.utils.HttpRequestManager;


public class MTNTrendAnalysisForKPI extends AbstractProvider implements DataProvider<MTNTrendAnalysisCompanyRMO>{
	String jwtToken; 
	String mtnId;
	String clientProcessId;
	String langId;
	
	@Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.MTN_TREND_ANALYSIS_FOR_KPI;
    }

    @SuppressWarnings("finally")
	@Override
    public ResultContainer<MTNTrendAnalysisCompanyRMO> loadData(DataRequestBody requestBody) {
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
    	
        final List<MTNTrendAnalysisCompanyRMO> data = new ArrayList<MTNTrendAnalysisCompanyRMO>();
        	ObjectMapper mapper = new ObjectMapper();
	    	try {
	    		String uri = baseUri+"/services/getTrendAnalysisData?langId=10&clientProcessId="+clientProcessId+"&seqNo=8&mtnId="+mtnId;
	    		if(queryParams.containsKey("kpiId") && queryParams.get("kpiId").get(0)!= null && !"null".equals(queryParams.get("kpiId").get(0))) {
	    			uri = uri+"&kpiId="+queryParams.get("kpiId").get(0);
	        	}else {
	        		throw new IllegalArgumentException("KPI Id is must");
	        	}
	    		JsonNode root =	HttpRequestManager.getRootObjectFromGetNodeService(jwtToken, uri);
	    		if(root != null && root.get("results")!= null && root.get("results").get(0)!= null) {
	    			JsonNode rootObject = root.get("results").get(0);
	    			JsonNode compList = rootObject.get("companies");
	    			JsonNode kpisArray = rootObject.get("kpis");
	    			JsonNode allYrsArray = rootObject.get("labels");
	    			
	    			ObjectReader yrsReader = mapper.readerFor(new TypeReference<List<String>>() {});
					List<String> allYrsList = yrsReader.readValue(allYrsArray);
					
					
					/*----------------*/
					ObjectReader objReader = mapper.readerFor(new TypeReference<List<Double>>() {});
					Map<Integer,List<Double>> companysKPIValue = new HashMap<Integer, List<Double>>();
					if(kpisArray != null && !kpisArray.isEmpty() && kpisArray.get(0).get("kpiData") != null && !kpisArray.get(0).get("kpiData").isEmpty()) {
						kpisArray.get(0).get("kpiData").forEach((company)->{
							try {
								companysKPIValue.put(company.get("id").asInt(),objReader.readValue(company.get("data")));
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
					}
					InjectableValues injectableVals = new InjectableValues.Std().addValue(Map.class,companysKPIValue).addValue("allYrs", allYrsList).addValue("gvnYrs", gvnYrs);
					System.out.println(compList);
			    	data.addAll(mapper.reader(injectableVals).forType(MTNTrendAnalysisCompanyRMO.class).readValue(new TreeTraversingParser(compList),new TypeReference<List<MTNTrendAnalysisCompanyRMO>>(){}));
	    		}
				/*Reading all yrs from service*/
				
				
	    	} catch(Exception e) {
				e.printStackTrace();
	    	} finally {
				return new ResultContainer<>(data, MTNTrendAnalysisCompanyRMO.class);
	    	}
    }	
}
