docker pull andrelugomes/docker-activemq
docker run --name activemq -d -p 61616:61616 -p 8161:8161 andrelugomes/docker-activemq:5.13.3