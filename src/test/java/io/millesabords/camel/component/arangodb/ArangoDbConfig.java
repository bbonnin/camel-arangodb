package io.millesabords.camel.component.arangodb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoHost;

@Configuration
public class ArangoDbConfig {
    
    @Bean
    public ArangoConfigure config() {
        final ArangoConfigure configure = new ArangoConfigure();
        configure.setArangoHost(new ArangoHost("localhost", 8529));
        configure.init();
        return configure;
    }

}
