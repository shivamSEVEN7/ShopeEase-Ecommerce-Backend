package com.ecommerce.project.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    String resourceName;
    String field;
    String fieldName;
    long fieldID;

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String field, String fieldName, String resourceName) {
        super(resourceName + "not found with " + field + " : " + fieldName);
        this.field = field;
        this.fieldName = fieldName;
        this.resourceName = resourceName;
    }

    public ResourceNotFoundException(String field, long fieldID, String resourceName) {
        super(resourceName + " not found with " + field + " : " + fieldID);
        this.field = field;
        this.fieldID = fieldID;
        this.resourceName = resourceName;
    }
}
