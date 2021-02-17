package com.sap.nextgen.vlm.providers;

import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.request.ResultContainer;

import java.io.IOException;

public interface DataProvider<T> {

    DataEndpoint getDataEndpoint();

    ResultContainer<T> loadData(DataRequestBody requestBody) throws IOException;
}
