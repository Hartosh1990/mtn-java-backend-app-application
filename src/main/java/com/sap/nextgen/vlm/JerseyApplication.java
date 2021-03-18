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
import com.sap.nextgen.vlm.constants.DataEndpoint;
import com.sap.nextgen.vlm.providers.DataProvider;
import com.sap.nextgen.vlm.providers.mtn.GetMTNSearchResultsProvider;

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
        // Configuration
        Locale.setDefault(Locale.ENGLISH);

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

//        ObjectMapperProvider.MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//        ObjectMapperProvider.MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

//        register(ObjectMapperProvider.class);
        //register(MultiPartFeature.class);
        register(RolesAllowedDynamicFeature.class);
        register(new LocalInjectionBinder());

//        MonitoringFeature.register(this);
//        register(new RemoteQueryBinder());
        //register(RemoteQueryAPI.class);

        // Exceptions
//        register(StackTraceExceptionMapper.class);
//
//        register(Utf8CharsetFilter.class);
//        register(ETagFilterFeature.class);
//        register(ETagCacheFilterFeature.class);

        // services V3
        register(CorpOverviewDataApiV3.class);

        // Swagger

        // Swagger
        register(OpenApiResourceFactory.create(JerseyApplication.class));
        OpenAPIExtensions.getExtensions().addAll(Arrays.asList(
                RolesAllowedOpenAPIExtension.create("Display"),
                SapStatisticsExtension.create(),
                ServerTimingExtension.create(),
                DeprecatedExtension.create()
        ));


      //  RemoteQueryBuilder.DEFAULT_SCHEMA = "_SYS_BIC";

    }

    private static class LocalInjectionBinder extends AbstractBinder {
        @Override
        protected void configure() {
            //bind(Database.class).to(Database.class).proxy(true).proxyForSameScope(false).in(RequestScoped.class);
            //bindFactory(TenantFactory.class).to(Tenant.class).in(RequestScoped.class);

            bind(OkHttpDestinationFactory.class).to(OkHttpDestinationFactory.class).in(Singleton.class);
            bindFactory(ObjectMapperFactory.class).to(ObjectMapper.class).in(Singleton.class);
            //bindFactory(UserProviderFactory.class).to(UserProvider.class);
            //bindFactory(PasswordStorageFactory.class).to(PasswordStorage.class).in(Singleton.class);
            //bind(RemoteQuerySettingsFactory.class).to(RemoteQuerySettingsFactory.class).in(Singleton.class);
        
            bind(GetMTNSearchResultsProvider.class).to(DataProvider.class).named(DataEndpoint.GET_COMPANY_SEARCH_RESULTS.name()).in(Singleton.class);
           
        }
    }
}
