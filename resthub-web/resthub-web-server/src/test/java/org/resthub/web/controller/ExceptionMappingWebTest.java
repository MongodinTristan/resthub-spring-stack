package org.resthub.web.controller;

import org.resthub.test.AbstractWebTest;
import org.resthub.web.exception.BadRequestClientException;
import org.resthub.web.exception.ConflictClientException;
import org.resthub.web.exception.InternalServerErrorClientException;
import org.resthub.web.exception.NotAcceptableClientException;
import org.resthub.web.exception.NotFoundClientException;
import org.testng.annotations.Test;

public class ExceptionMappingWebTest extends AbstractWebTest {
    
    public ExceptionMappingWebTest() {
         super("resthub-web-server,resthub-jpa");
    }
    
    @Test(expectedExceptions=NotAcceptableClientException.class)
    public void testHttpMediaTypeNotAcceptableException() {
        this.request("exception/test-default-spring-exception").getJson();
    }
    
    @Test(expectedExceptions=BadRequestClientException.class)
    public void testIllegalArgumentException() {
        this.request("exception/test-illegal-argument-exception").getJson();
    }
    
    @Test(expectedExceptions=InternalServerErrorClientException.class)
    public void testException() {
        this.request("exception/test-exception").getJson();
    }
    
    @Test(expectedExceptions=InternalServerErrorClientException.class)
    public void testRuntimeException() {
        this.request("exception/test-runtime-exception").getJson();
    }
    
    // Uncatched ClientEception should lead to an Internel Server Error, regardless the ClientException instance status code
    @Test(expectedExceptions=InternalServerErrorClientException.class)
    public void testClientException() {
        this.request("exception/test-client-exception").getJson();
    }
    
    @Test(expectedExceptions=NotFoundClientException.class)
    public void testObjectNotFoundException() {
        this.request("exception/test-object-not-found-exception").getJson();
    }
    
    @Test(expectedExceptions=NotFoundClientException.class)
    public void testEntityNotFoundException() {
        this.request("exception/test-entity-not-found-exception").getJson();
    }
    
    @Test(expectedExceptions=ConflictClientException.class)
    public void testEntityExistsException() {
        this.request("exception/test-entity-exists-exception").getJson();
    }
    
}
