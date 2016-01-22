package io.millesabords.camel.component.arangodb;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;
import org.apache.camel.util.CamelContextHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.ArangoConfigure;

/**
 * Represents the component that manages {@link ArangoDbEndpoint}.
 */
public class ArangoDbComponent extends UriEndpointComponent {
    
    private static final Logger LOG = LoggerFactory.getLogger(ArangoDbComponent.class);
        
    public ArangoDbComponent() {
        super(ArangoDbEndpoint.class);
    }

    public ArangoDbComponent(CamelContext context) {
        super(context, ArangoDbEndpoint.class);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        
        ArangoConfigure config = null;
        
        if (!StringUtils.isEmpty(remaining)) {
            config = CamelContextHelper.mandatoryLookup(getCamelContext(), remaining, ArangoConfigure.class);
            LOG.debug("Resolved the ArangoConfigure with the name {} as {}", remaining, config);
        }
        
        final ArangoDbEndpoint endpoint = new ArangoDbEndpoint(uri, this);
        endpoint.setConfigure(config);
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
