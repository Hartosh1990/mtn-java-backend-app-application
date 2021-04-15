package com.sap.nextgen.vlm.providers.mtn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.request.ResultContainer;
import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.nextgen.vlm.constants.VlmConstants;
import com.sap.nextgen.vlm.providers.AbstractProvider;
import com.sap.nextgen.vlm.providers.DataProvider;
import com.sap.nextgen.vlm.rmo.MTNCompanyProfileRMO;
import com.sap.nextgen.vlm.utils.DBQueryManager;
import com.sap.nextgen.vlm.utils.HttpRequestManager;

public class MTNCompanyProfileProvider extends AbstractProvider implements DataProvider<MTNCompanyProfileRMO> {
    String mtnId;
    String ciqId;
    String companyName;
    String isMtnCompany;
    String clientProcessId;
    String baseUri = "https://vlmdev.cfapps.eu10.hana.ondemand.com";
    String jwtToken; 
    
    @Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.GET_MTN_COMPANY_PROFILE;
    }
    @SuppressWarnings("finally")
	@Override
    public ResultContainer<MTNCompanyProfileRMO> loadData(DataRequestBody requestBody) {

    	Map<String, List<String>> queryParams = Optional.ofNullable(requestBody.getQueryParams()).orElse(new HashMap<>());
    	if (queryParams.containsKey("mtnId")) {
    		mtnId = requestBody.getQueryParams().get("mtnId").get(0);
    	}
    	if (queryParams.containsKey("ciqId")) {
    		ciqId = requestBody.getQueryParams().get("ciqId").get(0);
    	}
    	if (queryParams.containsKey("jwtToken")) {
    		jwtToken = requestBody.getQueryParams().get("jwtToken").get(0);
    	}
    	if (queryParams.containsKey("clientProcessId")) {
    		clientProcessId = requestBody.getQueryParams().get("clientProcessId").get(0);
    	}else {
    		clientProcessId = mtnId+"_intwomtn";
    	}

        final List<MTNCompanyProfileRMO> data = new ArrayList<MTNCompanyProfileRMO>();
        

	    ObjectMapper mapper = new ObjectMapper();
		try {
			String uri = baseUri +"/services/getMtnCompany?langId=10&clientProcessId="+clientProcessId+"&seqNo=8&mtnId="+mtnId;
			JsonNode root = HttpRequestManager.getRootObjectFromGetNodeService(jwtToken,uri);
			JsonNode companyProfileObject = root.get("results");
			System.out.println(companyProfileObject);
			MTNCompanyProfileRMO companyProfileInfo = mapper.treeToValue(companyProfileObject, MTNCompanyProfileRMO.class);
			String peerDataFlag = getIsPeerDataAvailableFlag(mtnId);
			String getPeersUri = baseUri +"/services/getMtnProfileData?langId=10&clientProcessId="+clientProcessId+"&seqNo=8";
			String body = "{\"ciq_id\":\""+ciqId+"\",\"mtnid\":"+mtnId+",\"isPeerDataAvailable\":"+peerDataFlag+"}";
			JsonNode businessDescNode = HttpRequestManager.getRootObjectFromPostNodeService(jwtToken, getPeersUri, body);
			String businessDescription = businessDescNode.get(VlmConstants.results.name()).get(0).get("businessDesc").asText();
			companyProfileInfo.setBusinessDesc(businessDescription);
			data.add(companyProfileInfo); 		    
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return new ResultContainer<>(data, MTNCompanyProfileRMO.class);
		}		
		
    }
	
    
    public String getIsPeerDataAvailableFlag(String mtnId) throws SQLException {
    	String peerDataFlag = "0";
    	if(mtnId != null) {
    		String query =  "select \"T0402_IsPeerDataAvailable\" from \"T0402_MTN_Attributes\" a "
    				+ "	join \"T0401_MTN\" d on d.\"T0401_AutoID\" = a.\"T0401_AutoID\""
    				+ "	where d.\"T0401_MTNID\" = "+mtnId+" and d.\"T0401_VersionNo\" = 1";
    		ResultSet companyTextResult =  DBQueryManager.getResultSet(query);
    		companyTextResult.next();
    		peerDataFlag = companyTextResult.getString(1);
    	}
    	
    	return peerDataFlag;
    }
    
}
