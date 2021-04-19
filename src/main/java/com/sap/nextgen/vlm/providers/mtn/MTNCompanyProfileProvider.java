package com.sap.nextgen.vlm.providers.mtn;

import java.sql.Connection;
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
    String langId = "10";
    Connection connection;
    
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
    	if (queryParams.containsKey("langId")) {
    		langId = requestBody.getQueryParams().get("langId").get(0);
    	}

        final List<MTNCompanyProfileRMO> data = new ArrayList<MTNCompanyProfileRMO>();
        

	    ObjectMapper mapper = new ObjectMapper();
		try {
			String uri = baseUri +"/services/getMtnCompany?langId=10&clientProcessId="+clientProcessId+"&seqNo=8&mtnId="+mtnId;
			JsonNode root = HttpRequestManager.getRootObjectFromGetNodeService(jwtToken,uri);
			JsonNode companyProfileObject = root.get("results");
			System.out.println(companyProfileObject);
			MTNCompanyProfileRMO companyProfileInfo = mapper.treeToValue(companyProfileObject, MTNCompanyProfileRMO.class);
			String businessDescription = getAvailableBusinessDesc(mtnId, langId);
			companyProfileInfo.setBusinessDesc(businessDescription);
			data.add(companyProfileInfo); 		    
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return new ResultContainer<>(data, MTNCompanyProfileRMO.class);
		}		
		
    }
	
    
    public String getAvailableBusinessDesc(String mtnId, String langId) throws SQLException {
    	String companyText = "";
    	if(mtnId != null) {
    		String query =  "select \"T0431_CompanyText\"  from \"T0402_MTN_Attributes\" a  "
    				+ " join \"T0401_MTN\" d on d.\"T0401_AutoID\" = a.\"T0401_AutoID\" "
    				+ " left outer join \"L0431_CIQCompany_Info\" b on a.\"T0431_AutoID\" = b.\"T0431_AutoID\" and b.\"T0018_ID\" = "+langId
    				+ " where d.\"T0401_MTNID\" = "+mtnId+" and d.\"T0401_VersionNo\" = 1";
    		ResultSet companyTextResult =  DBQueryManager.getResultSet(query,connection);
    		companyTextResult.next();
    		companyText = companyTextResult.getString(1);
    		if(connection != null) {
        		connection.close();    			
    		}
    	}
    	return companyText;
    }
    
}
