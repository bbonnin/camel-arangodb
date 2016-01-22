package io.millesabords.camel.component.arangodb;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.ArangoDriver;
import com.arangodb.ArangoException;
import com.arangodb.DocumentCursor;
import com.arangodb.entity.DocumentEntity;

/**
 * The camel-arangodb producer.
 */
public class ArangoDbProducer extends DefaultProducer implements ArangoDbConstants {
    
    private static final Logger LOG = LoggerFactory.getLogger(ArangoDbProducer.class);
    
    private final ArangoDbEndpoint endpoint;
    
    private final ArangoDriver driver;

    public ArangoDbProducer(ArangoDbEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
        this.driver = endpoint.getDriver();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        
        final String operation = getOperation(exchange);
        
        if (ARANGO_INSERT_DOC.equals(operation)) {
            doInsertDoc(exchange);
        }
        else if (ARANGO_DELETE_DOC.equals(operation)) {
            doDeleteDoc(exchange);
        }
        else if (ARANGO_AQL_QUERY.equals(operation)) {
            doQuery(exchange);
        }
        else {
            throw new CamelArangoDbException("Unknown operation " + operation);
        }
    }
    
    private String getOperation(Exchange exchange) throws CamelArangoDbException {
        String operation = endpoint.getOperation();
        
        if (operation == null) {
            operation = exchange.getIn().getHeader(ARANGO_OPERATION_HEADER, String.class);
        }
        
        if (operation == null) {
            throw new CamelArangoDbException("No operation");
        }
        
        return operation;
    }
    
    private String getCollection(Exchange exchange) throws CamelArangoDbException {
        String collection = endpoint.getCollection();
        
        if (collection == null) {
            collection = exchange.getIn().getHeader(ARANGO_COLLECTION_HEADER, String.class);
        }
        
        if (collection == null) {
            throw new CamelArangoDbException("No collection");
        }
        
        return collection;
    }
    
    private String getQuery(Exchange exchange) throws CamelArangoDbException {
        String aqlQuery  = endpoint.getAql();
        
        if (aqlQuery == null) {
            aqlQuery = exchange.getIn().getHeader(ARANGO_AQL_QUERY_HEADER, String.class);
        }
        
        if (aqlQuery == null) {
            throw new CamelArangoDbException("No query");
        }
        
        return aqlQuery;
    }
    
    /**
     * Create a document in ArangoDB.
     * 
     * @param exchange Exchange containing the data
     * @throws ArangoException Thrown if problem with the database
     * @throws CamelArangoDbException Thrown if problem the data provided in exchange
     */
    private void doInsertDoc(Exchange exchange) throws ArangoException, CamelArangoDbException {
        final Object obj = exchange.getIn().getBody();
        final DocumentEntity<?> doc = driver.createDocument(getCollection(exchange), obj);
        exchange.getOut().setBody(doc);
    }
    
    /**
     * Delete a document in ArangoDB.
     * 
     * @param exchange Exchange containing the data
     * @throws ArangoException Thrown if problem with the database
     * @throws CamelArangoDbException Thrown if problem the data provided in exchange
     */
    private void doDeleteDoc(Exchange exchange) throws ArangoException, CamelArangoDbException {
        DocumentEntity<?> doc = null;
        final String documentKey = exchange.getIn().getBody(String.class);
        if (documentKey != null) {
            doc = driver.deleteDocument(getCollection(exchange), documentKey);
        }
        else {
            final Long documentId = exchange.getIn().getBody(Long.class);
            if (documentId != null) {
                doc = driver.deleteDocument(getCollection(exchange), documentId);
            }
            else {
                throw new CamelArangoDbException("No document id or key");
            }
        }
        exchange.getOut().setBody(doc);
    }
    
    /**
     * Run a AQL query.
     * 
     * @param exchange Exchange containing the data
     * @throws ArangoException Thrown if problem with the database
     * @throws CamelArangoDbException Thrown if problem the data provided in exchange
     */
    private void doQuery(Exchange exchange) throws ArangoException, CamelArangoDbException {
        final String query = getQuery(exchange);
        final Map<String, Object> bindVars = null; //TODO : get in header
        
        final DocumentCursor<Object> cursor = driver.executeDocumentQuery(query, bindVars, null, Object.class);
        exchange.getOut().setBody(cursor != null ? cursor.iterator() : null);
    }

}
