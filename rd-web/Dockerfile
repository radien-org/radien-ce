FROM tomee:8-jre-8.0.5-plus as builder
COPY target/web.war /web.war
COPY cfg /cfg

FROM tomcat:8-jdk8-corretto 
WORKDIR /
ENV SPRING_CONFIG_NAME=application,local

COPY --from=builder /web.war /usr/local/tomcat/webapps/web.war
COPY --from=builder /cfg/* /usr/local/tomcat/conf/

CMD ["catalina.sh", "run"]
