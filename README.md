## FTP to REST data transfer service

### What should be installed
Docker, Docker Compose, JDK 11 or 17 and Maven must be installed
JAVA_HOME variable should be set
* Download JDK 11/17 [here](https://www.oracle.com/java/technologies/downloads/) or [here](https://openjdk.org/projects/jdk/)
* Docker Desktop available [here](https://www.docker.com/products/docker-desktop/)
* Maven download and installation [guide](https://maven.apache.org/install.html)

### How to run
Before starting build and run the webapp itself – the database and FTP should be configured first, to achieve this run the following command in the project root directory:
> docker-compose up

Now we have 3 containers up and running:
* MongoDB with 3 databases (dev_db, prod_db, test_db)
* Mongo Express Web UI
* Pure-FTPd server with 3 files available (the 'ftp' folder in the root of project tree, new files should be added to 'ftp/data/csv');

To build the project and run tests execute:
> mvn clean compile test

There are **3 profiles** available to run the application:
- **dev** (uses **dummy** FTP connector and **dummy** REST API client)
- **test** (uses **dummy** FTP connector and **dummy** REST API client)
- **prod** (uses **real** FTP connector and **real** REST API client)

To run application with prod profile execute:
> mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"

If you run from IntelliJ IDEA – don't forget to specify the application profile in Run Configuration too ('prod' or 'dev')

## Where to check
Now you can access Swagger OpenAPI by following this link: http://localhost:8080/swagger-ui/index.html

And Mongo Express UI here: http://localhost:8081/

## Implementation notes
As per technical task – new csv files with data are posted by 23:59 of the current day. They must be processed no later than 04:00 and sent to remote REST API.
For simplification, we assume that we are located in same timezone.

Import of date is being triggered every day at 00:00.
Additionally, the import can be triggered calling the API endpoint:
> POST http://localhost:8080/api/v1/payouts/processing/trigger

Body should be provided in request with the county we want import to be triggered ('WAKANDA' and 'GONDOR' are accepted):

`{
"countryName": "WAKANDA"
}`

### Import
Files on FTP server have particular naming:
>WK_payouts_**20221115**_202004.csv

Where the **20221115** is a creation date in format yyyyMMdd.
Application only consumes files, created **today** according to this naming, all previously created files are out of scope for this implementation.

All dataflow can be split into following stages:
1. Check remote folder exists
2. Get the list of remote files
3. Filter only CSVs created today
4. Download suitable files
5. Map into internal POJO
6. Start export
7. If export failed for the particular file with 5xx error – retry 5 times each 2 seconds
8. Mark the whole file (and the particular payout) as isReported (true/false) and store in MongoDB

Payouts in status PENDING can be included/not included into the final report into the debt collection system, this can be configured in application-properties:
> include.pending.payouts=true

Due to the presence of a local DB we always know which files were failed to report not only during this application run but also in previous time.
This adds us the possibility to create an additional logic of retries on top of the existing Retryable level. I added also a TODO mark regarding this in **IntrumProdRestClientImpl.java**

Attempt to trigger import for GONDOR causes the UNSUPPORTED_ACTION error

#### Additional notes
The Retryable and Recover annotations appeared hard to cover with unit tests, so I only **tested them both manually** as I didn't want to invest additional time into this problem. Looks like it's easier to do when you have it configured with beans in a separate Configuration class, but in my case it is fully annotation-driven.