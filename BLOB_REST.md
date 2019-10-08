# How to insert blobs through REST

To access  blob containers though rest API, firstly we need to generate 'Blob service SAS URL' with security and all the parameters which can be use for inserting blobs into containerusing the rest.

Do the following to generate Blob service SAS URL -
1. Login to azure portal
2. Navigate to storage accounts page
3. Click on 'Shared access signature'
4. Select the Allowed service, Expiry date Enter and Allowed ip range.
5. Click on generate SAS and connection string button.
6. Blob service SAS URL' is the endpoint to access the blob container
7. Append the the blob container name and blob file name before the start of parameters list in the URL.

Create a PUT request with header 'x-ms-blob-type' with value 'BlockBlob' and xml in the body of the request to push a file into the required blob container

Example of REST endpoint for pushing file to sandlblob container -
https://sandlstorage.blob.core.windows.net/sandlblobcontainer/myblo_1b.xml?sv=2018-03-28&ss=bfqt&srt=sco&sp=rwdlacup&se=2021-03-31T18:08:04Z&st=2019-10-09T10:08:04Z&sip=5.148.40.98&spr=https&sig=TXBdlzt2VAw0CHJIWyXVYnzcJMvJ%2FLrQ0iWidIFkVE4%3D