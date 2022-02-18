FROM quay.io/keycloak/keycloak:16.1.1
#ENV KEYCLOAK_USER=raiden \
#    KEYCLOAK_PASSWORD=brutality
#    KEYCLOAK_IMPORT=/tmp/master.json
#    KEYCLOAK_WELCOME_THEME=
#    KEYCLOAK_DEFAULT_THEME=

#https://www.baeldung.com/spring-keycloak-custom-themes
#COPY themes /opt/jboss/keycloak/themes


# kcadm.sh config credentials --server http://localhost:8080/auth --realm master --user raiden brutality

# Deploy Keycloak custom themes
COPY ../../rd-ms-keycloak-themes/target/keycloak-custom-themes.jar /opt/jboss/keycloak/standalone/deployments/
COPY ../../rd-ms-keycloak-customization/src/main/config/standalone.xml /opt/jboss/keycloak/standalone/configuration/standalone.xml
COPY ../../rd-ms-keycloak-customization/src/main/config/standalone-ha.xml /opt/jboss/keycloak/standalone/configuration/standalone-ha.xml
