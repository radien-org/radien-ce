#Support local execution
To support local execution of radien you need to configure the environment variables on the dockerfile.
then you need to build the docker image and execute.
After configuration, you can access keycloak console through ports 8080(http) ,8443(https) and 8000(remote jvm debug)

#stop the docker container with
<pre><code> docker stop radien-tomee1</code></pre>


#How to build custom keycloak radien docker image
<p>Open a terminal on the root of the git repository and execute</p>
<li>the war files are copied to the image in this step so make sure you have then available on the respective target folders</li>
<pre><code>docker build -t radien-tomee -f tomee/local/Dockerfile .</code></pre>

#How to execute 
   <p>creates container using the previously created image name radien-tomee</p>
   <li>it makes available the service throw the ports 8080(http), 8443(https), 8000 (remote jvm debug)</li>
   <li>you can find the logs from the application server in the directory radien/tomee/local/remoteTomee</li>

<pre><code>docker run -d -p 8080:8080 -p 8443:8443 -p 8000:8000 -v $(pwd)/tomee/local/remoteTomee:/usr/local/tomee/logs --name radien-tomee1 radien-tomee</code></pre>

