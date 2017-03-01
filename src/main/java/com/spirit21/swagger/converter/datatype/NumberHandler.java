package com.spirit21.swagger.converter.datatype;

import com.spirit21.swagger.converter.models.DataType;

/**
 * 
 * @author dsimon
 *
 */
public class NumberHandler implements TypeHandler {

    @Override
    public DataType getType(String className) {
        if (className.equals("long") || className.equals("Long") || className.equals("java.lang.Long")) {
            return new DataType("number", "long");
        } else if (className.equals("int") || className.equals("Integer") || className.equals("java.lang.Integer")) {
            return new DataType("number");
        } else if (className.equals("float") || className.equals("Float") || className.equals("java.lang.Float")) {
            return new DataType("number", "float");
        } else if (className.equals("double") || className.equals("Double") || className.equals("java.lang.Double")) {
            return new DataType("number", "double");
        }
        return null;
    }
}
