package com.spirit21.swagger.converter.writers;

import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import com.spirit21.swagger.converter.models.Definition;
import com.spirit21.swagger.converter.models.Parameter;

/**
 * 
 * @author dsimon
 *
 */
public class ParameterWriter extends AbstractWriter {

    public ParameterWriter(Log log) {
        super(log);
    }

    /**
     * Creates parameters JSON
     * 
     * @param parameters
     *            list of {@link Parameter} objects
     * @return JSON for all parameters
     */
    public JSONArray mapParameters(List<Parameter> parameters) {
        JSONArray parametersJson = new JSONArray();
        if (parameters != null) {
            parameters.forEach(e -> parametersJson.put(mapParameter(e)));
        }
        return parametersJson;
    }

    /**
     * Creates JSON for one parameter
     * 
     * @param parameter
     *            {@link Parameter} object
     * @return JSON for one parameter
     */
    private JSONObject mapParameter(Parameter parameter) {
        JSONObject obj = new JSONObject();
        String name = parameter.getName();
        String type = parameter.getType();
        String format = parameter.getFormat();
        String location = parameter.getLocation();
        String description = parameter.getDescription();
        Boolean required = parameter.getRequired();
        String defaultValue = parameter.getDefaultValue();
        Definition definition = parameter.getDefinition();
        if (name != null) {
            obj.put("name", name);
        }
        if (location != null) {
            obj.put("in", location);
        }
        if (description != null) {
            obj.put("description", description);
        }
        if (required != null) {
            obj.put("required", required);

        }
        if (defaultValue != null) {
            obj.put("default", defaultValue);

        }
        if (definition != null && format == null) {
            JSONObject ref = new JSONObject();
            ref.put("$ref", appendDefinitionPrefix(definition.getClassName()));
            obj.put("schema", ref);
        } else if (definition != null && format.equals("array")) {
            JSONObject items = new JSONObject();
            items.put("$ref", appendDefinitionPrefix(definition.getClassName()));
            JSONObject schema = new JSONObject();
            schema.put("type", format);
            schema.put("items", items);
            obj.put("schema", schema);
        } else if (format != null && format.equals("array")) {
            JSONObject items = new JSONObject();
            items.put("type", type);
            obj.put("type", format);
            obj.put("items", items);
        } else {
            obj.put("format", format);
            obj.put("type", type);
        }
        return obj;
    }
}
