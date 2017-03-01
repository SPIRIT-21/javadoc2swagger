package com.spirit21.swagger.converter.models;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author dsimon
 *
 */
public class Swagger implements SwaggerModel {

    private String description;
    private String version;
    private String title;
    private String host;
    private String basePath;
    private String fileName;
    private List<Tag> tags;
    private List<Resource> resources;

    public Swagger() {
        this.resources = new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public List<Resource> addResource(Resource resource) {
        resources.add(resource);
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
