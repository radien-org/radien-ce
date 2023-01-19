FROM tomee:8-jre-8.0.9-plus
WORKDIR /

ARG SOURCE_WAR_PATH
ARG EXT_PROPERTIES_PATH

ENV CONFIG_ORDINAL=500

ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,address=8000,suspend=n"
ENV RADIEN_ENV=local
ENV AUTHENTICATION_CLIENT_ID=userManagement
ENV AUTHENTICATION_CLIENT_SECRET=48cbf1a9-f383-49bc-8b84-25bd3130c716
ENV AUTH_ACCESS_TOKEN_URI=http://host.docker.internal:8180/auth/realms/radien/protocol/openid-connect/token
ENV AUTH_USER_AUTHORIZATION_URI=https://host.docker.internal:8343/auth/realms/radien/protocol/openid-connect/auth
ENV SCRIPT_REDIRECT_URL_VALUE=https://host.docker.internal:8443/web/login
ENV AUTH_LOGOUT_REDIRECT_URI=https://host.docker.internal:8343/auth/realms/radien/protocol/openid-connect/logout
ENV AUTH_ISSUER=https://localhost:8343/auth/realms/radien
ENV AUTH_JWKURL=http://host.docker.internal:8180/auth/realms/radien/protocol/openid-connect/certs
ENV AUTH_USER_INFO=https://localhost:8343/auth/realms/radien/protocol/openid-connect/userinfo
ENV KEYCLOAK_CLIENT_ID=ServiceAccountsClient
ENV KEYCLOAK_IDP_URL=https://host.docker.internal:8343
ENV KEYCLOAK_SECRET=f5e92b7c-4799-4c23-b3d8-4b3a27dad009
ENV KEYCLOAK_TOKEN_PATH=/auth/realms/master/protocol/openid-connect/token
ENV KEYCLOAK_USER_PATH=/auth/admin/realms/radien/users
ENV SCRIPT_CLIENT_ID_VALUE=radien
ENV SCRIPT_CLIENT_SECRET_VALUE=86c27042-b3a2-48ea-a936-025103144844
ENV REALMS_TOKEN_PATH=/auth/realms/radien/protocol/openid-connect/token

ENV auth_redirectUri=https://localhost:8443/web/login

ENV system_ms_endpoint_usermanagement=http://host.docker.internal:8085/usermanagementservice/v1
ENV system_ms_endpoint_tenantmanagement=http://host.docker.internal:8084/tenantmanagementservice/v1
ENV system_ms_endpoint_rolemanagement=http://host.docker.internal:8083/rolemanagementservice/v1
ENV system_ms_endpoint_permissionmanagement=http://host.docker.internal:8082/permissionmanagementservice/v1
ENV system_ms_endpoint_ecm=http://host.docker.internal:8081/cms/v1
ENV system_ms_endpoint_doctypemanagement=http://host.docker.internal:8086/doctypemanagementservice/v1

COPY tomee/local/lib/* /usr/local/tomee/lib/
COPY tomee/local/conf/* /usr/local/tomee/conf/
COPY $EXT_PROPERTIES_PATH /usr/local/tomee/conf/
COPY $SOURCE_WAR_PATH /usr/local/tomee/webapps/
