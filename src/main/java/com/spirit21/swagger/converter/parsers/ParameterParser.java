package com.spirit21.swagger.converter.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;

import com.spirit21.swagger.converter.Regex;
import com.spirit21.swagger.converter.datatype.DataTypeFactory;
import com.spirit21.swagger.converter.loader.ClassLoader;
import com.spirit21.swagger.converter.models.DataType;
import com.spirit21.swagger.converter.models.Definition;
import com.spirit21.swagger.converter.models.Parameter;
import com.spirit21.swagger.converter.models.Tag;

/**
 * 
 * @author dsimon
 *
 */
public class ParameterParser extends AbstractParser {

	private Map<String, String> descriptionMap;

	private Map<String, String> defaultValueMap;

	public ParameterParser(Log log, ClassLoader loader, List<Tag> tags, List<Definition> definitions) {
		super(log, loader, tags, definitions);
		this.defaultValueMap = new HashMap<>();

	}

	/**
	 * Searches for parameters in function header.
	 * 
	 * @param header
	 *            method header
	 * @param imports
	 *            imports of the java file
	 * @param javadoc
	 *            javadoc section
	 * @param fileName
	 *            java file name
	 * @param packageName
	 *            package name of current java file
	 * @return parameters
	 * @throws ParserException
	 *             Error while the parsing process
	 */
	public List<Parameter> findParametersInMethodHeader(String header, List<String> imports, String javadoc,
			String fileName, String packageName) throws ParserException {
		descriptionMap = getParameterDescriptionMap(javadoc);
		String regex = "\\((?s:.)*\\)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(header);
		while (matcher.find()) {
			String insideBrackets = header.substring(matcher.start() + 1, matcher.end() - 1);
			String[] parameters = insideBrackets.split(",");
			if (parameters != null && parameters.length != 0) {
				List<Parameter> retParameters = new ArrayList<>();
				for (String param : parameters) {
					if (!param.contains("/* @swagger:ignore */")) {
						Parameter parameter = getParameter(param, imports, fileName, packageName);
						if (parameter != null) {
							retParameters.add(parameter);
						}
					}
				}
				if (!retParameters.isEmpty()) {
					return retParameters;
				} else {
					return null;
				}
			}
		}
		return null;
	}

	/**
	 * Extracts a {@link Parameter} object from a parameter declaration in a method
	 * header
	 * 
	 * @param paramUnformatted
	 *            parameter with white spaces and annotations
	 * @param imports
	 *            imports of the java file
	 * @param fileName
	 *            java file name
	 * @return parameter or null
	 * @throws ParserException
	 *             Error while the parsing process
	 */
	private Parameter getParameter(String paramUnformatted, List<String> imports, String fileName, String packageName)
			throws ParserException {

		DataTypeFactory typeHandler = new DataTypeFactory();
		DefinitionParser definitionParser = new DefinitionParser(log, loader, tags, definitions);
		Boolean isQueryParam = paramUnformatted.matches(".*@QueryParam\\(\"[^\"]+\"\\).*");
		Boolean isDefaultValue = paramUnformatted.matches(".*@DefaultValue\\(\"[^\"]+\"\\).*");

		String param = paramUnformatted.replaceAll("[\\s]*" + Regex.ANNOTATION + "[\\s]*", "").trim();

		List<String> defaultValue = new ArrayList<String>();

		Matcher m = Pattern.compile(Regex.GETMETHODHEADERPARAMETER).matcher(paramUnformatted);
		while (m.find() && isDefaultValue) {
			List<String> paramUnformattedTest = new ArrayList<String>();
			paramUnformattedTest.add(m.group());
			for (int i = 0; i < paramUnformattedTest.size(); i++) {
				if (paramUnformattedTest.get(i).contains("@DefaultValue")) {
					createMapFromParameters(paramUnformatted);
					defaultValue.add(paramUnformattedTest.get(i));
				}
			}
		}
		Matcher matchDef = Pattern.compile(Regex.GETMETHODHEADERPARAMETERINSIDE).matcher(defaultValue.toString());
		while (matchDef.find()) {
			defaultValue.removeAll(defaultValue);
			defaultValue.add(matchDef.group());
		}
		if (!param.isEmpty()) {
			String[] split = param.split(" ");
			String className = split[0];
			String name = split[1];
			Parameter parameter = new Parameter();
			parameter.setName(name);
			DataType typeObject;
			if (className.matches("[a-zA-Z0-9]+<[a-zA-Z0-9]+>")) {
				String[] genericSplit = className.split("<");
				String type = genericSplit[0];
				String genericType = genericSplit[1].substring(0, genericSplit[1].length() - 1);
				if (type.equals("List") || type.equals("Set") || type.equals("Collection")) {
					typeObject = typeHandler.getDataType(genericType);
					typeObject.setFormat("array");
				} else {
					throw new ParserException(
							"DataType " + type + " not supported using generics! Use List, Collection or Set.");
				}
			} else {
				typeObject = typeHandler.getDataType(className);
			}
			String type = typeObject.getType();
			String format = typeObject.getFormat();
			if (type.startsWith("#") && !isQueryParam) {
				// reference -> body parameter
				parameter.setLocation("body");
				String title = definitionParser.createDefinitionIfNotExists(type, imports, fileName, packageName);
				if (title != null) {
					Definition definition = definitionParser.getDefinitionByClassName(title);
					parameter.setDefinition(definition);
				}
				// body parameters are always required
				parameter.setRequired(true);
			} else if (!isQueryParam) {
				parameter.setLocation("body");
				parameter.setType(type);
				parameter.setRequired(true);

			} else {
				parameter.setLocation("query");
				parameter.setName(getNameFromQueryParamAnnotation(paramUnformatted));
				parameter.setType(type);
			}
			if (isDefaultValue) {
				parameter.setLocation("query");
				// parameter.setType(type);
				for (int i = 0; i < defaultValue.size(); i++) {
					parameter.setDefaultValue(defaultValue.get(i));
					parameter.setRequired(false);
				}
				parameter.setFormat(format);
				String description = descriptionMap.get(name);
				if (this.defaultValueMap.get(name) != null) {
					description = description
							+ ". If parameter is not set, parameter will be set to the default Value \""
							+ this.defaultValueMap.get(name) + "\"";
				}
				parameter.setDescription(description);
			} else {
				parameter.setFormat(format);
				String description = descriptionMap.get(name);
				parameter.setDescription(description);
			}
			return parameter;
		}
		return null;
	}

