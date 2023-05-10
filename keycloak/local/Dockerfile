FROM sleighzy/keycloak:16.1.0-arm64
#ENV KEYCLOAK_USER=raiden \
#    KEYCLOAK_PASSWORD=brutality
#    KEYCLOAK_IMPORT=/tmp/master.json
#    KEYCLOAK_WELCOME_THEME=
#    KEYCLOAK_DEFAULT_THEME=

#https://www.baeldung.com/spring-keycloak-custom-themes
#COPY themes /opt/jboss/keycloak/themes


# kcadm.sh config credentials --server http://localhost:8080/auth --realm master --user raiden brutality

# Deploy Keycloak custom themes
## Configure Custom Welcome theme
ENV KEYCLOAK_WELCOME_THEME=radien
## Disable Theme Chacing
RUN sed -i -E "s/(<staticMaxAge>)2592000(<\/staticMaxAge>)/\1\-1\2/" /opt/jboss/keycloak/standalone/configuration/standalone.xml
RUN sed -i -E "s/(<cacheThemes>)true(<\/cacheThemes>)/\1false\2/" /opt/jboss/keycloak/standalone/configuration/standalone.xml
RUN sed -i -E "s/(<cacheTemplates>)true(<\/cacheTemplates>)/\1false\2/" /opt/jboss/keycloak/standalone/configuration/standalone.xml
## Copy Built custom theme
COPY ../../rd-ms-keycloak-themes/target/keycloak-custom-themes-jar-with-dependencies.jar /opt/jboss/keycloak/standalone/deployments/