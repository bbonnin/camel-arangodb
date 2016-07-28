package io.millesabords.camel.component.arangodb;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoDriver;
import com.arangodb.ArangoHost;

/**
 * Represents an ArangoDB endpoint.
 */
@UriEndpoint(scheme = "arangodb", title = "ArangoDB", syntax = "arangodb:configBean", producerOnly = true, label = "database,nosql")
public class ArangoDbEndpoint extends DefaultEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(ArangoDbEndpoint.class);
    
    private static final String DEFAULT_HOST = "localhost";
    
    private static final int DEFAULT_PORT = 8529;
    
    /** Name of configuration bean. */
    @UriPath
    private String configBean;
    
    /** Name of the database. */
    @UriParam @Metadata(required = "true")
    private String database;

    /** Name of the collection. */
    @UriParam
    private String collection;
    
    /** Hostname of ArangoDB. */
    @UriParam
    private final String host = DEFAULT_HOST;
    
    /** Port of ArangoDB. */
    @UriParam
    private final int port = DEFAULT_PORT;
    
    /** Operation to execute. */
    @UriParam
    private String operation;
    
    /** AQL Query to execute if operation is 'aql_query'. */
    @UriParam
    private String aql;

    private ArangoDriver driver;

    private ArangoConfigure configure;

    private boolean mustShutdown;


    public ArangoDbEndpoint() {
    }

    public ArangoDbEndpoint(String uri, ArangoDbComponent component) {
        super(uri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new ArangoDbProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("Cannot consume from an ArangoDbEndpoint: " + getEndpointUri());
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        if (configure == null) {
            mustShutdown = true;
            configure = new ArangoConfigure();
            configure.setArangoHost(new ArangoHost(host, port));
            configure.init();
        }
        
        configure.setDefaultDatabase(database);
        
        driver = new ArangoDriver(configure);
        
        LOG.info("Connected to {}:{}, default db is {}", host, port, database);
    }

    @Override
    protected void doStop() throws Exception {
        if (mustShutdown && configure != null) {
            configure.shutdown();
        }
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public ArangoDriver getDriver() {
        return driver;
    }

    public void setDriver(ArangoDriver driver) {
        this.driver = driver;
    }

    public ArangoConfigure getConfigure() {
        return configure;
    }

    public void setConfigure(ArangoConfigure configure) {
        this.configure = configure;
    }

    public boolean isMustShutdown() {
        return mustShutdown;
    }

    public void setMustShutdown(boolean mustShutdown) {
        this.mustShutdown = mustShutdown;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getAql() {
        return aql;
    }

    public void setAql(String aql) {
        this.aql = aql;
    }

    public String getConfigBean() {
        return configBean;
    }

    public void setConfigBean(String configBean) {
        this.configBean = configBean;
    }

}
