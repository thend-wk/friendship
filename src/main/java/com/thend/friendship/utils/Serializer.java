package com.thend.friendship.utils;

import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.TypeReference;


public class Serializer {
	
	private static final Log logger = LogFactory.getLog(Serializer.class);
	
	public static ObjectMapper json = new ObjectMapper();
	
	private static final JsonFactory jsonFactory = new JsonFactory();
	
	static {
        json.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
		.configure(Feature.ALLOW_SINGLE_QUOTES, true)
		.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false)
		.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    }
	
	public static String toJson(Object pojo) {
		return toJson(pojo, false);
	}
	
	public static String toJson(Object pojo, boolean prettyPrint) {
        try {
            StringWriter sw = new StringWriter();
            JsonGenerator jg = jsonFactory.createJsonGenerator(sw);
            if (prettyPrint) {
            	jg.useDefaultPrettyPrinter();
            }
            json.writeValue(jg, pojo);
            return sw.toString();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
	}
	
    public static <T> T fromJson(String jsonAsString, Class<T> pojoClass) {
	    try {
	    	return json.readValue(jsonAsString, pojoClass);
	    } catch (Exception e) {
	    	logger.error(e.getMessage(), e);
	    }
	    return null;
    }

	public static <T> T fromJson(String jsonAsString,TypeReference<T> typeRef){
	    try {
	    	return json.readValue(jsonAsString, typeRef);
	    } catch (Exception e) {
	    	logger.error(e.getMessage(), e);
	    }
	    return null;
	}
}
