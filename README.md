# sandl-blob-event-processor
* Checkout the project
* Build project : `mvn clean install prepare-package`
* Deploy project : `mvn azure-functions:deploy`

* Open the functionApp `sandlblobeventprocessor` in azure web portal
* Click on `Handlesandlblobevent` function
* Open the log console

* Open the `sandlstorage` storage account on storage explorer (in separate browser window)
* Navigate to `sandlblobcontainer` in BLOB Containers section
* Upload a file to blob container and see the log console to check the log of blob created event.
