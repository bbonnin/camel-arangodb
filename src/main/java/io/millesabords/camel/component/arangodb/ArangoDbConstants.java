package io.millesabords.camel.component.arangodb;


public interface ArangoDbConstants {
    
    String ARANGO_OPERATION_HEADER = "_arango.operation";
    
    String ARANGO_COLLECTION_HEADER = "_arango.collection";

    String ARANGO_DATABASE_HEADER = "_arango.database";
    
    String ARANGO_AQL_QUERY_HEADER = "_arango.aql_query";
    
    String ARANGO_AQL_QUERY_VARS_HEADER = "_arango.aql_query_vars";
    
    String ARANGO_INSERT_DOC = "insert";
    
    String ARANGO_UPDATE_DOC = "update";
    
    String ARANGO_DELETE_DOC = "delete";
    
    String ARANGO_GET_DOC = "get";
    
    String ARANGO_AQL_QUERY = "aql_query";
    
}
