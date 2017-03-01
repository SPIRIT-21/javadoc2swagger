package com.spirit21.swagger.converter.models;

import java.util.List;

/**
 * 
 * @author dsimon
 *
 */
public class Resource implements SwaggerModel {

    private String path;
    private Parameter pathParameter;
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

    public Parameter getPathParameter() {
        return pathParameter;
    }

    public void setPathParameter(Parameter pathParameter) {
        this.pathParameter = pathParameter;
    }
}
