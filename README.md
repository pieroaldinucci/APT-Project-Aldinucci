# APT-Project-Aldinucci


[![Build Status](https://travis-ci.com/Predictabowl/APT-Project-Aldinucci.svg?branch=master)](https://travis-ci.com/Predictabowl/APT-Project-Aldinucci)
[![Coverage Status](https://coveralls.io/repos/github/Predictabowl/APT-Project-Aldinucci/badge.svg?branch=master)](https://coveralls.io/github/Predictabowl/APT-Project-Aldinucci?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Predictabowl_APT-Project-Aldinucci&metric=alert_status)](https://sonarcloud.io/dashboard?id=Predictabowl_APT-Project-Aldinucci)

## Build
To launch the full test suit, from the parent folder, the command is:

```
./mvnw -f bookstore-parent/pom.xml clean verify -Pjacoco,e2e-test,mutation-test
```

## Database test container
From the root folder:

```
dcup
```

to launch docker container used for tests. It needs port 5432 available to run. 

## Launch the application
To launch the application for the first time using a blank database, from the root folder:

```
java -jar bookstore-app/target/bookstore-app-0.0.2-SNAPSHOT-jar-with-dependencies.jar -u=testUser -p=password -c
```
The `-c` option is used to create the needed tables and can be omitted if the database is already initialized.

For further help about available launch options:
```
java -jar bookstore-app/target/bookstore-app-0.0.2-SNAPSHOT-jar-with-dependencies.jar -h
```