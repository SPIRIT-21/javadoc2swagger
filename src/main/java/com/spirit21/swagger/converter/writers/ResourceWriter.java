package com.spirit21.swagger.converter.writers;

import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.json.JSONObject;

import com.spirit21.swagger.converter.models.Resource;

/**
 * 
 * @author dsimon
 *
 */
public class ResourceWriter extends AbstractWriter {
    OperationWriter operationWriter = new OperationWriter(log);

    public ResourceWriter(Log log) {
        super(log);
    }

    /**
     * Creates JSON for multiple resources
     * 
     * @param resources
     *            list of {@link Resource} objects
     * @return JSON for resources
     */
    public JSONObject mapResources(List<Resource> resources) {
        JSONObject resourcesJson = new JSONObject();
        resources.forEach(e -> resourcesJson.put(e.getPath(),
                operationWriter.mapOperations(e.getOperations(), e.getPathParameter())));
        return resourcesJson;
    }
}
