package com.sap.nextgen.vlm.providers;

import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.nextgen.vlm.utils.RequestBodyChecker;

public abstract class AbstractProvider {

//    @Inject
//    protected RemoteQueryClient client;




    protected void resolveRequestBody(DataRequestBody requestBody) {
        RequestBodyChecker.check(requestBody);
    }

//    protected RemoteQueryBridge.Client getBridgeClient() {
//        final RemoteQueryBridge.Client remoteQueryBridgeClient = RemoteQueryBridge.create(client);
//        return remoteQueryBridgeClient;
//    }
//
//    protected RemoteQueryClient getClient() {
//        return client;
//    }

}
