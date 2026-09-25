package dev.laur.deeptimeatlas.taxon.exception;

public class ReferenceNotFoundException extends RuntimeException {

    public ReferenceNotFoundException(Long referenceId) {
        super("Bibliographic reference not found: " + referenceId);
    }
}