package uk.gov.moj.cpp.sandl.util;

import static java.lang.System.getenv;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

public class BlobArchiver {
    private final String storageUrl = getenv("sandl_storage_url");

    public void archive(final String name, final ExecutionContext context) {
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
