package com.sap.nextgen.vlm.providers.mtn;

import java.util.ArrayList;
import java.util.List;

import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.request.ResultContainer;
import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.nextgen.vlm.providers.AbstractProvider;
import com.sap.nextgen.vlm.providers.DataProvider;
import com.sap.nextgen.vlm.rmo.MtnDashboardRMO;

public class CloudTransactionsS4HanaAdrmProvider extends AbstractProvider implements DataProvider<MtnDashboardRMO> {
    @Override
    public DataEndpoint getDataEndpoint() {
        return DataEndpoint.CLOUD_TRANSACTIONS_SALES_ADRM_S4;
    }

    @Override
    public ResultContainer<MtnDashboardRMO> loadData(DataRequestBody requestBody) {
        resolveRequestBody(requestBody);

//        RemoteQueryBridge.Client client = getBridgeClient();
//
//        Map<String, List<String>> queryParams = Optional.ofNullable(requestBody.getQueryParams()).orElse(new HashMap<>());
//
//        String quarter = YearQuarter.now().toString();
//
//        List<String> regions = Lists.newArrayList("AP", "EMNORTH", "EMSOUTH", "GCN", "MEE", "LA", "NA");
//        List<String> salesbags = Lists.newArrayList(
//                "S4H Cloud ES - Enterprise",
//                "S4H Cloud ES - Tech Addons & Serv",
//                "S4H Cloud EX - EMS",
//                "S4H Cloud EX - Enterprise",
//                "S4H Cloud EX - Suite Foundation",
//                "S4H Cloud EX - Tech Addons & Serv");
//
//        List<String> products = Lists.newArrayList(
//                "DCL",
//                "DSC",
//                "ERP",
//                "FCL",
//                "FIN",
//                "FS4",
//                "HEE",
//                "PC4",
//                "S4H",
//                "SBP",
//                "SOP",
//                "SPC",
//                "SPO");
//
//        if (queryParams.containsKey("quarter")) {
//            quarter = requestBody.getQueryParams().get("quarter").get(0);
//        }
//        if (queryParams.containsKey("regions")) {
//            regions = requestBody.getQueryParams().get("regions");
//        }

        final List<MtnDashboardRMO> data = new ArrayList<MtnDashboardRMO>();
        MtnDashboardRMO obj1 = new MtnDashboardRMO();
        obj1.setMtnId(1011);
        obj1.setCompanyName("Test MTN");
        data.add(obj1);
        return new ResultContainer<>(data, MtnDashboardRMO.class);
    }


//    private Double calculateGrowth(Double d1, Double d2) {
//        return div(diff(d1, d2), d2);
//    }
}
