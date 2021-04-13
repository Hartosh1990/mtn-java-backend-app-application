package com.sap.nextgen.vlm;


import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.ea.eacp.okhttp.destinationclient.OkHttpDestinationFactory;
import com.sap.ea.nga.jersey.filter.sapstatistics.SapStatisticsExtension;
import com.sap.ea.nga.jersey.filter.servertiming.ServerTimingExtension;
import com.sap.ea.nga.jersey.openapi.DeprecatedExtension;
import com.sap.ea.nga.jersey.openapi.OpenApiResourceFactory;
import com.sap.ea.nga.jersey.openapi.RolesAllowedOpenAPIExtension;
import com.sap.ea.nga.jersey.provider.jackson.ObjectMapperFactory;
import com.sap.nextgen.vlm.api.CorpOverviewDataApiV3;
import com.sap.nextgen.vlm.cache.apis.ICacheServiceLoader;
import com.sap.nextgen.vlm.cache.apis.impl.IndustryDataLoaderImpl;
import com.sap.nextgen.vlm.cache.apis.impl.MasterDataLoaderImpl;
import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.nextgen.vlm.providers.DataProvider;
import com.sap.nextgen.vlm.providers.mtn.GetMTNSearchResultsProvider;
import com.sap.nextgen.vlm.providers.mtn.GetMTNDashboardDataProvider;
import com.sap.nextgen.vlm.providers.mtn.MTNCompanyProfileProvider;
import com.sap.nextgen.vlm.providers.mtn.MTNPeerProfileProvider;
import com.sap.nextgen.vlm.providers.mtn.MTNTrendAnalysisYearsProvider;
import com.sap.nextgen.vlm.providers.mtn.SaveMTNCompanyProfileInfoProvider;
import com.sap.nextgen.vlm.providers.mtn.SaveMTNCompanyProvider;
import com.sap.nextgen.vlm.utils.CacheManager;
import com.sap.nextgen.vlm.utils.JWTTokenFactory;

import io.swagger.v3.jaxrs2.ext.OpenAPIExtensions;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;


@OpenAPIDefinition(
        info = @Info(
                description = "Corp Overview Custom Data Backend",
                version = "1.0",
                title = "Corp Overview Backend"),
        externalDocs = @ExternalDocumentation(description = "GitHub", url = "https://github.wdf.sap.corp/customer-insights/corporate-overview-backend")
)
@ApplicationPath("/api")
public class JerseyApplication extends ResourceConfig {

    private static final Logger LOG = LoggerFactory.getLogger(JerseyApplication.class);
    
    public JerseyApplication() {
    	
    	System.out.println("In Jersy Application");
        Locale.setDefault(Locale.ENGLISH);
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        register(RolesAllowedDynamicFeature.class);
        register(new LocalInjectionBinder());
        
        // services V3
        register(CorpOverviewDataApiV3.class);
        
        
        // Swagger
        register(OpenApiResourceFactory.create(JerseyApplication.class));
        OpenAPIExtensions.getExtensions().addAll(Arrays.asList(
                RolesAllowedOpenAPIExtension.create("Display"),
                SapStatisticsExtension.create(),
                ServerTimingExtension.create(),
                DeprecatedExtension.create()
        ));
    }

    private static class LocalInjectionBinder extends AbstractBinder {
        @Override
        protected void configure() {

            bind(OkHttpDestinationFactory.class).to(OkHttpDestinationFactory.class).in(Singleton.class);
            bindFactory(ObjectMapperFactory.class).to(ObjectMapper.class).in(Singleton.class);        
            bind(GetMTNDashboardDataProvider.class).to(DataProvider.class).named(DataEndpoint.MTN_DASHBOARD_DATA.name());
            bind(GetMTNSearchResultsProvider.class).to(DataProvider.class).named(DataEndpoint.GET_COMPANY_SEARCH_RESULTS.name());
            bind(SaveMTNCompanyProvider.class).to(DataProvider.class).named(DataEndpoint.SAVE_MTN_COMPANY.name());
            bind(MTNCompanyProfileProvider.class).to(DataProvider.class).named(DataEndpoint.GET_MTN_COMPANY_PROFILE.name());
            bind(MTNPeerProfileProvider.class).to(DataProvider.class).named(DataEndpoint.GET_MTN_PEER_PROFILE.name()).in(Singleton.class);
            bind(SaveMTNCompanyProfileInfoProvider.class).to(DataProvider.class).named(DataEndpoint.SAVE_MTN_COMPANY_PROFILE_INFO.name()).in(Singleton.class);
            bind(MTNTrendAnalysisYearsProvider.class).to(DataProvider.class).named(DataEndpoint.GET_MTN_TREND_ANALYSIS_YEARS.name()).in(Singleton.class);
            bind(MasterDataLoaderImpl.class).to(ICacheServiceLoader.class).in(Singleton.class);
            bind(IndustryDataLoaderImpl.class).to(ICacheServiceLoader.class).in(Singleton.class);
            bindAsContract(JWTTokenFactory.class).in(Singleton.class);
            bindAsContract(CacheManager.class).in(Singleton.class); // cache manager should be registered to the application at start of app.
            
        }
    }
}
