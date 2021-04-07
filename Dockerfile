FROM scratch as builder
COPY target/*.war /web.war

FROM radien/base:latest 
WORKDIR /

COPY --from=builder /web.war /usr/local/tomee/webapps/web.war

CMD ["catalina.sh", "run"]