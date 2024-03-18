package com.vedasole.ekartecommercebackend.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final String fieldValue;

    public ResourceNotFoundException(String resourceName,
                                     String fieldName,
                                     String fieldValue) {

        super(String.format("%s not found with %s : %s",
                resourceName, fieldName, fieldValue));

        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundException(String resourceName,
                                     String fieldName,
                                     Long fieldValue) {

        super(String.format("%s not found with %s : %s",
                resourceName, fieldName, fieldValue));

        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue.toString();
    }

}
