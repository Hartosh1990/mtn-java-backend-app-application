package com.sap.nextgen.vlm.providers.mtn;

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
import com.sap.nextgen.vlm.rmo.PeerInfoRMO;
import com.sap.nextgen.vlm.rmo.PeersDeleteListRMO;
import com.sap.nextgen.vlm.rmo.SaveMTNCustomerProfileRMO;
import com.sap.nextgen.vlm.utils.HttpRequestManager;

public class SaveMTNCompanyProfileInfoProvider extends AbstractProvider implements DataProvider<SaveMTNCustomerProfileRMO> {
	String mtnId;
	String saveType;
	String id;
	String newValue;
    String jwtToken; 
    int langId = 10;
    String isMtnCompany = "0";
    
	@Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.SAVE_MTN_COMPANY_PROFILE_INFO;
    }
	
	@Override
    public ResultContainer<SaveMTNCustomerProfileRMO> loadData(DataRequestBody requestBody) {
    	Map<String, List<String>> queryParams = Optional.ofNullable(requestBody.getQueryParams()).orElse(new HashMap<>());
    	List<String> akpiId = null;
        List<String> dkpiId = null;
        String currencyId = null;
        String newCiqId = null;
        String newCompanyName = null;
    	String companyIdtobedeleted = null;
    	PeersDeleteListRMO plObject = new PeersDeleteListRMO();
        final String clientProcessId;
        if (queryParams.containsKey("langId")) {
    		langId = Integer.parseInt(requestBody.getQueryParams().get("langId").get(0));
    	}
    	if (queryParams.containsKey("mtnId")) {
    		mtnId = requestBody.getQueryParams().get("mtnId").get(0);
    	}
    	if (queryParams.containsKey("saveType")) {
    		saveType = requestBody. getQueryParams().get("saveType").get(0);
    	}
    	if (queryParams.containsKey("newValue")) {
    		newValue = requestBody. getQueryParams().get("newValue").get(0);
    	}
    	if (queryParams.containsKey("currencyId")) {
    		currencyId = requestBody. getQueryParams().get("currencyId").get(0);
    	}
    	if (queryParams.containsKey("clientProcessId") && queryParams.get("clientProcessId")!=null) {
    		clientProcessId = requestBody.getQueryParams().get("clientProcessId").get(0);
    	}else {
    		clientProcessId = mtnId+"saveMTNCompanyProfileInfo+_intwomtn";
    	}
    	if (queryParams.containsKey("jwtToken")) {
    		jwtToken = requestBody.getQueryParams().get("jwtToken").get(0);
    	}
    	if (queryParams.containsKey("valuestobeAdded") && !queryParams.get("valuestobeAdded").isEmpty() && queryParams.get("valuestobeAdded").get(0) != null) {
    		akpiId = queryParams.get("valuestobeAdded");
    	}
    	if (queryParams.containsKey("valuestobeDeleted") && !queryParams.get("valuestobeDeleted").isEmpty() && queryParams.get("valuestobeDeleted").get(0) != null) {
    		dkpiId = queryParams.get("valuestobeDeleted");
    	}
    	if (queryParams.containsKey("newPeerCIQ")&& !queryParams.get("newPeerCIQ").isEmpty() && queryParams.get("newPeerCIQ").get(0) != null) {
    		newCiqId = queryParams.get("newPeerCIQ").get(0);
    	}
    	if (queryParams.containsKey("newPeerCompName") && !queryParams.get("newPeerCompName").isEmpty() && queryParams.get("newPeerCompName").get(0) != null) {
    		newCompanyName = requestBody.getQueryParams().get("newPeerCompName").get(0);
    	}
    	if(queryParams.containsKey("companyId")&& !queryParams.get("companyId").isEmpty() && queryParams.get("companyId").get(0) != null) {
    		companyIdtobedeleted = queryParams.get("companyId").get(0);
    		PeerInfoRMO pInfoRMO = new PeerInfoRMO();
    		pInfoRMO.setCompanyId(companyIdtobedeleted);
    		List<PeerInfoRMO> pl = List.of(pInfoRMO);
    		plObject.setPeers(pl);
    	}
        final List<SaveMTNCustomerProfileRMO> data = new ArrayList<SaveMTNCustomerProfileRMO>();
        
        ObjectMapper mapper = new ObjectMapper();
        if(saveType!= null && Integer.parseInt(saveType) == 11) { // To support KPI add/delete in save call..
        	if(akpiId != null) {
      			akpiId.forEach((kpiId)->{
      				String addUri = baseUri +"/services/getMtnMetricsKpis?langId="+langId+"&clientProcessId="+clientProcessId+"&seqNo=0&mtnId="+mtnId;
      				addUri = addUri+"&kpiId="+kpiId;
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
	          					System.out.println("The KPI with id "+kpiId + " is deleted successfully");
	          				};
	          			}
					} catch (Exception e) {
						e.printStackTrace();
					}
          				
      			});
      			
      		}
        }else if(saveType!= null && Integer.parseInt(saveType) == 12) { // To support peer add/delete in save call..
        	String addPeerUri = baseUri+"/services/saveCompanyForMtn?langId=" + langId + "&clientProcessId=" + clientProcessId +"&seqNo=0";
            if(newCiqId != null) {
            	String json = "{\"Response\": [{\"ciq_id\" :\"" + newCiqId + "\", \"companyName\": \"" + newCompanyName + "\", \"isMtnCompany\": \"" + isMtnCompany + "\", \"mtnid\":" + mtnId + "}]}";
            	JsonNode addedPeer;
				try {
					addedPeer = HttpRequestManager.getRootObjectFromPostNodeService(jwtToken, addPeerUri, json);
					if(addedPeer != null && addedPeer.get("success")!= null) {
	      				if(addedPeer.get("success").asBoolean()) {
	      					System.out.println("The Peer with cid "+ newCiqId + " is added successfully");
	      				};
	      			}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            }
            if(companyIdtobedeleted != null) {
            	try {
            		String json = mapper.writeValueAsString(plObject);
                	String uri = baseUri + "/services/deleteMTNCompany?langId=" + langId + "&clientProcessId=" + clientProcessId +"&seqNo=0&mtnId="+mtnId;
                	JsonNode deletedPeer = HttpRequestManager.getRootObjectFromPostNodeService(jwtToken, uri, json);
                	if(deletedPeer != null && deletedPeer.get("success")!= null) {
          				if(deletedPeer.get("success").asBoolean()) {
          					System.out.println("The Peer with company id "+ companyIdtobedeleted + " is deleted successfully");
          				};
          			}
            	}
            	catch(Exception e) {
            		e.printStackTrace();
            	}
            	
            }
        }
        else {
        	String finalUri = baseUri +"/services/saveMtnValue?langId=10&clientProcessId=20210304140829nlmahf2b65s6&seqNo=8&mtnId=" + mtnId + "&saveType=" + saveType;
        	String json = "{\"id\" :\"" + mtnId + "\", \"newValue\": \"" + newValue + "\"}";
        	if(saveType!= null && Integer.parseInt(saveType) == 1) {
        		json = "{\"id\" :\"" + currencyId + "\", \"newValue\": \"" + newValue + "\"}";
        	}
     
        	try {
        		JsonNode root = HttpRequestManager.getRootObjectFromPostNodeService(jwtToken, finalUri, json);
        		data.add(mapper.treeToValue(root, SaveMTNCustomerProfileRMO.class));
        	} catch (Exception e) {
				e.printStackTrace();
			} 
        }
        
        return new ResultContainer<>(data, SaveMTNCustomerProfileRMO.class);
        
    }

}
