Radien ${rootArtifactId} Management Microservice
======

# - Pre requirements:
========================

# Make sure in path rd-service-generator
# mvn clean install
# change desired path to generate rd-service-generator

# To generate custom Radien ${rootArtifactId} Management Microservice
========================================================================

mvn archetype:generate \
-DarchetypeGroupId=io.radien \
-DarchetypeArtifactId=rd-service-generator \
-DarchetypeVersion=rd-0.0.2-SNAPSHOT \
-DinteractiveMode=false \
-DgroupId=io.radien \
-DartifactId={artifactId} \
-Dversion=rd-0.0.2-SNAPSHOT \
-Dpackage={package} \
-DentityResourceName={EntityClassName} 

# For example
==================

# -DartifactId=rd-ms-demo
# -Dpackage=io.radien
# -DentityResourceName=Demo

# Add parent pom.xml as a maven project
# maven clean install

# To run server(s)
==================
# follow the instructions provided README.md file in ${rootArtifactId}-service to run ${entityResourceName.toLowerCase()}ManagementService

# follow the instructions provided README.md file in ${rootArtifactId}-web to run web server



