package com.spirit21.swagger.converter.loader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

/**
 * 
 * @author dsimon
 *
 */
public class ClassLoader {
    private URLClassLoader loader;

    /**
     * Initializes the ClassLoader
     * 
     * @param project
     * @param log
     * @throws ClassLoaderException
     */
    public ClassLoader(MavenProject project) throws ClassLoaderException {
        try {
            List<?> runtimeClasspathElements = project.getRuntimeClasspathElements();
            URL[] runtimeUrls = new URL[runtimeClasspathElements.size()];
            for (int i = 0; i < runtimeClasspathElements.size(); i++) {
                String element = (String) runtimeClasspathElements.get(i);
                runtimeUrls[i] = new File(element).toURI().toURL();
            }
            loader = new URLClassLoader(runtimeUrls, Thread.currentThread().getContextClassLoader());
        } catch (MalformedURLException | DependencyResolutionRequiredException e) {
            throw new ClassLoaderException("Error initializing the ClassLoader!", e);
        }
    }

    /**
     * Loads a class with the {@link URLClassLoader}
     * 
     * @param name
     * @return loaded class
     * @throws ClassNotFoundException
     */
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loader.loadClass(name);
    }
}
