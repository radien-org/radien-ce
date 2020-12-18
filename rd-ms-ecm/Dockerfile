FROM tomee:latest as builder
COPY target/rd-ms-ecm.war /web.war
COPY cfg /cfg

FROM tomee:latest 
WORKDIR /

COPY --from=builder /web.war /usr/local/tomee/webapps/web.war
COPY --from=builder /cfg/* /usr/local/tomee/conf/

CMD ["catalina.sh", "run"]
