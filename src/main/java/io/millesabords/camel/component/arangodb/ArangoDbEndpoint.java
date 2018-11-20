package io.millesabords.camel.component.arangodb;

import com.arangodb.ArangoDB;
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

/**
 * Represents an ArangoDB endpoint.
 */
@UriEndpoint(scheme = "arangodb", title = "ArangoDB", syntax = "arangodb:arangoBean", producerOnly = true, label = "database,nosql")
public class ArangoDbEndpoint extends DefaultEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(ArangoDbEndpoint.class);
    
    private static final String DEFAULT_HOST = "localhost";
    
    private static final int DEFAULT_PORT = 8529;
    
    /** Name of ArangoDB bean. */
    @UriPath
    private String arangoBean;
    
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

    /** User name to access ArangoDB. */
    @UriParam
    private String user;

    /** Password to access ArangoDB. */
    @UriParam
    private String password;
    
    /** Operation to execute. */
    @UriParam
    private String operation;
    
    /** AQL Query to execute if operation is 'aql_query'. */
    @UriParam
    private String aql;

    private ArangoDB arango;

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

        if (arango == null) {
            mustShutdown = true;

            final ArangoDB.Builder builder = new ArangoDB.Builder().host(host, port);

            if (user != null) {
                builder.user(user).password(password);
            }

            arango = builder.build();
        }

        LOG.info("Connected to {}:{}, default db is {}", host, port, database);
    }

    @Override
    protected void doStop() throws Exception {
        if (mustShutdown && arango != null) {
            arango.shutdown();
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

    public ArangoDB getArango() {
        return arango;
    }

    public void setArango(ArangoDB arango) {
        this.arango = arango;
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

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
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

    public String getArangoBean() {
        return arangoBean;
    }

    public void setArangoBean(String arangoBean) {
        this.arangoBean = arangoBean;
    }

}
