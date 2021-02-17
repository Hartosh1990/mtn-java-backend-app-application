package com.sap.nextgen.vlm.model.chart;

import com.sap.ida.eacp.nucleus.data.client.model.response.V1C4sComponentMetadataDTO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class C4SComponentMetaDataChartDTO extends V1C4sComponentMetadataDTO {

    String type;
}
