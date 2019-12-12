Maven project for evaluating Azure functions and Blob triggers
=================================================================

# Introduction
Read blobs created in sandlblobcontainer, parse the xml, enrich it and save it to the sandldb (using hibernate ORM)

# Details

The `pom.xml` dependencies : -

* Azure-functions-java-library
* Azure-storage (8.4.0)
* Hibernate-core (5.2.3)
* mssql-jdbc (6.1.0-jre8)
* Commons-lang3 (3.9)
* commons-io (2.6)

# Run and Deploy

 * Build project : `mvn clean install prepare-package`
 * Deploy project : `mvn azure-functions:deploy` (you need to login before you run this command)
 * Add new function : `mvn azure-functions:add

# Login to Azure portal

* Open the functionApp `sandlblobeventprocessor`
* Open the log console of 'Handlesandlblobevent' function
* Open the `sandlstorage` storage account on storage explorer (in separate browser window)
* Upload a file to blob to `sandlblobcontainer` and see the log console to check the log of blob created event.


# Install/Do the following in your machine 

* Azure core functions (`https://docs.microsoft.com/en-us/azure/azure-functions/functions-run-local#v2`)
* Azure cli (`https://docs.microsoft.com/en-us/cli/azure/install-azure-cli?view=azure-cli-latest`)
* Update the .net framework to latest version

# Azure cli commands
* login : `az login`
* set correct subscription : `az account set --subscription <subscription id>`
* list subscriptions - `az account list`
