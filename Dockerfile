FROM scratch as builder
COPY **/target/*.war /

FROM radien/base:latest 
WORKDIR /

COPY --from=builder /*.war /usr/local/tomee/webapps/

CMD ["catalina.sh", "run"]