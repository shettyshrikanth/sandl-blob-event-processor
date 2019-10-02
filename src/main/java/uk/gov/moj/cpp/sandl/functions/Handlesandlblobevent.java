package uk.gov.moj.cpp.sandl.functions;

import static java.lang.System.getenv;

import uk.gov.moj.cpp.sandl.enricher.Enricher;
import uk.gov.moj.cpp.sandl.parser.RotaXMLParser;
import uk.gov.moj.cpp.sandl.parser.util.RotaPayload;
import uk.gov.moj.cpp.sandl.persistence.entity.CourtSchedule;
import uk.gov.moj.cpp.sandl.persistence.repository.HibernateConfiguration;

import java.util.List;
import java.util.Map;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.BlobTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.StorageAccount;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class Handlesandlblobevent {

    private final String storageUrl = getenv("sandl_storage_url");
    private Configuration configuration = new HibernateConfiguration().createHibernateConfiguration();
    private final Enricher enricher = new Enricher();

    @FunctionName("Handlesandlblobevent")
    @StorageAccount("sandlstorage_STORAGE")
    public void run(
            @BlobTrigger(name = "content", path = "sandlblobcontainer/{name}", dataType = "binary") byte[] content,
            @BindingName("name") String name,
            final ExecutionContext context) {

        context.getLogger().info("Storage Url :" + storageUrl);

        final Map<RotaPayload, Map<String, Map<String, Object>>> records = new RotaXMLParser().parse(content);

        context.getLogger().info("File parsed successfully now enriching it..");

        final List<CourtSchedule> courtSchedules = enricher.enrich(records);
        context.getLogger().info("Enriched it now, saving it to DB..");

        saveToDB(courtSchedules, context);

        context.getLogger().info("Saved successfully, now archiving it");

        archiveBlob(name, context);

        context.getLogger().info("Transformed and archived the content and found " + records.size() + " sitting records");
    }

    private boolean saveToDB(final List<CourtSchedule> records, final ExecutionContext context) {

        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            context.getLogger().info("Started saving court schedules\n");
            session.beginTransaction();

            for (final CourtSchedule courtSchedule : records) {
                session.save(courtSchedule);
            }

            session.getTransaction().commit();

            context.getLogger().info("Saved all court schedules successfully \n");
        } catch (Exception e) {
            e.printStackTrace();
            context.getLogger().info("Exception during save to DB : " + e.getMessage());

            return false;
        }

        return true;
    }

    private void archiveBlob(final String name, final ExecutionContext context) {
        try {

            context.getLogger().info("Started archiving the blob");

            final CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageUrl);
            final CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            final CloudBlobContainer container = blobClient.getContainerReference("sandlblobcontainer");
            final CloudBlockBlob blob = container.getBlockBlobReference(name);

            final CloudBlobContainer archiveContainer = blobClient.getContainerReference("archivesandlblobcontainer");
            final CloudBlockBlob targetBlob = archiveContainer.getBlockBlobReference(blob.getName());
            targetBlob.startCopy(blob);

            context.getLogger().info("Copied the source blob to destination blob");

            blob.delete();

            context.getLogger().info("Removed the blob from the source contained successfully");

        } catch (Exception e) {
            context.getLogger().info("Exception during the archival :" + e.getLocalizedMessage());
        }
    }

}
