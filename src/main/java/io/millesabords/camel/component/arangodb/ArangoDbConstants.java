package io.millesabords.camel.component.arangodb;


public interface ArangoDbConstants {
    
    static final String ARANGO_OPERATION_HEADER = "_arango.operation";
    
    static final String ARANGO_COLLECTION_HEADER = "_arango.collection";
    
    static final String ARANGO_AQL_QUERY_HEADER = "_arango.aql_query";
    
    static final String ARANGO_AQL_QUERY_VARS_HEADER = "_arango.aql_query_vars";
    
    static final String ARANGO_INSERT_DOC = "insert";
    
    static final String ARANGO_UPDATE_DOC = "update";
    
    static final String ARANGO_DELETE_DOC = "delete";
    
    static final String ARANGO_GET_DOC = "get";
    
    static final String ARANGO_AQL_QUERY = "aql_query";
    
}
