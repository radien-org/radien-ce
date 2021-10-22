#!/bin/sh
#to run this shell script you need jq (is a lightweight and flexible command-line JSON processor)
echo''
if ! command -v jq &> /dev/null
then
    echo "To run this shell script you need to install jq (It is a lightweight and flexible command-line JSON processor)"
    exit
fi

#Variables
initialize_validation='
-. Validating the existence of rd-ms-demo module in parent pom.xml....
'
start_Initializer='
|---------------------------------------------------------------------------------------------
|-. Started Initializer ...                                                                   |
|---------------------------------------------------------------------------------------------'
stop_Initializer='
|---------------------------------------------------------------------------------------------
|-. Initializer stopped.                                                                      |
|---------------------------------------------------------------------------------------------
'

initiate_processMS='
-. Initiated to build a micro-service....
'

buildModule_success='
-. Radien Service Generator built SUCCESSFULLY....
'

initialize_RSG='
-. Micro-Service generating....
'

successInfo_RSG='
-. Micro-Service Generated SUCCESSFULLY....
'

validates_noModule='
*. Hello User, Do you want to create a new micro service (Y/N): '

validates_existsModule='
*. Hello User, Do you want continue to work with the existing module (Y/N): '

validates_toCleanupExistsModule_LocalMavenRepo='
*. Hello User, Do you want to delete the existing micro-service & create a new micro service (Y/N): '

varString_moduleExists='
|---------------------------------------------------------------------------------------------
|-. Find the existing module rd-ms-demo, continue to explore....                              |
|---------------------------------------------------------------------------------------------
'

varString_moduleCreates='
|---------------------------------------------------------------------------------------------
|-. Processing to create a micro service...                                                   |
|---------------------------------------------------------------------------------------------
'

#Application
echo $start_Initializer
sleep 1
echo '
!---------------------------------------------------------------------------------------------!
|  _   _   _   _   _   _     _   _   _   _   _   _   _     _   _   _   _   _   _   _   _   _  |
| / \ / \ / \ / \ / \ / \   / \ / \ / \ / \ / \ / \ / \   / \ / \ / \ / \ / \ / \ / \ / \ / \ |
|[ R ! a ! d ! i ! e ! n ] [ S ! e ! r ! v ! i ! c ! e ] [ G ! e ! n ! e ! r ! a ! t ! o ! r ]|
| \_/ \_/ \_/ \_/ \_/ \_/   \_/ \_/ \_/ \_/ \_/ \_/ \_/   \_/ \_/ \_/ \_/ \_/ \_/ \_/ \_/ \_/ |
|---------------------------------------------------------------------------------------------|
| Author: Rajesh Gavvala                                                                      |
!---------------------------------------------------------------------------------------------!
'

#Functions
buildModuleRSG() {
  cd rd-service-generator/
  mvn clean install
}

buildRadien() {
  mvn clean install -DskipTests=true
}

cleanRadien() {
  mvn clean
}

buildMicroService() {
  echo '\n-. Initializing Maven Clean Install : rd-ms-demo'
  cd rd-ms-demo
  mvn clean install
  echo '\n-. Built SUCCESSFULLY : rd-ms-demo'
  cd ..
}

generateMicroService() {
  cd ..
  mvn archetype:generate \
  -DarchetypeGroupId=io.radien \
  -DarchetypeArtifactId=rd-service-generator \
  -DarchetypeVersion=rd-0.0.2-SNAPSHOT \
  -DinteractiveMode=false \
  -DgroupId=io.radien \
  -DartifactId=rd-ms-demo \
  -Dversion=rd-0.0.2-SNAPSHOT \
  -Dpackage=io.rd \
  -DentityResourceName=Demo
}

cleanupExistsModule_LocalMVNRepo() {
  echo -n $validates_toCleanupExistsModule_LocalMavenRepo
  read REPLY
  case $REPLY in
  [Yy])
    M2_REPO=${HOME}/.m2/repository/io/radien
    echo "\n!. Deleting existing local maven repositories & project module of rd-ms-demo "
    sed -i '' -e '/<module>rd-ms-demo<\/module>/d' ./pom.xml
    find "${M2_REPO}" -name 'rd-ms-demo' -exec rm -rf {} \;
    find "${M2_REPO}" -name 'rd-ms-demo*-**' -exec rm -rf {} \;
    find . -name "rd-ms-demo" -type d -exec rm -rf {} \;
    echo "!. No more exists rd-ms-demo in local maven repositories & project module "
    echo '\nRe-conform once again to create a micro-service'
    showStatement_noModule;;

  [Nn]) echo $varString_moduleExists;
        echo $stop_Initializer ;;
  *) cleanupExistsModule_LocalMVNRepo ;;
  esac
}

showStatement_noModule() {
  sleep 1
  echo -n $validates_noModule
  read REPLY
  case $REPLY in
  [Yy]) echo $initiate_processMS;
        sleep 1
        cleanRadien;
        buildModuleRSG;
        echo $buildModule_success;
        sleep 1
        echo $initialize_RSG;
        sleep 1
        generateMicroService;
        sleep 1
        echo $successInfo_RSG;
        sleep 1
        buildMicroService;
        sleep 1
        buildRadien;
        echo $stop_Initializer;
        echo '-. To Run the module\nReload the pom.xml to maven/remove un ignore to maven if any in the IDE\n...Explore the module!\n';;
  [Nn]) echo $stop_Initializer;;
  *) showStatement_noModule ;;
  esac
}

showStatement_moduleExists() {
  sleep 1
  echo -n $validates_existsModule
  read REPLY
  case $REPLY in
  [Yy]) echo $varString_moduleExists;
        echo $stop_Initializer;;
  [Nn]) sleep 1;
    cleanupExistsModule_LocalMVNRepo ;;
  *) showStatement_moduleExists ;;
  esac
}

getUserConfirmation_ProcessMS() {
  case $REPLY in
  [Yy]) echo $varString_moduleExists;;
  [Nn]) showStatements;;
  *) getUserConfirmation_ProcessMS ;;
  esac
}

getUserConfirmation_createMS() {
  case $REPLY in
  [Yy]) echo $varString_moduleCreates;;
  [Nn]) echo $stop_Initializer;;
  *) getUserConfirmation_createMS ;;
  esac
}

echo $initialize_validation
if grep "<module>rd-ms-demo</module>" ./pom.xml > /dev/null
then
  echo '\n-. rd-ms-demo module exists already'
  sleep 1
  showStatement_moduleExists
else
  echo '\n-. rd-ms-demo module does not exists'
  sleep 1
  showStatement_noModule
fi