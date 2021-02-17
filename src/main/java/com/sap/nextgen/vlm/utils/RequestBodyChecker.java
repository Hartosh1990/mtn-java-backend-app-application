package com.sap.nextgen.vlm.utils;

import com.sap.ida.eacp.nucleus.data.client.model.request.DataRequestBody;
import com.sap.ida.eacp.nucleus.data.client.model.request.FilterDTO;

import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class RequestBodyChecker {

    public static void check(DataRequestBody requestBody) {
        Optional.ofNullable(requestBody.getFilters())
                .ifPresent(RequestBodyChecker::checkFilters);

        Optional.ofNullable(requestBody.getQueryParams())
                .ifPresent(RequestBodyChecker::checkQueryParams);
    }

    public static void checkFilter(FilterDTO filter) {
        if (Objects.isNull(filter.getValues()) || filter.getValues().isEmpty()) {
            throw new BadRequestException("Please specify filter values for " + filter.getId());
        }
    }

    public static void checkFilters(List<FilterDTO> filters) {
        filters.forEach(RequestBodyChecker::checkFilter);
    }


    public static void checkQueryParam(String key, List<String> queryParamValues) {
        if (Objects.isNull(queryParamValues) || queryParamValues.isEmpty()) {
            throw new BadRequestException("Please specify values for the query param key " + key);
        }
    }

    public static void checkQueryParams(Map<String, List<String>> queryParams) {
        queryParams.forEach(RequestBodyChecker::checkQueryParam);
    }


}
