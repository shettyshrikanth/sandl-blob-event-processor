package uk.gov.moj.cpp.sandl.parser;

import static java.util.UUID.randomUUID;
import static javax.xml.stream.XMLInputFactory.newInstance;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static uk.gov.moj.cpp.sandl.parser.util.PropertiesLoader.xmlProperties;

import uk.gov.moj.cpp.sandl.parser.util.RotaPayload;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class RotaXMLParser {

    private final String TAG_SEPATOR = ".";

    public Map<RotaPayload, Map<String, Map<String, Object>>> parse(final byte[] content) {
        final Map<String, Map<String, Object>> magistrates = new HashMap<>();
        final Map<String, Map<String, Object>> districtJudges = new HashMap<>();
        final Map<String, Map<String, Object>> courtListingProfiles = new HashMap<>();
        final Map<String, Map<String, Object>> schedules = new HashMap<>();
        final Map<RotaPayload, Map<String, Map<String, Object>>> result = new HashMap<>();

        Map<String, Object> record = new HashMap<>();
        String xpath = "";
        boolean root = true;

        try {
            final XMLInputFactory factory = newInstance();
            final XMLEventReader eventReader = factory.createXMLEventReader(new ByteArrayInputStream(content));

            while (eventReader.hasNext()) {
                final XMLEvent event = eventReader.nextEvent();

                switch (event.getEventType()) {

                    case XMLStreamConstants.START_ELEMENT: {
                        final StartElement startElement = event.asStartElement();
                        String qName = startElement.getName().getLocalPart();

                        if (root) {
                            xpath = qName;
                            root = false;
                        } else {
                            xpath += TAG_SEPATOR + qName;
                        }

                        if (qName.equals("magistrate")
                                || qName.equals("districtJudge")
                                || qName.equals("schedule")
                                || xpath.endsWith("courtListingProfiles.courtListingProfile")) {
                            record = new HashMap<>();

                            final Iterator attributes = startElement.getAttributes();

                            while (attributes.hasNext()) {
                                Attribute attribute = (Attribute) attributes.next();
                                record.put(attribute.getName().getLocalPart(), attribute.getValue());
                            }
                        } else if (qName.equals("justice")
                                || xpath.endsWith("schedule.courtListingProfile")) {

                            final String idref = startElement.getAttributeByName(new QName("idref")).getValue();
                            record.put(qName, idref);
                        }

                        break;
                    }

                    case XMLStreamConstants.CHARACTERS: {
                        Characters characters = event.asCharacters();
                        final String property = xmlProperties.get(xpath);

                        if (property != null) {
                            record.put(property, characters.getData());
                        }

                        break;
                    }

                    case XMLStreamConstants.END_ELEMENT: {
                        final String qName = event.asEndElement().getName().getLocalPart();
                        xpath = removeEnd(xpath, TAG_SEPATOR + qName);

                        if (qName.equals("magistrate")) {
                            magistrates.put(record.get("id").toString(), record);
                        } else if (qName.equals("districtJudge")) {
                            districtJudges.put(record.get("id").toString(), record);
                        } else if (qName.equals("schedule")) {
                            schedules.put(record.get("id").toString(), record);
                        } else if (xpath.endsWith("courtListingProfiles.courtListingProfile")) {
                            courtListingProfiles.put(record.get("id").toString(), record);
                        } else if (qName.equals("rotaPeriod")) {
                            result.put(RotaPayload.ROTA_PERIOD, Collections.singletonMap(randomUUID().toString(), record));
                        }
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }

        result.put(RotaPayload.MAGISTRATES, magistrates);
        result.put(RotaPayload.DISTRICT_JUDGES, districtJudges);
        result.put(RotaPayload.COURT_LISTING, courtListingProfiles);
        result.put(RotaPayload.SCHEDULE, schedules);

        return result;
    }

    /*public static void main(String args[]) {
        try (final InputStream inputStream = RotaXMLParser.class.getClassLoader().getResourceAsStream("payload.xml")) {
            final long startTime = System.nanoTime();

            final Map<RotaPayload, Map<String, Map<String, Object>>> records = new RotaXMLParser().parse(toByteArray(inputStream));final List<CourtSchedule> courtSchedules = new Enricher().enrich(records);

            final long endtime = System.nanoTime();
            System.out.print("Time took: " + (endtime - startTime));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
