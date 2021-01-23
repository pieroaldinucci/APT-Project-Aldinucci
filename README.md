# APT-Project-Aldinucci


[![Build Status](https://travis-ci.com/Predictabowl/APT-Project-Aldinucci.svg?branch=master)](https://travis-ci.com/Predictabowl/APT-Project-Aldinucci)
[![Coverage Status](https://coveralls.io/repos/github/Predictabowl/APT-Project-Aldinucci/badge.svg?branch=master)](https://coveralls.io/github/Predictabowl/APT-Project-Aldinucci?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Predictabowl_APT-Project-Aldinucci&metric=alert_status)](https://sonarcloud.io/dashboard?id=Predictabowl_APT-Project-Aldinucci)

To launch the full test suit, from the parent folder, the command is:
mvn -f bookstore-parent/pom.xml clean verify -Pjacoco,e2e-test,mutation-test