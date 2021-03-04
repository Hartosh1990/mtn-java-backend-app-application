package com.sap.nextgen.vlm;

import java.io.IOException;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cloud.security.token.AccessToken;
import com.sap.cloud.security.token.TokenClaims;



@WebServlet("/hello")
@ServletSecurity(@HttpConstraint(rolesAllowed = { "Display" }))
public class HelloWorldServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(HelloWorldServlet.class);

    @Override
    protected void doGet( final HttpServletRequest request, final HttpServletResponse response )
        throws IOException
    {
        logger.info("I am running!");
        AccessToken token = (AccessToken) request.getUserPrincipal();
		try {
			response.getWriter().write("You ('"
					+ token.getClaimAsString(TokenClaims.EMAIL) + "') "
					+ "are authenticated and can access the application.");
		} catch (final IOException e) {
			logger.error("Failed to write error response: " + e.getMessage() + ".", e);
		}
    	response.getWriter().write("Hello");
    }
}

