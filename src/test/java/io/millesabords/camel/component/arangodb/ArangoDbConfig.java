package io.millesabords.camel.component.arangodb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoDriver;
import com.arangodb.ArangoException;
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
    
    @Bean(initMethod = "init", destroyMethod = "shutdown")
    public ArangoDriver driver() {
        final ArangoDriver driver = new ArangoDriver(config()) {
            public void init() throws ArangoException {
                this.createDatabase("testdb");
                this.setDefaultDatabase("testdb");
                this.createCollection("users");
            }
            
            public void shutdown() throws ArangoException {
                this.deleteDatabase("testdb");
            }
        };
        return driver;
    }

}
