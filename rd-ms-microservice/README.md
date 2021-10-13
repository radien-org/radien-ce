Radien rd-ms-microservice Management Microservice
======

# - Pre requirements:
========================

# Make sure in path rd-service-generator
# mvn clean install
# change desired path to generate rd-service-generator

# To generate custom Radien rd-ms-microservice Management Microservice
========================================================================

mvn archetype:generate \
-DarchetypeGroupId=io.radien \
-DarchetypeArtifactId=rd-service-generator \
-DarchetypeVersion=rd-0.0.2-SNAPSHOT \
-DinteractiveMode=false \
-DgroupId=io.radien \
-DartifactId=rd-ms-microservice \
-Dversion=rd-0.0.2-SNAPSHOT \
-Dpackage=io.rd.microservice \
-DentityResourceName=Microservice 

# For example
==================

# -DartifactId=rd-ms-demo
# -Dpackage=io.radien
# -DentityResourceName=Demo

# Add parent pom.xml as a maven project
# maven clean install

# To run server(s)
==================
# follow the instructions provided README.md file in rd-ms-microservice-service to run microserviceManagementService

# follow the instructions provided README.md file in rd-ms-microservice-web to run web server



