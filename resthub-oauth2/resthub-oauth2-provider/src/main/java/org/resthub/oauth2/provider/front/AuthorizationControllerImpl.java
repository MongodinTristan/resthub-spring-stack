package org.resthub.oauth2.provider.front;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.resthub.oauth2.provider.exception.ProtocolException;
import org.resthub.oauth2.provider.exception.ProtocolException.Type;
import org.resthub.oauth2.provider.front.model.TokenResponse;
import org.resthub.oauth2.provider.model.Token;
import org.resthub.oauth2.provider.service.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authorization controller implementation.
 */
@Named("authorizationController")
@Singleton
public class AuthorizationControllerImpl implements AuthorizationController {

	// -----------------------------------------------------------------------------------------------------------------
	// Private attributes
	
	/**
	 * Class logger.
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Inject the service layer.
	 */
	@Inject
	protected AuthorizationService service;
	
	// -----------------------------------------------------------------------------------------------------------------
	// Public attributes

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response obtainAccessTokenBasicCredentials(String clientId,
			String clientSecret, String grant, String scopes, String userName,
			String password) {
		logger.trace("[obtainAccessTokenBasicCredentials] Token generation for clientId '{}', user name '{}', grant " +
				"{} and scopes {}", new Object[]{clientId, userName, grant, scopes});
		// Checks mandatory parameters.
		if(grant == null || clientId == null || clientSecret == null || userName == null || password == null) {
			logger.debug("[obtainAccessTokenBasicCredentials] missing mandatory parameters");
			throw new ProtocolException(Type.INVALID_REQUEST, "grant_type, client_id, client_secret, username and " +
					"password parameters are mandatory");
		}
		// Checks grant_type
		if(grant.compareTo("basic-credentials") != 0) {	
			logger.debug("[obtainAccessTokenBasicCredentials] unsupported grant-type {}", grant);
			throw new ProtocolException(Type.UNSUPPORTED_GRANT_TYPE, "Only grant_type 'basic-credentials' is supported");
		}		
		// Checks clientId and clientSecret
		if(clientId.compareTo("") != 0 || clientSecret.compareTo("") != 0) {	
			logger.debug("[obtainAccessTokenBasicCredentials] non-empty client credentials {}|{}", clientId,
						clientSecret);
			throw new ProtocolException(Type.INVALID_CLIENT_CREDENTIALS, "For now, client id and secret must be empty");
		}		
		// Checks scope
		List<String> scopesList = new ArrayList<String>();
		// Scopes are optional
		if (scopes != null) {
			// Test scope syntax.
			if(scopes.length() != 0 && !scopes.matches("^(\\w*\\s)*\\w*$")) {
				logger.debug("[obtainAccessTokenBasicCredentials] malformed scope {}",scopes);
				throw new ProtocolException(Type.INVALID_SCOPE, "Scope must be a whitespace delimited string");
			}
			// Split with spaces, and skip whitespaces 
			String[] scopesArray = scopes.split(" ");
			for(String scope : scopesArray) {
				if(scope != null && scope.length() > 0) {
					scopesList.add(scope);
				}
			}
		}

		// Calls the service layer.
		Token token = null;
		try {
			token = service.generateToken(scopesList, clientId, clientSecret, userName, password);
		} catch (IllegalArgumentException exc) {
			logger.debug("[obtainAccessTokenBasicCredentials] invalid parameter: {}", exc.getMessage());
			throw new ProtocolException(Type.INVALID_REQUEST, "grant_type, client_id, client_secret, username and " +
				"password parameters are mandatory");
		}
		logger.trace("[obtainAccessTokenBasicCredentials] Generated token: {}", token);
		// Builds a 200 response.
		ResponseBuilder builder = Response.status(Status.OK);
		// Response body.
		builder.entity(new TokenResponse(token, scopes));
		// Cache control
		CacheControl noCache = new CacheControl();
		noCache.setNoStore(true);
		builder.cacheControl(noCache);
		// Sends response.
		return builder.build(); 
	} // obtainAccessTokenBasicCredentials().
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Token obtainTokenInformation(String accessToken) {
		logger.trace("[obtainTokenInformation] Token retrieval for accessToken '{}'", accessToken);
		// Checks mandatory parameters.
		if(accessToken == null) {
			logger.debug("[obtainAccessTokenBasicCredentials] missing mandatory parameters");
			throw new IllegalArgumentException("accessToken parameter is mandatory");
		}
		Token token = service.getTokenInformation(accessToken);
		logger.trace("[obtainTokenInformation] Retrieved token: {}", token);
		return token;
	} // obtainTokenInformation().

} // class AuthorizationControllerImpl