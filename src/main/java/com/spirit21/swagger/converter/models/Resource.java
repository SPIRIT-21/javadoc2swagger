package com.spirit21.swagger.converter.models;

import java.util.List;

/**
 * 
 * @author dsimon
 *
 */
public class Resource implements SwaggerModel {

    private String path;
    private List<Parameter> pathParameters;
    private List<Operation> operations;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public List<Parameter> getPathParameters() {
        return pathParameters;
    }

    public void setPathParameters(List<Parameter> pathParameters) {
        this.pathParameters = pathParameters;
    }
}
