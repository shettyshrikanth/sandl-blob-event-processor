package uk.gov.moj.cpp.sandl.functions;

import static java.lang.System.getenv;

import uk.gov.moj.cpp.sandl.enricher.Enricher;
import uk.gov.moj.cpp.sandl.parser.RotaXMLParser;
import uk.gov.moj.cpp.sandl.parser.util.RotaPayload;
import uk.gov.moj.cpp.sandl.persistence.entity.CourtSchedule;
import uk.gov.moj.cpp.sandl.persistence.repository.CourtScheduleRepository;
import uk.gov.moj.cpp.sandl.util.BlobArchiver;

import java.util.List;
import java.util.Map;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.BlobTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.StorageAccount;


public class Handlesandlblobevent {

    private final String storageUrl = getenv("sandl_storage_url");
    private final String skip = getenv("skip_files");
    private final String orm = getenv("orm");
    private final Enricher enricher = new Enricher();
    private final CourtScheduleRepository repository = new CourtScheduleRepository();
    private final BlobArchiver archiver = new BlobArchiver();

    @FunctionName("Handlesandlblobevent")
    @StorageAccount("sandlstorage_STORAGE")
    public void run(
            @BlobTrigger(name = "content", path = "%blobtriggerpath%", dataType = "binary") byte[] content,
            @BindingName("name") String name,
            final ExecutionContext context) {

        try {

            context.getLogger().info("Started processing  blob :" + name);

            if (!skip.equals("true")) {

                final Map<RotaPayload, Map<String, Map<String, Object>>> records = new RotaXMLParser().parse(content);

                context.getLogger().info("File parsed successfully now enriching it..");

                final List<CourtSchedule> courtSchedules = enricher.enrich(records, context);

                context.getLogger().info(String.format("Enriched %d , saving it to DB..", courtSchedules.size()));

                if (orm.equals("true")) {
                    repository.saveOrm(courtSchedules, context);
                } else {
                    repository.saveJdbc(courtSchedules, context);
                }

                context.getLogger().info("Saved successfully, now archiving it");

                archiver.archive("lmn/" + name, context);

                context.getLogger().info("Successfully archived the blob to archived blobs container");
            } else {
                context.getLogger().info("Skipped  processing  blob :  " + name);
            }
        } catch (Exception e) {
            context.getLogger().info("Exception :" + e.getMessage());
        }

        //calReferenceData(context);
    }

//    public String calReferenceData(final ExecutionContext context) {
//        final String result = getRequest(TIMELINESS_SJP_CASE_PENDING, Maps.newHashMap(), context).asString();
//        return result;
//
//    }
}
