package io.millesabords.camel.component.arangodb;

import java.util.Map;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.entity.DocumentUpdateEntity;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The camel-arangodb producer.
 */
public class ArangoDbProducer extends DefaultProducer implements ArangoDbConstants {
    
    private static final Logger LOG = LoggerFactory.getLogger(ArangoDbProducer.class);
    
    private final ArangoDbEndpoint endpoint;
    
    private final ArangoDB arango;

    public ArangoDbProducer(ArangoDbEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
        this.arango = endpoint.getArango();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        
        final String operation = getOperation(exchange);
        
        LOG.debug("Process operation {}", operation);
        
        if (ARANGO_INSERT_DOC.equals(operation)) {
            doInsertDoc(exchange);
        }
        else if (ARANGO_UPDATE_DOC.equals(operation)) {
            doUpdateDoc(exchange);
        }
        else if (ARANGO_DELETE_DOC.equals(operation)) {
            doDeleteDoc(exchange);
        }
        else if (ARANGO_GET_DOC.equals(operation)) {
            doGetDoc(exchange);
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

    private String getDB(Exchange exchange) throws CamelArangoDbException {
        String db = endpoint.getDatabase();

        if (db == null) {
            db = exchange.getIn().getHeader(ARANGO_DATABASE_HEADER, String.class);
        }

        if (db == null) {
            throw new CamelArangoDbException("No database");
        }

        return db;
    }
    
    private String getQuery(Exchange exchange) throws CamelArangoDbException {
        String aqlQuery = endpoint.getAql();
        
        if (aqlQuery == null) {
            aqlQuery = exchange.getIn().getHeader(ARANGO_AQL_QUERY_HEADER, String.class);
        }
        
        if (aqlQuery == null) {
            throw new CamelArangoDbException("No query");
        }
        
        return aqlQuery;
    }

    private ArangoCollection getArangoCollection(Exchange exchange) throws CamelArangoDbException {
        return arango.db(getDB(exchange)).collection(getCollection(exchange));
    }
    
    /**
     * Create a document in ArangoDB.
     * 
     * @param exchange Exchange containing the data
     * @throws CamelArangoDbException Thrown if problem the data provided in exchange
     */
    private void doInsertDoc(Exchange exchange) throws CamelArangoDbException {
        final Object obj = exchange.getIn().getBody();
        final DocumentCreateEntity doc = getArangoCollection(exchange).insertDocument(obj);
        exchange.getOut().setBody(doc);
    }
    
    /**
     * Update a document in ArangoDB.
     * 
     * @param exchange Exchange containing the data
     * @throws CamelArangoDbException Thrown if problem the data provided in exchange
     */
    private void doUpdateDoc(Exchange exchange) throws CamelArangoDbException {
        final BaseDocument newdoc = exchange.getIn().getBody(BaseDocument.class);
        final DocumentUpdateEntity doc = getArangoCollection(exchange).updateDocument(newdoc.getKey(), newdoc);
        exchange.getOut().setBody(doc);
    }
    
    /**
     * Find a document in ArangoDB.
     * 
     * @param exchange Exchange containing the document key
     * @throws CamelArangoDbException Thrown if problem the data provided in exchange
     */
    private void doGetDoc(Exchange exchange) throws CamelArangoDbException {
        final String key = exchange.getIn().getBody(String.class);
        BaseDocument doc = null;
        
        try {
            doc = getArangoCollection(exchange).getDocument(key, BaseDocument.class);
        }
        catch (ArangoDBException e) {
            // If the exception is "not found", let set the body to null, otherwise throw the exception
            if (e.getErrorNum() != 404) {
                throw e;
            }
        }
        
        exchange.getOut().setBody(doc);
    }
    
    /**
     * Delete a document in ArangoDB.
     * 
     * @param exchange Exchange containing the data
     * @throws CamelArangoDbException Thrown if problem the data provided in exchange
     */
    private void doDeleteDoc(Exchange exchange) throws CamelArangoDbException {
        DocumentEntity doc = null;
        final String documentKey = exchange.getIn().getBody(String.class);
        if (documentKey != null) {
            doc = getArangoCollection(exchange).deleteDocument(documentKey);
        }
        else {
            throw new CamelArangoDbException("No document key");
        }
        exchange.getOut().setBody(doc);
    }
    
    /**
     * Run a AQL query.
     * 
     * @param exchange Exchange containing the data
     * @throws CamelArangoDbException Thrown if problem the data provided in exchange
     */
    private void doQuery(Exchange exchange) throws CamelArangoDbException {
        final String query = getQuery(exchange);
        final Map<String, Object> bindVars = exchange.getIn().getHeader(ARANGO_AQL_QUERY_VARS_HEADER, Map.class);
        
        final ArangoCursor<BaseDocument> cursor = arango.db(getDB(exchange))
                .query(query, bindVars, null, BaseDocument.class);
        exchange.getOut().setBody(cursor != null ? cursor.iterator() : null);
    }

}
