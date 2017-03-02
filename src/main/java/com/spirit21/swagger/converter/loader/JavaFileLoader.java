package com.spirit21.swagger.converter.loader;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.spirit21.swagger.converter.models.JavaFile;
import com.spirit21.swagger.converter.models.Method;

/**
 * Provides methods to load java files from a Maven project
 * 
 * @author dsimon
 *
 */
public class JavaFileLoader extends AbstractLoader {

    public JavaFileLoader(Log log) {
        super(log);
    }

    /**
     * Loads all java files from a Maven project and writes each of them to a
     * list of {@link JavaFile} objects
     * 
     * @param project
     *            Maven Project
     * @return List of java files
     * @throws JavaFileLoadException
     *             An error occured while loading the Java files
     */
    public List<JavaFile> getJavaFiles(MavenProject project) throws JavaFileLoadException {
        try {
            List<Path> javaSourceFiles = new ArrayList<>();
            List<?> rootDirectories = project.getCompileSourceRoots();
            for (Object raw : rootDirectories) {
                String rootDirectory = String.class.cast(raw);
                javaSourceFiles.addAll(getJavaFileNames(Paths.get(rootDirectory)));
            }
            return getInformationFromJavaFiles(javaSourceFiles);
        } catch (IOException ex) {
            throw new JavaFileLoadException("Error while loading the java files!", ex);
        }
    }

    /**
     * Finds all pathes to Java files in the root directory recursively and
     * writes them to a list
     * 
     * @param fileNames
     *            list of pathes
     * @param dir
     *            root directory
     * @return list of pathes
     * @throws IOException
     */
    private List<Path> getJavaFileNames(Path dir) throws IOException {
        List<Path> fileNames = new ArrayList<>();
        DirectoryStream<Path> stream = null;
        try {
            stream = Files.newDirectoryStream(dir);
            for (Path path : stream) {
                if (path.toFile().isDirectory()) {
                    fileNames.addAll(getJavaFileNames(path));
                } else if (path.getFileName().toString().endsWith(".java")) {
                    fileNames.add(path);
                }
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return fileNames;
    }

    /**
     * Reads the given Java files and writes javadoc, annotations and imports
     * for each file to an object
     * 
     * @param files
     *            pathes to Java files
     * @return list of list of strings
     * @throws IOException
     */
    private List<JavaFile> getInformationFromJavaFiles(List<Path> files) throws IOException {
        ClassJavadocLoader classJavadocLoader = new ClassJavadocLoader(log);
        ClassAnnotationLoader classAnnotationLoader = new ClassAnnotationLoader(log);
        MethodLoader methodLoader = new MethodLoader(log);
        ApiJavadocLoader apiJavadocLoader = new ApiJavadocLoader(log);
        ImportLoader importLoader = new ImportLoader(log);
        List<JavaFile> javaFiles = new ArrayList<>();
        for (Path file : files) {
            String fileString = fileAsString(Files.readAllLines(file));
            JavaFile javaFile = new JavaFile();
            String packageName = getPackageNameFromFile(fileString);
            List<String> imports = importLoader.getImportsFromFile(fileString);
            List<Method> methods = methodLoader.getMethodsFromJavaFile(fileString);
            String apiJavadoc = apiJavadocLoader.getApiJavadocFromJavaFile(fileString);
            List<String> classAnnotations = classAnnotationLoader.getClassAnnotationsFromJavaFile(fileString);
            String classJavadoc = classJavadocLoader.getClassJavadocFromJavaFile(fileString);
            if (imports != null) {
                javaFile.setImports(imports);
            }
            if (classJavadoc != null) {
                javaFile.setClassJavadoc(removeJavadocCharactersFromString(classJavadoc));
            }
            if (classAnnotations.isEmpty()) {
                javaFile.setClassAnnotations(classAnnotations);
            }
            if (apiJavadoc != null) {
                javaFile.setApiJavadoc(removeJavadocCharactersFromString(apiJavadoc));
            }
            javaFile.setPackageName(packageName);
            javaFile.setFileName(file.getFileName().toString());
            javaFile.setFunctions(methods);
            javaFiles.add(javaFile);
        }
        return javaFiles;
    }

    /**
     * Gets the package name of a java file
     * 
     * @param fileString
     *            file as string
     * @return package or null
     */
    private String getPackageNameFromFile(String fileString) {
        Pattern pattern = Pattern.compile("package [^;]*;");
        Matcher matcher = pattern.matcher(fileString);
        if (matcher.find()) {
            return fileString.substring(matcher.start() + 8, matcher.end() - 1);
        }
        return null;
    }

    /**
     * Converts the content of a file from line by line to a single string.
     * 
     * @param file
     * @return string
     */
    private String fileAsString(List<String> file) {
        String ret = "";
        for (String line : file) {
            ret = ret.concat(line + " ");
        }
        return ret;
    }
}
