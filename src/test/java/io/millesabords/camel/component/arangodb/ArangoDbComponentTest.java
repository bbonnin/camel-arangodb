package io.millesabords.camel.component.arangodb;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * https://github.com/arangodb/arangodb-java-driver
 *
 */
public class ArangoDbComponentTest extends CamelTestSupport {
    
    protected ApplicationContext applicationContext;
    
    @Override
    protected CamelContext createCamelContext() throws Exception {
        applicationContext = new AnnotationConfigApplicationContext(ArangoDbConfig.class);
        final CamelContext ctx = SpringCamelContext.springCamelContext(applicationContext);
        return ctx;
    }
  
    @Test
    public void test() throws Exception {
        final MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);       
        template.requestBody("direct:arangodb", "hello");
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:arangodb")
                  .to("arangodb:?database=testdb&collection=test")
                  .to("arangodb:config?database=testdb&collection=test")
                  .to("mock:result");
            }
        };
    }
}
