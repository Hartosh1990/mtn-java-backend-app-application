package com.sap.nextgen.vlm.providers.mtn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.request.ResultContainer;
import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.nextgen.vlm.providers.AbstractProvider;
import com.sap.nextgen.vlm.providers.DataProvider;
import com.sap.nextgen.vlm.rmo.GetMTNKPICatalogRMO;


public class GetMTNKPICatalogProvider extends AbstractProvider implements DataProvider<GetMTNKPICatalogRMO>{
	String jwtToken; 
	String mtnId;
	
	@Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.GET_MTN_KPI_CATALOG;
    }

    @SuppressWarnings("finally")
	@Override
    public ResultContainer<GetMTNKPICatalogRMO> loadData(DataRequestBody requestBody) {
    	Map<String, List<String>> queryParams = Optional.ofNullable(requestBody.getQueryParams()).orElse(new HashMap<>());
    	if (queryParams.containsKey("mtnId")) {
    		mtnId = requestBody.getQueryParams().get("mtnId").get(0);
    	}
    	if (queryParams.containsKey("jwtToken")) {
    		jwtToken = requestBody.getQueryParams().get("jwtToken").get(0);
    		System.out.println(jwtToken);
    	}
        final List<GetMTNKPICatalogRMO> data = new ArrayList<GetMTNKPICatalogRMO>();
        
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet get = new HttpGet(baseUri +"/services/getMtnKpiCatalogForIntWo?langId=10&clientProcessId=20210304140829nlmahf2b65s6&seqNo=8&mtnId=" + mtnId);

	        get.setHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
	        get.setHeader("Cookie", "UserData="+jwtToken);
	    	CloseableHttpResponse httpResponse;
	    	try {
				httpResponse = httpclient.execute(get);
				System.out.println(httpResponse.getStatusLine());
				String response = EntityUtils.toString(httpResponse.getEntity());
		    	ObjectMapper mapper = new ObjectMapper();

				JsonNode root = mapper.readTree(response);
				JsonNode kpiList = root.get("results").get(0).get("kpiList");
				System.out.println(kpiList);
		    	data.addAll(mapper.readValue(new TreeTraversingParser(kpiList),new TypeReference<List<GetMTNKPICatalogRMO>>(){}));
		    	httpclient.close();
		    	httpResponse.close();	    		

	    	} catch(IOException e) {
				e.printStackTrace();
	    	} finally {
				return new ResultContainer<>(data, GetMTNKPICatalogRMO.class);
	    	}
    }	
}
