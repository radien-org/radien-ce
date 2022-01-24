#Support local execution
To support local execution of radien you need two realms that were previously exported for you.
To configure your environment you need to build the docker image and execute for import.
After configuration, you can access keycloak console through ports 8180(http) and 8343(https)

#stop the docker container with
<pre><code> docker stop rkc</code></pre>
and
Since this is in a public repository you should always customise your keycloak installation for public environments

#How to build custom keycloak radien docker image
Open a terminal on the same directory of this readme.md and execute

<pre><code>docker build -t radien-keycloak .</code></pre>

#How to execute for import
   <p>creates container using the previously created image name radien-keycloak and imports previously exported realms</p>
   <li>it maps the current directory where is the command is executed to container tmp</li>
   <li>it makes available the service throw the ports 8180(http) and 8343(https)</li>
   <li>the --rm flag automatically remove the container when it exits clearing all the data in it. If you want to manage it with "docker start" and "docker stop" remove it</li>

<pre><code>docker run -d --rm -p 8180:8080 -p 8343:8443 -e KEYCLOAK_USER=raiden -e KEYCLOAK_PASSWORD=brutality -e KEYCLOAK_IMPORT=/tmp/master.json -e KEYCLOAK_IMPORT=/tmp/radien.json -v $(pwd):/tmp --name rkc radien-keycloak</code></pre>

#How to export realms

<p>How to start with a blank keycloak for customizations and later export</p>
<pre><code>docker run -d --rm -p 8180:8080 -p 8343:8443 -e KEYCLOAK_USER=raiden -e KEYCLOAK_PASSWORD=brutality -v $(pwd):/tmp --name rkc radien-keycloak</code></pre>

#Exports realm files
<p>The examples provided are exporting the realms master and radien from an container named "rkc"</p>
<li>it executes the script standalone inside the container named rkc</li>
<li>After "Admin console listening on" you need to end each command with a Ctrl+C to free the terminal</li>
<pre><code>docker exec -d -it rkc /opt/jboss/keycloak/bin/standalone.sh -Djboss.socket.binding.port-offset=100 -Dkeycloak.migration.action=export -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.realmName=master -Dkeycloak.migration.usersExportStrategy=REALM_FILE -Dkeycloak.migration.file=/tmp/master.json</code></pre>
<pre><code>docker exec -d -it rkc /opt/jboss/keycloak/bin/standalone.sh -Djboss.socket.binding.port-offset=100 -Dkeycloak.migration.action=export -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.realmName=radien -Dkeycloak.migration.usersExportStrategy=REALM_FILE -Dkeycloak.migration.file=/tmp/radien.json</code></pre>

