FROM docker.io/library/tomcat:10-jdk17

COPY webapp-map/target/oskari-map/WEB-INF/lib/postgresql-*.jar lib/
COPY ./docker/log4j2.xml lib/
COPY ./docker/server.xml conf/server.xml

# Delete Tomcat's own dated logs (catalina.*, localhost.*, ...) after 30 days.
# Drop the default maxDays lines first, otherwise the later default (90) wins.
RUN sed -i -E -e '/^[0-9]+[a-z-]+\.org\.apache\.juli\.(Async)?FileHandler\.maxDays *=/d' \
      -e 's/^([0-9]+[a-z-]+\.org\.apache\.juli\.(Async)?FileHandler)\.directory = .*/&\n\1.maxDays = 30/' \
      conf/logging.properties

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
