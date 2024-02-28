package com.vedasole.ekartecommercebackend.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException {

    private String resourceName;
    private String fieldName;
    private String fieldValue;

    public ResourceNotFoundException(String resourceName,
                                     String fieldName,
                                     String fieldValue) {

        super(String.format("%s not found with %s : %s",
                resourceName, fieldName, fieldValue));

        this.setResourceName(resourceName);
        this.setFieldName(fieldName);
        this.setFieldValue(fieldValue);
    }

    public ResourceNotFoundException(String resourceName,
                                     String fieldName,
                                     Long fieldValue) {

        super(String.format("%s not found with %s : %s",
                resourceName, fieldName, fieldValue));

        this.setResourceName(resourceName);
        this.setFieldName(fieldName);
        this.setFieldValue(fieldValue.toString());
    }

}
