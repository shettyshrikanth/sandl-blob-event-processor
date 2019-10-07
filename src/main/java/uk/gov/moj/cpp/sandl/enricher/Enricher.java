package uk.gov.moj.cpp.sandl.enricher;

import static java.lang.String.format;
import static java.util.UUID.randomUUID;

import uk.gov.moj.cpp.sandl.parser.util.RotaPayload;
import uk.gov.moj.cpp.sandl.persistence.entity.CourtSchedule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;

public class Enricher {
    private Random random = new Random(100);
    private static final String REFDATA_URL = "https://prod-25.uksouth.logic.azure.com/workflows/be2c682354654c348367dc1632b38b2c/triggers/manual/paths/invoke/%7Bresource%7D/?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=7S2QcXriAWcmQAqHjckP1Of_htosO0RhYWFv7xZXuJg";
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<Location, String> cache = new HashMap<>();

    public List<CourtSchedule> enrich(final Map<RotaPayload, Map<String, Map<String, Object>>> records, final ExecutionContext context) {

        final Map<String, Map<String, Object>> schedules = records.get(RotaPayload.SCHEDULE);
        final Map<String, Map<String, Object>> courtListings = records.get(RotaPayload.COURT_LISTING);

        final List<CourtSchedule> scheduleList = new ArrayList<>();

        for (final Map<String, Object> schedule : schedules.values()) {
            final String listingId = (String) schedule.get("courtListingProfile");
            final Map<String, Object> courtListing = courtListings.get(listingId);
            final String locationId = (String) courtListing.get("locationId");
            final String venueId = (String) courtListing.get("venueId");

            final String ouCode = getOuCode(locationId, venueId, context);
            final int slots = random.nextInt(5);
            final LocalDate startDate = LocalDate.parse((String) courtListing.get("sessionDate"));
            final LocalDate endDate = startDate.plusDays(slots);
            final ZonedDateTime startTime = startDate.atStartOfDay(ZoneId.systemDefault()).plusHours(slots);
            final ZonedDateTime endTime = endDate.atStartOfDay(ZoneId.systemDefault()).plusHours(slots);

            scheduleList.add(new CourtSchedule(
                    randomUUID().toString(),
                    ouCode,
                    startDate,
                    startTime,
                    endDate,
                    endTime,
                    slots,
                    false,
                    true));
        }

        return scheduleList;
    }

    public String getOuCode(final String locationId, final String venueId, final ExecutionContext context) {

        final Location location = new Location(locationId, venueId);

        String ouCode = cache.get(location);

        if(ouCode != null) {
            //context.getLogger().info(format("OuCode %s  found in cache\n", ouCode));
            return ouCode;
        }

        try {
            context.getLogger().info(format("Calling reference data to fetch Court Centre for location : %s and venue : %s \n", locationId, venueId));
            final String params = format("&locationId=%d&venueId=%d", Integer.parseInt(locationId), Integer.parseInt(venueId));

            URL url = new URL(REFDATA_URL + params);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            final String response = new BufferedReader(new InputStreamReader((conn.getInputStream()))).readLine();

            conn.disconnect();

            ouCode = mapper.readTree(response).get(0).get("oucode").asText();

            cache.put(location, ouCode);

            context.getLogger().info(format("Got response from reference data with OUCode : %s \n", ouCode));

            return ouCode;

        } catch (Exception e) {
            throw new RuntimeException("Referencedata call failed : ", e);
        }
    }

    private class Location {
        private final String id;
        private final String venue;

        public Location(final String id, final String venue) {
            this.id = id;
            this.venue = venue;
        }

        public String getId() {
            return id;
        }

        public String getVenue() {
            return venue;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Location location = (Location) o;
            return Objects.equals(id, location.id) &&
                    Objects.equals(venue, location.venue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, venue);
        }
    }
}
