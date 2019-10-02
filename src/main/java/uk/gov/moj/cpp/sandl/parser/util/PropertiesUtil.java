package uk.gov.moj.cpp.sandl.parser.util;

import uk.gov.moj.cpp.sandl.parser.RotaXMLParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {

    public static final Map<String, String> xmlProperties = loadXmlProperties();

    private static Map<String, String> loadXmlProperties() {
        final Properties props = new Properties();
        final Map<String, String> propsMap = new HashMap<>();
        try (InputStream input = RotaXMLParser.class.getClassLoader().getResourceAsStream("rotaXml.properties")) {

            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (final String key : props.stringPropertyNames()) {
            propsMap.put(props.getProperty(key), key);
        }

        return propsMap;
    }


}
