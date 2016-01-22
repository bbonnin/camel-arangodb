package io.millesabords.camel.component.arangodb;

public class CamelArangoDbException extends Exception {  

    private static final long serialVersionUID = 5347431428477121661L;

    public CamelArangoDbException(String message, Throwable cause) {
        super(message, cause);
    }

    public CamelArangoDbException(String message) {
        super(message);
    }

    public CamelArangoDbException(Throwable cause) {
        super(cause);
    }

}