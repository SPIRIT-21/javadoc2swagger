package com.spirit21.swagger.converter.datatype;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

/**
 * 
 * @author dsimon
 *
 */
public class HttpMethodAnnotationHandler {
    /**
     * Checks if the given class mathes the JAX-RS annotations @GET, @POST, @PUT
     * or @DELETE
     * 
     * @param cls
     * @return null or http method in lower case
     */
    public String getHttpMethodAnnotationType(Class<?> cls) {
        if (cls.equals(GET.class)) {
            return "get";
        } else if (cls.equals(POST.class)) {
            return "post";
        } else if (cls.equals(PUT.class)) {
            return "put";
        } else if (cls.equals(DELETE.class)) {
            return "delete";
        }
        return null;
    }
}
