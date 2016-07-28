# camel-arangodb
Camel ArangoDB Component

For more details about ArangoDB, consult the (ArangoDB web site)[https://www.arangodb.com/].

> ArangoDB is a multi-models database. But, at this moment, this component is only dedicated to the document model.


## Build

```bash
mvn clean install
```

## How to use

### Dependency

Add the dependency in your pom.xml
```xml
<dependency>
  <groupId>io.millesabords.camel</groupId>
  <artifactId>camel-arangodb</artifactId>
  <version>1.0</version>
</dependency>
```

### URI format

```
arangodb:configBean?database=databaseName&collection=collectionName&operation=operationName[&moreOptions...]
`̀ `

### Endpoint options

<table>
  <tr><th>name</th><th>Default value</th><th>Description</th></tr>
  <tr><td>database</td><td>none</td><td><b>Required</b>. Name of the default database.</td></tr>
  <tr><td>collection</td><td>none</td><td>Name of the collection to which this endpoint will be bound.</td></tr>
  <tr><td>host</td><td>localhost</td><td>Hostname where ArangoDB is running.</td></tr>
  <tr><td>port</td><td>8529</td><td>Port of ArangoDB.</td></tr>
  <tr><td>operation</td><td>none</td><td>Operation to execute (insert, update, delete, get, aql_query).</td></tr>
  <tr><td>aql</td><td>none</td><td>AQL query to execute (if operation is aql_query).</td></tr>
</table>


### Headers

<table>
  <tr><th>name</th><th>Description</th></tr>
  <tr><td>_arango.operation</td><td>Operation to execute (insert, update, delete, get, aql_query).</td></tr>
  <tr><td>_arango.collection</td><td>Name of the collection to use by the producer.</td></tr>
  <tr><td>_arango.aql_query</td><td>AQL query to execute (if operation is aql_query).</td></tr>
  <tr><td>_arango.aql_query_vars</td><td>Variables to use with the AQL query.</td></tr>
</table>


### Sample routes

* Insert a user in a collection 'users' (the body must be a BaseDocument instance)
```java
from("direct:insert_user")
    .to("arangodb:config?database=testdb&collection=users&operation=insert")
    .to("mock:result");
```

* Find a user (the body must be the document key of the user and you will find the document in the body)
```java
from("direct:get_user")
    .to("arangodb:config?database=testdb&collection=users&operation=get")
    .to("mock:result");
```

* Delete a user (the body must be the document key)
```java
from("direct:delete_user")
    .to("arangodb:config?database=testdb&collection=users&operation=delete")
    .to("mock:result");
```

* Log all the users

```java
from("direct:log_users_bob")
    .setHeader(ArangoDbConstants.ARANGO_AQL_QUERY_HEADER)
        .constant("FOR u IN users FILTER u.name == @name RETURN u")
    .setHeader(ArangoDbConstants.ARANGO_AQL_QUERY_VARS_HEADER)
        .constant(new MapBuilder().put("name", "bob").get())
    .to("arangodb:config?database=testdb&operation=aql_query")
        .split(body())
            .log("${body}")
    .to("mock:result");
```


## The next steps

Feel free to send PRs to fix issues, add new features, etc. Any comments/questions/comments are welcome ! 