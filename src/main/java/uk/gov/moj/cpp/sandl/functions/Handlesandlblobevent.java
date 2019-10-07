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
    private final Enricher enricher = new Enricher();
    private final CourtScheduleRepository repository = new CourtScheduleRepository();
    private final BlobArchiver archiver = new BlobArchiver();

    @FunctionName("Handlesandlblobevent")
    @StorageAccount("sandlstorage_STORAGE")
    public void run(
            @BlobTrigger(name = "content", path = "sandlblobcontainer/{name}", dataType = "binary") byte[] content,
            @BindingName("name") String name,
            final ExecutionContext context) {

        context.getLogger().info("Storage Url :" + storageUrl);

        final Map<RotaPayload, Map<String, Map<String, Object>>> records = new RotaXMLParser().parse(content);

        context.getLogger().info("File parsed successfully now enriching it..");

        final List<CourtSchedule> courtSchedules = enricher.enrich(records, context);

        context.getLogger().info("Enriched it now, saving it to DB..");

        repository.save(courtSchedules, context);

        context.getLogger().info("Saved successfully, now archiving it");

        archiver.archive(name, context);

        context.getLogger().info("Successfully archived the blob to archived blobs container");

        //calReferenceData(context);
    }

//    public String calReferenceData(final ExecutionContext context) {
//        final String result = getRequest(TIMELINESS_SJP_CASE_PENDING, Maps.newHashMap(), context).asString();
//        return result;
//
//    }
}
