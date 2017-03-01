package com.spirit21.swagger.converter.writers;

import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.json.JSONObject;

import com.spirit21.swagger.converter.models.Definition;
import com.spirit21.swagger.converter.models.Response;

/**
 * 
 * @author dsimon
 *
 */
public class ResponseWriter extends AbstractWriter {
    public ResponseWriter(Log log) {
        super(log);
    }

    /**
     * Creates JSON for multiple responses
     * 
     * @param responses
     * @return
     */
    public JSONObject mapResponses(List<Response> responses) {
        JSONObject responsesJson = new JSONObject();
        if (responses != null) {
            responses.forEach(e -> responsesJson.put(String.valueOf(e.getStatusCode()), mapResponse(e)));
        }
        return responsesJson;
    }

    /**
     * Creates JSON for a single response
     * 
     * @param response
     * @return
     */
    private JSONObject mapResponse(Response response) {
        JSONObject obj = new JSONObject();
        String description = response.getMessage();
        String responseType = response.getResponseType();
        Definition definition = response.getDefinition();
        if (description != null) {
            obj.put("description", description);
        }
        if (responseType != null && definition != null && responseType.equals("array")) {
            JSONObject schema = new JSONObject();
            schema.put("type", responseType);
            JSONObject ref = new JSONObject();
            ref.put("$ref", appendDefinitionPrefix(definition.getClassName()));
            schema.put("items", ref);
            obj.put("schema", schema);
        } else if (definition != null && responseType == null) {
            JSONObject schema = new JSONObject();
            schema.put("$ref", appendDefinitionPrefix(definition.getClassName()));
            obj.put("schema", schema);
        } else if (responseType != null) {
            obj.put("type", responseType);
        }
        return obj;
    }
}
