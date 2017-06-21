# Maven plugin for generating Swagger from JAX-RS and Javadoc comments
This Maven Plugin is generating a Swagger API documentation for JAX-RS based Java servers. Additional information that is not contained in JAX-RS annotations is placed in the Javadoc comments.

## Example
A sample Maven project using the `javadoc2swagger-maven-plugin` is available here:

https://github.com/SPIRIT-21/javadoc2swagger-examples

## Introduction
The normal way of creating a Swagger documentation from Java source code would be <a href="https://github.com/swagger-api/swagger-core">Swagger Core</a> annotations. But there are some issues with them.

First, Swagger annotations are compiled into the JAR / WAR of your server and consume disk space. Second, they don't look good. Third, there are redundant data if you are both documenting your code with Javadoc and using Swagger Annotations, so there is more work for developers to maintain them.

With this plugin it is possible to place Swagger information in the Javadoc comments to generate a Swagger documentation.

## Configuration
Include the plugin to your POM:
```xml
<plugin>
	<groupId>com.spirit21</groupId>
	<artifactId>javadoc2swagger-maven-plugin</artifactId>
	<version>VERSION</version>
	<executions>
		<execution>
			<phase>compile</phase>
			<goals>
				<goal>generateswagger</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```

If you want the Swagger file to be added to your JAR / WAR, use the `maven-jar-plugin` / `maven-war-plugin`. The following example adds all JSON files located in the `target/swagger` folder to the root directory of the created WAR:

```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-war-plugin</artifactId>
	<version>3.0.0</version>
	<configuration>
		<webResources>
			<resource>
				<directory>target/swagger</directory>
				<includes>
					<include>*.json</include>
				</includes>
			</resource>
		</webResources>
	</configuration>
</plugin>
```
	
## Usage
To execute the plugin, initiate the Maven compile phase, e.g. by executing `mvn clean install` in the command line.

### Basic API information
For a valid Swagger documentation, you need to at least provide a title. Place your basic API information anywhere in your Javadoc comments like in this example:

```java
/**
 * @apiTitle API title
 * @apiVersion version1
 * @apiDescription API description
 * @apiHost localhost:8080
 * @apiBasePath /example/api/v1
 * @fileName swagger-file-name.json
 */
```

### Resources
For defining a resource, a path must be provided. When using a path parameter, its format must be defined. This information needs to be placed in the Javadoc of the class definition, like in the following example:

```java
/**
 * @path /type/{id}
 * @pathParam id @type number @format long
 */
public class ...
```

When using multiple path parameters, please ensure that the `@format` tag is always given. If you don't want to specify a format, leave the text behind the tag empty. Example:

```java
/**
 * @path /type/{id}/{childId}
 * @pathParam id @type string @format
 * @pathParam childId @type number @format long
 */
public class ...
```
	
### Operations
An operation must be defined in the same file as its resource! Otherwise the operation will be ignored because it cannot be assigned to a resource.

The plugin detects the JAX-RS HTTP-method annotations and the `@Produces` / `@Consumes` annotations by itself.

### Parameters
Parameters are obtained by the parameter list of the java function. Use the `@QueryParam` annotation for query parameters. If you don't want a parameter to be included in the Swagger documentation, paste the following comment: `/* @swagger:ignore */`. Following example ignores the `date` parameter:

```java
public Response updateType(Type type, /* @swagger:ignore */ Date date) { ...
```
	
If you want to provide a description for a parameter, use the built-in `@param` Javadoc tag, followed by the name and finally the description.

### Responses
Responses have to be defined in the Javadoc code. Use the tags `@responseCode` and `@responseMessage`. Provide a schema of your response type with the tag `@responseSchema` followed by a link tag pointing to the class with the schema. Use the tag `@responseType` followed by `array` if an array of object is returned. Following example uses all possible tags:

```java
/**
 * @responseCode 201
 * @responseMessage Types were returned successfully
 * @responseSchema {@link Type}
 * @responseType array
 * 
 * @responseCode 400
 * @responseMessage An error occurred while validation
 * @responseSchema {@link ErrorCode}
 */
```

## Troubleshooting
Following section lists some errors that might occure when executing the plugin and how to fix them.

* "You need to provide information about your API! Title is required."
  * Solution: Add an API information block in the Javadoc with at least a title.
* "Multiple API information Javadoc sections found!"
  * Solution: Remove duplicate API information blocks.
* "Error loading Class *className*"
  * An error occured while loading the specified class.
  * Solution: The plugin is only able to 'see' the source code from the project in which it is executed. When a class from another project is not being found, simply pass the project to the plugin as dependency, like in the following example:
  
```xml
<plugin>
	<groupId>com.spirit21</groupId>
	<artifactId>javadoc2swagger-maven-plugin</artifactId>
	...
	<dependencies>
		<dependency>
			<groupId>GROUP-ID</groupId>
			<artifactId>ARTIFACT-ID</artifactId>
			<version>VERSION</version>
		</dependency>
	</dependencies>
</plugin>
```
If the error persists, you can ignore the whole Java file by placing the following somewhere in the Javadoc comments: `@swagger:ignore_javafile`. This doesn't fix the problem but allows you to still create Swagger for all your other Java files.
