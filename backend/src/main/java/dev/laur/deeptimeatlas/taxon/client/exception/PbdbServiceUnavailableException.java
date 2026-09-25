package dev.laur.deeptimeatlas.taxon.client.exception;

public class PbdbServiceUnavailableException extends RuntimeException {
    public PbdbServiceUnavailableException(
        String message,
        Throwable cause
                        
    ){
        super(message,cause);
    }
    
}
