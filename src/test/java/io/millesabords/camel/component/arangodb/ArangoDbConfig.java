package io.millesabords.camel.component.arangodb;

import com.arangodb.ArangoDB;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ArangoDbConfig {

    public static final String DB = "testdb";

    public static final String USERS = "users";

    @Bean
    public ArangoDB arango() {
        final ArangoDB arango = new ArangoDB.Builder().user("root").password("openSesame").build();
        arango.createDatabase(DB);
        arango.db(DB).createCollection(USERS);
        return arango;
    }
}
