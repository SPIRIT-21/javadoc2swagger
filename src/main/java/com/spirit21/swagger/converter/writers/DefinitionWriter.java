package com.spirit21.swagger.converter.writers;

import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import com.spirit21.swagger.converter.models.Definition;

/**
 * 
 * @author dsimon
 *
 */
public class DefinitionWriter extends AbstractWriter {
    PropertyWriter propertyWriter = new PropertyWriter(log);

    public DefinitionWriter(Log log) {
        super(log);
    }

    /**
     * Creates JSON for all definitions
     * 
     * @param definitions
     * @return
     */
    public JSONObject mapDefinitions(List<Definition> definitions) {
        JSONObject definitionsJson = new JSONObject();
        if (definitions != null && !definitions.isEmpty()) {
            definitions.forEach(e -> definitionsJson.put(e.getClassName(), mapDefinition(e)));
        }
        return definitionsJson;
    }

    /**
     * Creates JSON for one definition
     * 
     * @param definition
     * @return
     */
    private JSONObject mapDefinition(Definition definition) {
        JSONObject obj = new JSONObject();
        List<String> required = definition.getRequired();
        if (required != null && !required.isEmpty()) {
            JSONArray reqJson = new JSONArray();
            for (String req : required) {
                reqJson.put(req);
            }
            obj.put("required", reqJson);
        }
        obj.put("type", "object");
        obj.put("properties", propertyWriter.mapProperties(definition.getProperties()));
        return obj;
    }
}
