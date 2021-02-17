package com.sap.nextgen.vlm;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Locale;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.togglz.junit.TogglzRule;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;
import com.sap.ea.nga.jersey.AbstractApiTest;
import com.sap.ea.nga.jersey.jms.PerformanceMonitoringRule;
import com.sap.ea.nga.jersey.provider.db.Tenant;
import com.sap.ea.nga.jersey.provider.jackson.ObjectMapperProvider;
import com.sap.ea.nga.jersey.rules.RemoteQueryRule;
import com.sap.it.mobile.hcp.mock.MockitoUtil;
import com.sap.security.um.user.User;

import okhttp3.mockwebserver.MockResponse;


public abstract class APITest extends AbstractApiTest {

    protected static final Logger LOG = LoggerFactory.getLogger(APITest.class);

    @Rule
    public PerformanceMonitoringRule performanceMonitoringRule = new PerformanceMonitoringRule();

    @Rule
    public RemoteQueryRule remoteQuery = new RemoteQueryRule(ObjectMapperProvider.MAPPER);

//    @ClassRule
//    public static final TogglzRule togglzRule = TogglzRule.allDisabled(FeatureToggle.class);

    @Override
    protected ResourceConfig createApplication() {
        final JerseyApplication app = new JerseyApplication();
        app.register(new LocalDatabaseInjectionBinder());
        app.register(new RemoteQueryRule.Binder(() -> remoteQuery));

        return app;
    }

    class LocalDatabaseInjectionBinder extends AbstractBinder {
        @Override
        protected void configure() {
            bind(new Tenant("12345", "eacpv", true)).to(Tenant.class).ranked(1);
        }
    }

    public MockResponse mockResponse(String responseFilePath) {
        final InputStream inputStream = getClass().getResourceAsStream(responseFilePath);
        try {
            final String mockResponse = CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));

            return new MockResponse()
                    .setResponseCode(200)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .setBody(mockResponse);

        } catch (IOException e) {
            LOG.error("Couldn't map file content to string from " + responseFilePath, e);
        }

        return new MockResponse().setResponseCode(404).setBody("");
    }


    protected void mockResponseSequence(String... fileName) {
        remoteQuery.mockResponseSequence(fileName);
    }

    protected void mockRealUserId(String userId) {
        final User userIdMock = MockitoUtil.User.mock(userId, "Mock-Firstname", "Mock-Lastname", userId + "@mock.exchange.sap.corp", Locale.getDefault(), Sets.newHashSet("r_developer"), Collections.emptySet());
        MockitoUtil.UserProvider.setCurrentUser(this.mock.userProvider, userIdMock);
    }
}
