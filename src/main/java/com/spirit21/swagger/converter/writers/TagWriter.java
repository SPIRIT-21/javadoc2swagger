package com.spirit21.swagger.converter.writers;

import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import com.spirit21.swagger.converter.models.Tag;

/**
 * 
 * @author dsimon
 *
 */
public class TagWriter extends AbstractWriter {
    public TagWriter(Log log) {
        super(log);
    }

    /**
     * Creates JSON for the global tag list
     * 
     * @param tags
     *            list of {@link Tag} objects
     * @return JSON for tags
     */
    public JSONArray mapTagDefinitions(List<Tag> tags) {
        JSONArray tagsJson = new JSONArray();
        for (Tag tag : tags) {
            String name = tag.getName();
            String description = tag.getDescription();
            JSONObject obj = new JSONObject();
            obj.put("name", name);
            if (description != null) {
                obj.put("description", description);
            }
            tagsJson.put(obj);
        }
        return tagsJson;
    }

    /**
     * Creates JSON for a local tag lost
     * 
     * @param tags
     *            list of {@link Tag} objects
     * @return JSON for tags
     */
    public JSONArray mapTags(List<Tag> tags) {
        JSONArray tagsJson = new JSONArray();
        if (tags != null) {
            tags.forEach(e -> tagsJson.put(e.getName()));
        }
        return tagsJson;
    }
}
