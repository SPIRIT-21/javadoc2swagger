package com.spirit21.swagger.converter.datatype;

import com.spirit21.swagger.converter.models.DataType;

/**
 * 
 * @author dsimon
 *
 */
public class DateHandler implements TypeHandler {

    @Override
    public DataType getType(String className) {
        if (className.equals("Date") || className.equals("java.util.Date")) {
            return new DataType("string", "date");
        } else if (className.equals("LocalDateTime") || className.equals("java.time.LocalDateTime")) {
            return new DataType("string", "date-time");
        } else if (className.equals("LocalDate") || className.equals("java.time.LocalDate")) {
            return new DataType("string", "date");
        }
        return null;
    }
}
