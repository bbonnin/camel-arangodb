package io.millesabords.camel.component.arangodb;

import java.util.Map;

import com.arangodb.ArangoDB;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.util.CamelContextHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the component that manages {@link ArangoDbEndpoint}.
 */
public class ArangoDbComponent extends DefaultComponent {
    
    private static final Logger LOG = LoggerFactory.getLogger(ArangoDbComponent.class);
        
    public ArangoDbComponent() {
    }

    public ArangoDbComponent(CamelContext context) {
        super(context);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {

        ArangoDB arango = null;
        
        if (!StringUtils.isEmpty(remaining)) {
            arango = CamelContextHelper.mandatoryLookup(getCamelContext(), remaining, ArangoDB.class);
            LOG.debug("Resolved the ArangoDB with the name {} as {}", remaining, arango);
        }
        
        final ArangoDbEndpoint endpoint = new ArangoDbEndpoint(uri, this);
        endpoint.setArango(arango);
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
