package com.sap.nextgen.vlm.providers.mtn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;

import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.request.ResultContainer;
import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.nextgen.vlm.constants.VlmConstants;
import com.sap.nextgen.vlm.providers.AbstractProvider;
import com.sap.nextgen.vlm.providers.DataProvider;
import com.sap.nextgen.vlm.rmo.MasterDataGenericRMO;
import com.sap.nextgen.vlm.utils.CacheManager;


public class MTNCurrencyProvider extends AbstractProvider implements DataProvider<MasterDataGenericRMO>{
		
	@Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.MTN_CURRENCY_LIST;
    }

	@Override
    public ResultContainer<MasterDataGenericRMO> loadData(DataRequestBody requestBody) throws ClientProtocolException, ExecutionException, IOException {
    	
        final List<MasterDataGenericRMO> data = new ArrayList<MasterDataGenericRMO>();
        Map<String,MasterDataGenericRMO> currencyList = (Map<String,MasterDataGenericRMO>) CacheManager.getInstance().getCachedObjects(CacheManager.getInstance().getDefaultMasterPackageKey(30, VlmConstants.CurrencyData.name()));
		List<MasterDataGenericRMO> currencyRMOList = new ArrayList<MasterDataGenericRMO>(currencyList.values());
        data.addAll(currencyRMOList);
		return new ResultContainer<>(data, MasterDataGenericRMO.class);
		
    }	
}
