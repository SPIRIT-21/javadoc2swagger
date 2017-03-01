package com.spirit21.swagger.converter.datatype;

import com.spirit21.swagger.converter.parsers.ParserException;

/**
 * 
 * @author dsimon
 *
 */
public class MediaTypeHandler {
    public String getConsumesOrProducesByEnum(String mediaType) throws ParserException {
        switch (mediaType) {
        case "APPLICATION_JSON":
            return "application/json";
        case "APPLICATION_XML":
            return "application/xml";
        case "APPLICATION_ATOM_XML":
            return "application/atom+xml";
        case "APPLICATION_XHTML_XML":
            return "application/xhtml+xml";
        case "APPLICATION_SVG_XML":
            return "application/svg+xml";
        case "APPLICATION_FORM_URLENCODED":
            return "application/x-www-form-urlencoded";
        case "MULTIPART_FORM_DATA":
            return "multipart/form-data";
        case "APPLICATION_OCTET_STREAM":
            return "application/octet-stream";
        case "TEXT_PLAIN":
            return "text/plain";
        case "TEXT_XML":
            return "text/xml";
        case "TEXT_HTML":
            return "text/html";
        default:
            throw new ParserException("Unhandled Media DataType: " + mediaType);
        }
    }
}
