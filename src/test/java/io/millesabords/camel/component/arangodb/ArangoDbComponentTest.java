package io.millesabords.camel.component.arangodb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.arangodb.entity.BaseDocument;
import com.arangodb.util.MapBuilder;

/**
 * Basic unit tests.
 */
public class ArangoDbComponentTest extends CamelSpringTestSupport {
    
    private AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(ArangoDbConfig.class);
    
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return ctx;
    }
    
    private BaseDocument createUser(String key, String name) {
        final BaseDocument user = new BaseDocument(key);
        user.addAttribute("name", name);
        return user;
    }
    
    @Test
    public void testWithUriParams() throws Exception {
        final MockEndpoint mockInsert = getMockEndpoint("mock:result_insert");
        mockInsert.expectedMessageCount(1);       
        template.requestBody("direct:arangodb_insert", createUser("user1", "alice"));
        
        final MockEndpoint mockGet = getMockEndpoint("mock:result_get");
        mockGet.expectedMessageCount(2);
        
        BaseDocument user = template.requestBody("direct:arangodb_get", "user1", BaseDocument.class);
        assertNotNull("User1 is null", user);
   
        final MockEndpoint mockDel = getMockEndpoint("mock:result_del");
        mockDel.expectedMessageCount(1);       
        template.requestBody("direct:arangodb_del", "user1");
        
        user = template.requestBody("direct:arangodb_get", "user1", BaseDocument.class);
        assertNull("User1 is not null", user);
        
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testWithHeaders() throws Exception {
        final MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(5);  
        
        final BaseDocument alice = createUser("user1", "alice");
        
        final Map<String, Object> headers = new HashMap<>();
        headers.put(ArangoDbConstants.ARANGO_COLLECTION_HEADER, "users");
        
        headers.put(ArangoDbConstants.ARANGO_OPERATION_HEADER, "insert");
        template.requestBodyAndHeaders("direct:arangodb", alice, headers);
        
        headers.put(ArangoDbConstants.ARANGO_OPERATION_HEADER, "get");
        BaseDocument user = template.requestBodyAndHeaders("direct:arangodb", "user1", headers, BaseDocument.class);
        assertNotNull("User1 is null", user);
        
        headers.put(ArangoDbConstants.ARANGO_OPERATION_HEADER, "update");
        alice.addAttribute("address", "Wonderland");
        user = template.requestBodyAndHeaders("direct:arangodb", alice, headers, BaseDocument.class);
        assertNotNull("User1 is null", user);
        assertEquals("Address not equal", user.getAttribute("address"), alice.getAttribute("address"));
   
        headers.put(ArangoDbConstants.ARANGO_OPERATION_HEADER, "delete");
        template.requestBodyAndHeaders("direct:arangodb", "user1", headers);
        
        headers.put(ArangoDbConstants.ARANGO_OPERATION_HEADER, "get");
        user = template.requestBodyAndHeaders("direct:arangodb", "user1", headers, BaseDocument.class);
        assertNull("User1 is not null", user);
        
        assertMockEndpointsSatisfied();
    }
    
    @Test
    public void testAql() {
        
        final Map<String, Object> headers = new HashMap<>();
        headers.put(ArangoDbConstants.ARANGO_COLLECTION_HEADER, "users");
        
        headers.put(ArangoDbConstants.ARANGO_OPERATION_HEADER, "insert");
        template.requestBodyAndHeaders("direct:arangodb", createUser("user1", "alice"), headers);
        template.requestBodyAndHeaders("direct:arangodb", createUser("user2", "bob"), headers);
        template.requestBodyAndHeaders("direct:arangodb", createUser("user3", "bob"), headers);
        
        headers.put(ArangoDbConstants.ARANGO_OPERATION_HEADER, "aql_query");
        headers.put(ArangoDbConstants.ARANGO_AQL_QUERY_HEADER, "FOR u IN users FILTER u.name == @name RETURN u");
        
        final Map<String, Object> bindVars = new MapBuilder().put("name", "bob").get();
        headers.put(ArangoDbConstants.ARANGO_AQL_QUERY_VARS_HEADER, bindVars);
        
        final Iterator<BaseDocument> userIter = template.requestBodyAndHeaders("direct:arangodb", null, headers, Iterator.class);
        int nb = 0;
        while (userIter.hasNext()) {
            nb++;
            System.out.println("Key: " + userIter.next().getDocumentKey());
        }
        assertEquals("Bad number of users", nb, 2);
        
        template.request("direct:arangodb_log_users_bob", null);
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:arangodb_insert")
                    .to("arangodb:config?database=testdb&collection=users&operation=insert")
                    .to("mock:result_insert");
                
                from("direct:arangodb_del")
                    .to("arangodb:config?database=testdb&collection=users&operation=delete")
                    .to("mock:result_del");
                
                from("direct:arangodb_get")
                    .to("arangodb:config?database=testdb&collection=users&operation=get")
                    .to("mock:result_get");
                
                from("direct:arangodb")
                    .to("arangodb:config?database=testdb")
                    .to("mock:result");
                
                from("direct:arangodb_log_users_bob")
                    .setHeader(ArangoDbConstants.ARANGO_AQL_QUERY_HEADER)
                        .constant("FOR u IN users FILTER u.name == @name RETURN u")
                    .setHeader(ArangoDbConstants.ARANGO_AQL_QUERY_VARS_HEADER)
                        .constant(new MapBuilder().put("name", "bob").get())
                    .to("arangodb:config?database=testdb&operation=aql_query")
                        .split(body())
                            .log("${body}")
                    .to("mock:result");
            }
        };
    }
}
