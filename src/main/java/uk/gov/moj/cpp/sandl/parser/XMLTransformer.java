package uk.gov.moj.cpp.sandl.parser;

import static javax.xml.stream.XMLInputFactory.newInstance;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.apache.commons.lang3.StringUtils.removeEnd;

import uk.gov.moj.cpp.sandl.entity.Assignment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

public class XMLTransformer {

    private final String TAG_SEPATOR = ".";
    private final Map<String, String> propsMap = loadProperties();

    public List<Map<String, Object>> parse(final byte[] content) {
        final List<Map<String, Object>> resultMap = new ArrayList<>();
        String xpath = "";
        Map<String, Object> record = new HashMap<>();
        boolean firstElement = true;

        try {
            XMLInputFactory factory = newInstance();
            XMLEventReader eventReader = factory.createXMLEventReader(new ByteArrayInputStream(content));

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                switch (event.getEventType()) {

                    case XMLStreamConstants.START_ELEMENT: {
                        String qName = event.asStartElement().getName().getLocalPart();

                        if(firstElement) {
                            xpath = qName;
                            firstElement = false;
                        } else {
                            xpath += TAG_SEPATOR + qName;
                        }

                        if(qName.equals("magistrate")) {
                            record = new HashMap<>();
                        }

                        break;
                    }

                    case XMLStreamConstants.CHARACTERS: {
                        Characters characters = event.asCharacters();
                        final String property = propsMap.get(xpath);

                        if(property != null) {
                            record.put(property, characters.getData());
                        }

                        break;
                    }

                    case XMLStreamConstants.END_ELEMENT: {
                        String qName = event.asEndElement().getName().getLocalPart();

                        xpath = removeEnd(xpath, TAG_SEPATOR + qName);

                        if(qName.equals("magistrate")) {
                            resultMap.add(record);
                        }

                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }


        return resultMap;
    }

    private Map<String, String> loadProperties() {
        final Properties props = new Properties();
        final Map<String, String> propsMap = new HashMap<>();
        try (InputStream input = XMLTransformer.class.getClassLoader().getResourceAsStream("rotaXml.properties")) {

            props.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (final String key : props.stringPropertyNames()) {
            propsMap.put(props.getProperty(key), key);
        }

        return propsMap;
    }

    public static void main (String args[]) {
        try (final InputStream inputStream = XMLTransformer.class.getClassLoader().getResourceAsStream("payload.xml")) {
            EntityConverter converter = new EntityConverter();

            final List<Map<String, Object>> records = new XMLTransformer().parse(toByteArray(inputStream));
            final List<Assignment> assignments = new ArrayList<>();

            for(final Map<String, Object> record : records) {
                final Assignment assignment = converter.convert(record);
                assignments.add(assignment);
            }

            System.out.print("SIZE : "+records.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
