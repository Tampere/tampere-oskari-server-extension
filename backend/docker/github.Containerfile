FROM docker.io/library/jetty:9.4-jre17-alpine

COPY webapp-map/target/oskari-map/WEB-INF/lib/postgresql-*.jar lib/
COPY ./docker/log4j2.xml lib/

# COPY ./docker/oskari-ext.properties resources/

ARG OSKARI_CONTEXT="oskari"
ARG FRONTEND_VERSION
RUN test -n "$FRONTEND_VERSION"

COPY webapp-map/target/oskari-map.war webapps/${OSKARI_CONTEXT}.war

# Oskari configurations we can apply at build time ( path, etc )
ENV OSKARI_OSKARI_AJAX_URL_PREFIX="/${OSKARI_CONTEXT}/action?"
ENV OSKARI_OSKARI_CLIENT_VERSION="dist/${FRONTEND_VERSION}"
ENV OSKARI_OSKARI_CLIENT_DOMAIN="http://localhost:8080/${OSKARI_CONTEXT}"
