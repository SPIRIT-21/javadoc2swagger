package com.spirit21.swagger.converter.writers;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import com.spirit21.swagger.converter.models.Operation;
import com.spirit21.swagger.converter.models.Parameter;

/**
 * 
 * @author dsimon
 *
 */
public class OperationWriter extends AbstractWriter {
    TagWriter tagWriter = new TagWriter(log);
    ParameterWriter parameterWriter = new ParameterWriter(log);
    ResponseWriter responseWriter = new ResponseWriter(log);

    public OperationWriter(Log log) {
        super(log);
    }

    /**
     * Creates operations JSON
     * 
     * @param operations
     * @param pathParam
     * @return
     */
    public JSONObject mapOperations(List<Operation> operations, Parameter pathParam) {
        JSONObject operationsJson = new JSONObject();
        if (operations != null) {
            operations.forEach(e -> operationsJson.put(e.getMethod(), mapOperation(e, pathParam)));
        }
        return operationsJson;
    }

    /**
     * Creates JSON for an operation
     * 
     * @param operation
     * @param pathParam
     * @return
     */
    private JSONObject mapOperation(Operation operation, Parameter pathParam) {
        JSONObject obj = new JSONObject();
        String description = operation.getDescription();
        String summary = operation.getSummary();
        String operationId = operation.getOperationId();
        List<String> produces = operation.getProduces();
        List<String> consumes = operation.getConsumes();
        if (description != null) {
            obj.put("description", description);
        }
        if (summary != null) {
            obj.put("summary", summary);
        }
        if (operationId != null) {
            obj.put("operationId", operationId);
        }
        if (produces != null && !produces.isEmpty()) {
            JSONArray array = new JSONArray();
            produces.forEach(array::put);
            obj.put("produces", array);
        }
        if (consumes != null && !consumes.isEmpty()) {
            JSONArray array = new JSONArray();
            consumes.forEach(array::put);
            obj.put("consumes", array);
        }
        obj.put("tags", tagWriter.mapTags(operation.getTags()));
        List<Parameter> parameters = operation.getParameters();
        if (pathParam != null) {
            if (parameters == null) {
                parameters = new ArrayList<>();
            }
            parameters.add(pathParam);
        }
        if (parameters != null && !parameters.isEmpty()) {
            obj.put("parameters", parameterWriter.mapParameters(parameters));
        }
        obj.put("responses", responseWriter.mapResponses(operation.getResponses()));
        return obj;
    }
}