	/**
	 * Gets the query parameter name
	 * 
	 * @param paramUnformatted
	 *            unformatted query parameter annotation
	 * @return formatted string
	 */
	private String getNameFromQueryParamAnnotation(String paramUnformatted) {
		String regex = "@QueryParam\\(\"[\\w]+\"";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(paramUnformatted);
		if (matcher.find()) {
			return paramUnformatted.substring(matcher.start() + 13, matcher.end() - 1);
		}
		return null;
	}

	/**
	 * Initializes the HashMap for a parameter and its description
	 * 
	 * @param javadoc
	 *            javadoc section
	 * @return Map
	 */
	private Map<String, String> getParameterDescriptionMap(String javadoc) {
		Map<String, String> map = new HashMap<>();
		String paramRegex = "@param [a-zA-Z0-9]+ " + Regex.DESCRIPTION;
		Pattern pattern = Pattern.compile(paramRegex);
		Matcher matcher = pattern.matcher(javadoc);
		while (matcher.find()) {
			String section = javadoc.substring(matcher.start() + 7, matcher.end());
			String[] parts = section.split(" ");
			String description = "";
			for (int i = 1; i < parts.length; i++) {
				description = description.concat(parts[i] + " ");
			}
			map.put(parts[0], removeUnwantedCharactersFromJavadocDescription(description));
		}
		return map;
	}

	/**
	 * Takes the parameter String and extracts the values of the parameter and of
	 * the default value and maps them together
	 * 
	 * @param paramUnformatted
	 * @return
	 */
	private Map<String, String> createMapFromParameters(String paramUnformatted) {

		Boolean isDefaultValue = paramUnformatted.matches(".*@DefaultValue\\(\"[^\"]+\"\\).*");
		Boolean isQueryParam = paramUnformatted.matches(".*@QueryParam\\(\"[^\"]+\"\\).*");

		List<String> defaultValue = new ArrayList<String>();
		List<String> defaultValueKey = new ArrayList<String>();
		List<String> defaultValueValue = new ArrayList<String>();

		List<String> queryParam = new ArrayList<String>();
		List<String> queryParamKey = new ArrayList<String>();
		List<String> queryParamValue = new ArrayList<String>();

		Matcher m = Pattern.compile(Regex.GETMETHODHEADERPARAMETER).matcher(paramUnformatted);
		while (m.find() && isDefaultValue) {
			List<String> paramUnformattedTest = new ArrayList<String>();
			paramUnformattedTest.add(m.group());
			for (int i = 0; i < paramUnformattedTest.size(); i++) {
				if (paramUnformattedTest.get(i).contains("@DefaultValue")) {

					defaultValue.add(paramUnformattedTest.get(i));
				}
			}
		}

		Matcher matchDefKey = Pattern.compile("@([a-zA-Z0-9])+").matcher(defaultValue.toString());
		while (matchDefKey.find()) {
			defaultValueKey.removeAll(defaultValueKey);
			defaultValueKey.add(matchDefKey.group());
		}

		Matcher matchDefValueValue = Pattern.compile(Regex.GETMETHODHEADERPARAMETERINSIDE)
				.matcher(defaultValue.toString());
		while (matchDefValueValue.find()) {
			defaultValueValue.removeAll(defaultValueValue);
			defaultValueValue.add(matchDefValueValue.group());
		}
		Matcher m5 = Pattern.compile(Regex.GETMETHODHEADERPARAMETER).matcher(paramUnformatted);
		while (m5.find() && isQueryParam) {
			List<String> paramUnformattedTest = new ArrayList<String>();
			paramUnformattedTest.add(m5.group());
			for (int i = 0; i < paramUnformattedTest.size(); i++) {
				if (paramUnformattedTest.get(i).contains("@QueryParam")) {
					queryParam.add(paramUnformattedTest.get(i));
				}
			}
		}
		Matcher matchQueryParamKey = Pattern.compile("@([a-zA-Z0-9])+").matcher(queryParam.toString());
		while (matchQueryParamKey.find()) {
			queryParamKey.removeAll(queryParamKey);
			queryParamKey.add(matchQueryParamKey.group());
		}

		Matcher matchQueryParamValue = Pattern.compile(Regex.GETMETHODHEADERPARAMETERINSIDE)
				.matcher(queryParam.toString());
		while (matchQueryParamValue.find()) {
			queryParamValue.removeAll(queryParamValue);
			queryParamValue.add(matchQueryParamValue.group());
		}

		defaultValueMap.put(queryParamValue.toString().replace("[", "").replace("]", ""),
				defaultValueValue.toString().replace("[", "").replace("]", ""));
		return defaultValueMap;
	}
}
