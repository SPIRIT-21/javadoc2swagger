package com.spirit21.swagger.converter.parsers;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

import com.spirit21.swagger.converter.loader.ClassLoader;
import com.spirit21.swagger.converter.models.Definition;
import com.spirit21.swagger.converter.models.Tag;

/**
 * 
 * @author dsimon
 *
 */
public class TagParser extends AbstractParser {

    public TagParser(Log log, ClassLoader loader, List<Tag> tags, List<Definition> definitions) {
        super(log, loader, tags, definitions);
    }

    /**
     * Get all needed tags by a path and creates them if necessary
     * 
     * @param path
     * @return List of Tags
     */
    public List<Tag> generateTags(String path) {
        List<Tag> localTags = new ArrayList<>();
        String[] pathSplit = path.substring(1).split("/");
        String tagName = "";
        for (int i = 0; i < pathSplit.length; i++) {
            tagName = tagName.concat(pathSplit[i]);
            Tag tag = new Tag(tagName);
            if (!isTagContained(tagName)) {
                tags.add(tag);
            }
            localTags.add(tag);
        }
        if (!localTags.isEmpty()) {
            return localTags;
        } else {
            return null;
        }
    }

    /**
     * Checks if a tag is already contained in tags list
     * 
     * @param tagName
     * @return
     */
    private boolean isTagContained(String tagName) {
        for (Tag tag : tags) {
            if (tag.getName().equals(tagName)) {
                return true;
            }
        }
        return false;
    }
}
