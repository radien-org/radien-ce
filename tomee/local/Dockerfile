FROM tomee:8-jre-8.0.9-plus
WORKDIR /
#https://host.docker.internal:8343/auth/realms/radien/.well-known/openid-configuration

ENV CATALINA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000" \
AUTH_ACCESS_TOKEN_URI=http://host.docker.internal:8180/auth/realms/radien/protocol/openid-connect/token \
auth_redirectUri=https://localhost:8443/web/login \
auth_logoutRedirectUri=https://host.docker.internal:8343/auth/realms/radien/protocol/openid-connect/logout \
auth_issuer=https://localhost:8343/auth/realms/radien \
auth_jwkUrl=http://host.docker.internal:8180/auth/realms/radien/protocol/openid-connect/certs \
AUTH_USER_AUTHORIZATION_URI=https://host.docker.internal:8343/auth/realms/radien/protocol/openid-connect/auth \
KEYCLOAK_CLIENT_ID=ServiceAccountsClient \
KEYCLOAK_IDP_URL=https://host.docker.internal:8343 \
KEYCLOAK_SECRET=f5e92b7c-4799-4c23-b3d8-4b3a27dad009 \
KEYCLOAK_TOKEN_PATH=/auth/realms/master/protocol/openid-connect/token \
KEYCLOAK_USER_PATH=/auth/admin/realms/radien/users \
SCRIPT_CLIENT_ID_VALUE=radien \
SCRIPT_CLIENT_SECRET_VALUE=86c27042-b3a2-48ea-a936-025103144844 \
REALMS_TOKEN_PATH=/auth/realms/radien/protocol/openid-connect/token \
system_mail_host=smtp.office365.com \
system_mail_user=openappframe@rethink.de \
system_mail_password=missing \
system_mail_from_system_admin=openappframe@re-think.consulting \
system_mail_to_system_admin=openappframe@re-think.consulting \
system_ms_endpoint_usermanagement=http://localhost:8080/usermanagementservice/v1 \
system_ms_endpoint_tenantmanagement=http://localhost:8080/tenantmanagementservice/v1 \
system_ms_endpoint_rolemanagement=http://localhost:8080/rolemanagementservice/v1 \
system_ms_endpoint_permissionmanagement=http://localhost:8080/permissionmanagementservice/v1 \
system_ms_endpoint_ecm=http://localhost:8080/ecm \
SYSTEM_JCR_SOURCE=FILESYSTEM \
SYSTEM_JCR_HOME=rd_cms \
SYSTEM_JCR_NODETYPES_CND_FILE=jcr/oafnodetypes.cnd \
RADIEN_ENV=local



COPY tomee/local/lib/* /usr/local/tomee/lib/
COPY tomee/local/conf/* /usr/local/tomee/conf/
COPY rd-web/target/web.war /usr/local/tomee/webapps/
COPY rd-ms-usermanagement/target/usermanagementservice.war /usr/local/tomee/webapps/
COPY rd-ms-tenantmanagement/target/tenantmanagementservice.war /usr/local/tomee/webapps/
COPY rd-ms-rolemanagement/target/rolemanagementservice.war /usr/local/tomee/webapps/
COPY rd-ms-permissionmanagement/target/permissionmanagementservice.war /usr/local/tomee/webapps/
COPY rd-ms-ecm/target/cms.war /usr/local/tomee/webapps/cms.war

#COPY manager/META-INF/* /usr/local/tomee/webapps/manager/META-INF/
#COPY manager/WEB-INF/* /usr/local/tomee/webapps/manager/WEB-INF/
#docker run -d -p 8080:8080 -p 8443:8443 -p 8000:8000 -v $(pwd)/remoteTomee:/usr/local/tomee/logs --name radien-tomee1 radien-tomee

CMD ["catalina.sh", "run"]

