package com.sap.nextgen.vlm.rmo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(
value = {"id"}
)
public class IndustryDataRMO {


    public static final String FIELD_ID = "code";
    public static final String NAME = "desc";


    @JsonProperty(FIELD_ID)
    private String id;

    @JsonProperty(NAME)
    private String name;
    
}
