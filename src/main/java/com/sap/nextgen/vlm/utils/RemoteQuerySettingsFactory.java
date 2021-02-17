package com.sap.nextgen.vlm.utils;

import com.sap.ea.eacp.remotequeryclient.model.CacheConfiguration;
import com.sap.ea.eacp.remotequeryclient.model.QuerySettings;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.time.Duration;
import java.util.Arrays;

public class RemoteQuerySettingsFactory {

    @Context
    private UriInfo uriInfo;

    public QuerySettings create(Presets preset) {
        final QuerySettings querySettings = new QuerySettings();

        final String queryCacheSettings = uriInfo.getQueryParameters().getFirst("query_fallback_cache");

        if ("false".equalsIgnoreCase(queryCacheSettings)) {
            return querySettings;
        }

        if ("force".equalsIgnoreCase(queryCacheSettings)) {
            return querySettings.setCache(Arrays.asList(
                    new CacheConfiguration("force", Duration.ofDays(14), Duration.ofMillis(0))
            ));
        }

        switch (preset) {
            case FAST:
                querySettings.setCache(Arrays.asList(
                        new CacheConfiguration("quick", Duration.ofHours(1), Duration.ofSeconds(5)),
                        new CacheConfiguration("early", Duration.ofDays(1), Duration.ofSeconds(10)),
                        new CacheConfiguration("slow", Duration.ofDays(7), Duration.ofSeconds(30)),
                        new CacheConfiguration("safeguard", Duration.ofDays(14), Duration.ofMinutes(1))
                ));
                break;
            case MEDIUM:
                querySettings.setCache(Arrays.asList(
                        new CacheConfiguration("quick", Duration.ofHours(1), Duration.ofSeconds(10)),
                        new CacheConfiguration("early", Duration.ofDays(1), Duration.ofSeconds(20)),
                        new CacheConfiguration("slow", Duration.ofDays(7), Duration.ofSeconds(40)),
                        new CacheConfiguration("safeguard", Duration.ofDays(14), Duration.ofMinutes(1))
                ));
                break;
            case SLOW:
                querySettings.setCache(Arrays.asList(
                        new CacheConfiguration("quick", Duration.ofHours(1), Duration.ofSeconds(20)),
                        new CacheConfiguration("early", Duration.ofDays(1), Duration.ofSeconds(40)),
                        new CacheConfiguration("slow", Duration.ofDays(7), Duration.ofSeconds(90)),
                        new CacheConfiguration("safeguard", Duration.ofDays(14), Duration.ofSeconds(110))
                ));
                break;
            case VERY_SLOW:
                querySettings.setCache(Arrays.asList(
                        new CacheConfiguration("quick", Duration.ofHours(1), Duration.ofSeconds(35)),
                        new CacheConfiguration("early", Duration.ofDays(1), Duration.ofSeconds(60)),
                        new CacheConfiguration("slow", Duration.ofDays(7), Duration.ofSeconds(95)),
                        new CacheConfiguration("safeguard", Duration.ofDays(14), Duration.ofSeconds(110))
                ));
            case HOPE_AND_PRAY:
                querySettings.setCache(Arrays.asList(
                        new CacheConfiguration("quick", Duration.ofHours(1), Duration.ofSeconds(60)),
                        new CacheConfiguration("early", Duration.ofDays(1), Duration.ofSeconds(70)),
                        new CacheConfiguration("slow", Duration.ofDays(7), Duration.ofSeconds(100)),
                        new CacheConfiguration("safeguard", Duration.ofDays(14), Duration.ofSeconds(110))
                ));
        }

        return querySettings;
    }

    public enum Presets {
        FAST,
        MEDIUM,
        SLOW,
        VERY_SLOW,
        HOPE_AND_PRAY
    }
}
