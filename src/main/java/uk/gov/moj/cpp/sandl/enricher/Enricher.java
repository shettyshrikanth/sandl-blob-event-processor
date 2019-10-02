package uk.gov.moj.cpp.sandl.enricher;

import static java.util.UUID.randomUUID;

import uk.gov.moj.cpp.sandl.parser.util.RotaPayload;
import uk.gov.moj.cpp.sandl.persistence.entity.CourtSchedule;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

public class Enricher {
    private Random random = new Random(100);

    public List<CourtSchedule> enrich(final Map<RotaPayload, Map<String, Map<String, Object>>> records) {

        final Map<String, Map<String, Object>> schedules = records.get(RotaPayload.SCHEDULE);
        final Map<String, Map<String, Object>> courtListings = records.get(RotaPayload.COURT_LISTING);

        final List<CourtSchedule> scheduleList = new ArrayList<>();

        for (final Map<String, Object> schedule : schedules.values()) {
            final String listingId = (String) schedule.get("courtListingProfile");
            final Map<String, Object> courtListing = courtListings.get(listingId);

            final String generatedString = RandomStringUtils.random(7, true, false).toUpperCase();
            final int slots = random.nextInt(5);
            final LocalDate startDate = LocalDate.parse((String) courtListing.get("sessionDate"));
            final LocalDate endDate = startDate.plusDays(slots);
            final ZonedDateTime startTime = startDate.atStartOfDay(ZoneId.systemDefault()).plusHours(slots);
            final ZonedDateTime endTime = endDate.atStartOfDay(ZoneId.systemDefault()).plusHours(slots);
            ;

            scheduleList.add(new CourtSchedule(
                    randomUUID().toString(),
                    generatedString,
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
}
