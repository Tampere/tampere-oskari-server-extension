FROM docker.io/library/tomcat:10-jdk17

COPY webapp-map/target/oskari-map/WEB-INF/lib/postgresql-*.jar lib/
COPY ./docker/log4j2.xml lib/

# COPY ./docker/oskari-ext.properties resources/

ARG OSKARI_CONTEXT="oskari"
# This should match the context, uness context is ROOT, when this should be '/''
ARG OSKARI_PATH="/${OSKARI_CONTEXT}"
ARG FRONTEND_VERSION
RUN test -n "$FRONTEND_VERSION"

COPY webapp-map/target/oskari-map.war webapps/${OSKARI_CONTEXT}.war

# Oskari configurations we can apply at build time ( path, etc )
ENV OSKARI_OSKARI_AJAX_URL_PREFIX="${OSKARI_PATH}/action?"
ENV OSKARI_OSKARI_CLIENT_VERSION="dist/${FRONTEND_VERSION}"
ENV OSKARI_OSKARI_CLIENT_DOMAIN="${OSKARI_PATH}"
