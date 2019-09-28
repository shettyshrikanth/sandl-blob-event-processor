package uk.gov.moj.cpp.sandl;

import static java.util.UUID.randomUUID;

import uk.gov.moj.cpp.sandl.entity.Assignment;
import uk.gov.moj.cpp.sandl.parser.EntityConverter;
import uk.gov.moj.cpp.sandl.parser.XMLTransformer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
import com.microsoft.rest.protocol.Environment;


public class Handlesandlblobevent {

    private EntityConverter converter = new EntityConverter();
    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=sandlstorage;AccountKey=WtYR1J6BmQ/U16dbZdP/bPr2Vx2NWaoKZKfWKfSyKrtGhjGWV3ZuzSdZMMIknlYCDGECxhQZ+SE3K7iGd+6g4A==;EndpointSuffix=core.windows.net";

    //sandl-db-conn-url


    String connectionUrl = "jdbc:sqlserver://sandl.database.windows.net:1433;database=sandldb;user=sandldb;password=Passw0rd;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;";


    @FunctionName("Handlesandlblobevent")
    @StorageAccount("sandlstorage_STORAGE")
    public void run(
            @BlobTrigger(name = "content", path = "sandlblobcontainer/{name}", dataType = "binary") byte[] content,
            @BindingName("name") String name,
            final ExecutionContext context) {
        final List<Map<String, Object>> records = new XMLTransformer().parse(content);

        savetoDB(records, context);

        archiveBlob(name, context);

        context.getLogger().info("Transformed and archived the content and found " + records.size() + " sitting records");
    }

    private void archiveBlob(final String name, final ExecutionContext context) {
        try {


            context.getLogger().info("Started archiving the blob");

            final CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
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

    private boolean savetoDB(final List<Map<String, Object>> records, final ExecutionContext context) {
        try (Connection con = DriverManager.getConnection(connectionUrl);
             PreparedStatement pstmt = con.prepareStatement("INSERT INTO Assignment (Id, JusticeAreaId, JusticeAreaType, Title, Surname, Forenames, LjaId, SittingLocationId, RotaScheduleStartDate,\n" +
                     " RotaScheduleEndDate, DobDate, StartDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            context.getLogger().info("Got the connection " + (con != null) + "and now start inserting records into DB ");

            for (final Map<String, Object> record : records) {
                final Assignment assignment = converter.convert(record);
                pstmt.setString(1, randomUUID().toString());
                pstmt.setString(2, assignment.getJusticeAreaId());
                pstmt.setString(3, assignment.getJusticeAreaType());
                pstmt.setString(4, assignment.getTitle());
                pstmt.setString(5, assignment.getSurname());
                pstmt.setString(6, assignment.getForenames());
                pstmt.setString(7, assignment.getLjaId());
                pstmt.setString(8, assignment.getSittingLocationId());
                pstmt.setObject(9, assignment.getRotaScheduleStartDate());
                pstmt.setObject(10, assignment.getRotaScheduleEndDate());
                pstmt.setObject(11, assignment.getDobDate());
                pstmt.setObject(12, assignment.getStartDate());

                pstmt.executeUpdate();
                context.getLogger().info("Inserted " + assignment.getJusticeAreaId() + " into DB");
            }
            context.getLogger().info("Successfully loaded 120 records into DB");
            con.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            context.getLogger().info("Exception during save to DB : " + e.getMessage());

            return false;
        }

        return true;
    }
}
